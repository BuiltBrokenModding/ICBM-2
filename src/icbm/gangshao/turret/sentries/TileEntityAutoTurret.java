package icbm.gangshao.turret.sentries;

import icbm.api.IMissile;
import icbm.api.sentry.AmmoPair;
import icbm.api.sentry.IAATarget;
import icbm.api.sentry.IAmmo;
import icbm.api.sentry.IAutoSentry;
import icbm.api.sentry.ProjectileTypes;
import icbm.gangshao.ZhuYaoGangShao;
import icbm.gangshao.actions.ActionIdle;
import icbm.gangshao.actions.ActionKillTarget;
import icbm.gangshao.actions.ActionManager;
import icbm.gangshao.actions.ActionRepeat;
import icbm.gangshao.actions.ActionRotateTo;
import icbm.gangshao.actions.ActionSearchTarget;
import icbm.gangshao.turret.TileEntityTurretBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.INpc;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import universalelectricity.core.vector.Vector3;

/**
 * Extend this class for all turrets that are automatic.
 * 
 * @author Rseifert
 * 
 */
public abstract class TileEntityAutoTurret extends TileEntityTurretBase implements IAutoSentry
{
	/** The target this turret is hitting. */
	public Entity target;

	public boolean targetPlayers = true;
	public boolean targetLiving = true;
	public boolean targetCrafts = false;
	public boolean targetMissiles = false;

	public final ActionManager AIManager = new ActionManager();

	@Override
	public void initiate()
	{
		if (!this.worldObj.isRemote)
		{
			this.AIManager.addCommand(this, ActionSearchTarget.class);
			this.AIManager.addCommand(this, ActionKillTarget.class);
			this.AIManager.addCommand(this, ActionRepeat.class);
		}
	}

	@Override
	public void onUpdate()
	{
		if (!this.worldObj.isRemote)
		{
			this.speedUpRotation = this.target != null;
		}
		this.AIManager.onUpdate();
		/**
		 * Only update the action manager for idle movements if the target is invalid.
		 */
		if (this.target == null && !this.worldObj.isRemote)
		{
			this.actionManager.onUpdate();

			if (!this.actionManager.hasTasks())
			{
				this.actionManager.clear();
				this.actionManager.addCommand(this, ActionRotateTo.class, new String[] { "" + (this.worldObj.rand.nextInt(60) + 30), "0" });
				this.actionManager.addCommand(this, ActionIdle.class, new String[] { "" + (this.worldObj.rand.nextInt(50) + 10) });

				this.actionManager.addCommand(this, ActionRotateTo.class, new String[] { "" + (-this.worldObj.rand.nextInt(60) - 30), "0" });
				this.actionManager.addCommand(this, ActionIdle.class, new String[] { "" + (this.worldObj.rand.nextInt(50) + 10) });

				this.actionManager.addCommand(this, ActionRotateTo.class, new String[] { "" + (this.worldObj.rand.nextInt(60) + 30), "0" });
				this.actionManager.addCommand(this, ActionIdle.class, new String[] { "" + (this.worldObj.rand.nextInt(50) + 10) });
			}
		}

	}

	@Override
	public AxisAlignedBB getTargetingBox()
	{
		return AxisAlignedBB.getBoundingBox(xCoord - this.getDetectRange(), yCoord - 5, zCoord - this.getDetectRange(), xCoord + this.getDetectRange(), yCoord + 5, zCoord + this.getDetectRange());
	}

	@Override
	public Entity getTarget()
	{
		return this.target;
	}

	@Override
	public boolean setTarget(Entity target, boolean override)
	{
		if (this.target == null)
		{
			this.target = target;
			return true;
		}
		else if (override)
		{
			this.target = target;
			return true;
		}
		return false;
	}

	@Override
	public boolean isValidTarget(Entity entity)
	{
		if (entity != null)
		{
			if (!entity.isDead && !entity.isEntityInvulnerable())
			{
				if (entity.getDistance(this.xCoord, this.yCoord, this.zCoord) < this.getDetectRange())
				{
					if (!this.lookHelper.canEntityBeSeen(entity))
					{
						return false;
					}

					if (entity instanceof EntityPlayer || entity.riddenByEntity instanceof EntityPlayer)
					{
						if(this.targetPlayers)
						{
							return false;
						}
						EntityPlayer player;
						if (entity.riddenByEntity instanceof EntityPlayer)
						{
							player = (EntityPlayer) entity.riddenByEntity;
						}
						else
						{
							player = ((EntityPlayer) entity);
						}

						if (player.capabilities.isCreativeMode)
						{
							return false;
						}

						if (this.getPlatform() != null && this.getPlatform().canUserAccess(player.username))
						{
							return false;
						}
					}
					else if (entity instanceof IMissile)
					{
						if(this.targetMissiles)
						{
							return false;
						}
						IMissile missile = (IMissile) entity;
						if (missile.getTicksInAir() > 0)
						{
							return true;
						}
						return false;
					}
					else if (entity instanceof IAATarget)
					{
						if(this.targetCrafts || !((IAATarget) entity).canBeTargeted(this))
						{
							return false;
						}
						return true;
					}
					else if (entity instanceof EntityLiving && !(entity instanceof EntityAmbientCreature))
					{
						if (this.targetCrafts && entity instanceof EntityFlying)
						{
							if (entity instanceof IMob)
							{
								return true;
							}
							if(entity instanceof IBossDisplayData)
							{
								return true;
							}
						}
						if (this.targetLiving)
						{
							if (entity instanceof IMob)
							{
								return true;
							}
							if (entity instanceof IAnimals || entity instanceof INpc || entity instanceof IMerchant)
							{
								return false;
							}
						}
					}
					else
					{
						return false;
					}
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public boolean canActivateWeapon()
	{
		if (this.isValidTarget(this.target) && this.getPlatform() != null)
		{
			if (!this.lookHelper.isLookingAt(this.target, 10f))
			{
				this.lookHelper.lookAtEntity(this.target);
				return false;
			}
			else
			{
				return this.ticks % this.getCooldown() == 0 && (this.getPlatform().wattsReceived >= this.getRequest()) && this.getPlatform().hasAmmunition(ProjectileTypes.CONVENTIONAL) != null;
			}
		}

		return false;
	}

	@Override
	public void onWeaponActivated()
	{
		onFire();
	}

	protected boolean onFire()
	{
		AmmoPair<IAmmo, ItemStack> ammo = this.getPlatform().hasAmmunition(ProjectileTypes.CONVENTIONAL);

		if (this.getPlatform() != null && ammo != null)
		{
			boolean fired = false;

			if (this.target instanceof EntityLiving)
			{
				this.getPlatform().wattsReceived -= this.getRequest();
				ammo.getAmmo().attackTargetLiving(ammo.getStack().getItemDamage(), this, this.target, true);
				fired = true;
			}
			else if (this.target instanceof IMissile)
			{
				if (this.worldObj.rand.nextFloat() > 0.3)
				{
					((IMissile) this.target).normalExplode();
				}

				fired = true;
			}
			else if (this.target instanceof IAATarget)
			{
				if (this.worldObj.rand.nextFloat() > 0.1)
				{
					int damage = ((IAATarget) this.target).doDamage(10);
					if (damage == -1 && this.worldObj.rand.nextFloat() > 0.7)
					{
						((IAATarget) this.target).destroyCraft();
					}
					else if (damage < 0)
					{
						((IAATarget) this.target).destroyCraft();
					}
				}
				fired = true;
			}

			if (fired)
			{
				if (!this.worldObj.isRemote && this.worldObj.rand.nextFloat() < 0.9)
				{
					Vector3 spawnPos = this.getMuzzle();
					EntityItem entityShell = new EntityItem(this.worldObj, spawnPos.x, spawnPos.y, spawnPos.z, ZhuYaoGangShao.bulletShell.copy());
					entityShell.delayBeforeCanPickup = 20;
					this.worldObj.spawnEntityInWorld(entityShell);
				}
				if (ammo.getAmmo().consumeItem(ammo.getStack().getItemDamage()))
				{
					this.getPlatform().useAmmunition(ammo.getStack());
				}
			}

			return fired;
		}

		return false;
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);

		nbt.setBoolean("shootPlayers", this.targetPlayers);
		nbt.setBoolean("shootCraft", this.targetCrafts);
		nbt.setBoolean("shootLiving", this.targetLiving);
		nbt.setBoolean("shootAir", this.targetMissiles);

	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		if (nbt.hasKey("shootPlayers"))
		{
			this.targetPlayers = nbt.getBoolean("shootPlayers");
		}
		if (nbt.hasKey("shootCraft"))
		{
			this.targetCrafts = nbt.getBoolean("shootCraft");
		}
		if (nbt.hasKey("shootLiving"))
		{
			this.targetLiving = nbt.getBoolean("shootLiving");
		}
		if (nbt.hasKey("shootAir"))
		{
			this.targetMissiles = nbt.getBoolean("shootAir");
		}
	}
}
