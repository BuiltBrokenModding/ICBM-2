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
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import universalelectricity.core.vector.Vector3;
import dark.library.helpers.Pair;

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

	public boolean targetPlayers = false;
	public boolean targetAir = false;
	public boolean targetHostile = false;
	public boolean targetFriendly = false;

	public final ActionManager AIManager = new ActionManager();

	public int baseTargetRange = 20;
	public int maxTargetRange = 90;

	public float idleRtSpeed = 2f;
	public float targetRtSpeed = 6f;

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
					if (this.lookHelper.canEntityBeSeen(entity))
					{
						if (this.targetAir)
						{
							if (entity instanceof IMob && entity instanceof EntityFlying)
							{
								return true;
							}

							if (entity instanceof IAATarget && ((IAATarget) entity).canBeTargeted(this))
							{
								return true;
							}
						}

						if (this.targetPlayers)
						{
							if (entity instanceof EntityPlayer || entity.riddenByEntity instanceof EntityPlayer)
							{
								EntityPlayer player;

								if (entity.riddenByEntity instanceof EntityPlayer)
								{
									player = (EntityPlayer) entity.riddenByEntity;
								}
								else
								{
									player = ((EntityPlayer) entity);
								}

								if (!player.capabilities.isCreativeMode)
								{
									if (this.getPlatform() != null && !this.getPlatform().canUserAccess(player.username))
									{
										return true;
									}
								}
							}
						}

						if (this.targetHostile)
						{
							if (entity instanceof IMob)
							{
								return true;
							}
						}

						if (this.targetFriendly)
						{
							if (entity instanceof IAnimals || entity instanceof INpc || entity instanceof IMerchant)
							{
								return false;
							}
						}
					}
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
				return this.ticks % this.getFiringDelay() == 0 && (this.getPlatform().wattsReceived >= this.getFiringRequest()) && this.getPlatform().hasAmmunition(ProjectileTypes.CONVENTIONAL) != null;
			}
		}

		return false;
	}

	@Override
	public void onWeaponActivated()
	{
		if (this.onFire())
		{
			this.playFiringSound();
			this.sendShotToClient(new Vector3(this.target).add(new Vector3(0, this.target.getEyeHeight(), 0)));
		}
	}

	/**
	 * Sound this turrets plays each time its fires
	 */
	public abstract void playFiringSound();

	protected boolean onFire()
	{
		AmmoPair<IAmmo, ItemStack> ammo = this.getPlatform().hasAmmunition(ProjectileTypes.CONVENTIONAL);

		if (this.getPlatform() != null && ammo != null)
		{
			boolean fired = false;
			IAmmo bullet = ammo.getAmmo();
			int meta = ammo.getStack().getItemDamage();
			if (this.target instanceof EntityLiving)
			{
				this.getPlatform().wattsReceived -= this.getFiringRequest();

				if (bullet.applyDirectDamage(meta))
				{
					Pair<DamageSource, Integer> damage = bullet.getDamage(this.target, this.getDamageEntity(), meta);
					if (damage != null && damage.getKey() != null && damage.getValue() > 0)
					{
						this.target.attackEntityFrom(damage.getKey(), damage.getValue());
						fired = true;
					}

				}
				else
				{
					if (!bullet.fireAmmoLiving(target, meta))
					{
						fired = bullet.fireAmmoLoc(this.worldObj, new Vector3(target), meta);
					}
					else
					{
						fired = true;
					}
				}

			}
			else if (this.target instanceof IMissile)
			{
				if (this.worldObj.rand.nextFloat() > 0.21)
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
				if (!this.worldObj.isRemote && this.worldObj.rand.nextFloat() < 0.95)
				{
					Vector3 spawnPos = this.getMuzzle();
					EntityItem entityShell = new EntityItem(this.worldObj, spawnPos.x, spawnPos.y, spawnPos.z, ZhuYaoGangShao.bulletShell.copy());
					entityShell.delayBeforeCanPickup = 20;
					this.worldObj.spawnEntityInWorld(entityShell);
				}
				this.setHeat(this.getHeatPerShot(), true);
				this.getPlatform().useAmmunition(ammo);
			}

			return fired;
		}

		return false;
	}

	public abstract double getHeatPerShot();

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);

		nbt.setBoolean("targetPlayers", this.targetPlayers);
		nbt.setBoolean("targetAir", this.targetAir);
		nbt.setBoolean("targetHostile", this.targetHostile);
		nbt.setBoolean("targetFriendly", this.targetFriendly);

	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);

		if (nbt.hasKey("targetPlayers"))
		{
			this.targetPlayers = nbt.getBoolean("targetPlayers");
		}
		if (nbt.hasKey("targetAir"))
		{
			this.targetAir = nbt.getBoolean("targetAir");
		}
		if (nbt.hasKey("targetHostile"))
		{
			this.targetHostile = nbt.getBoolean("targetHostile");
		}
		if (nbt.hasKey("targetFriendly"))
		{
			this.targetFriendly = nbt.getBoolean("targetFriendly");
		}

	}

	@Override
	public double getDetectRange()
	{
		if (this.getPlatform() != null)
		{
			return this.baseTargetRange + Math.max(this.baseTargetRange * this.getPlatform().getUpgradePercent("TargetRange"), this.maxTargetRange);
		}
		return this.baseTargetRange;
	}

	@Override
	public float getRotationSpeed()
	{
		if (this.speedUpRotation)
		{
			return Math.max(this.targetRtSpeed + (this.targetRtSpeed * this.getPlatform().getUpgradePercent("TargetSpeed")), 30f);
		}
		return this.idleRtSpeed;
	}
}
