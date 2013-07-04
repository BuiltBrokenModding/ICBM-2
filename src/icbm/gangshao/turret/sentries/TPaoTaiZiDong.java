package icbm.gangshao.turret.sentries;

import icbm.api.IMissile;
import icbm.api.sentry.AmmoPair;
import icbm.api.sentry.IAATarget;
import icbm.api.sentry.IAmmo;
import icbm.api.sentry.IAutoSentry;
import icbm.api.sentry.ProjectileTypes;
import icbm.core.Pair;
import icbm.gangshao.ZhuYaoGangShao;
import icbm.gangshao.task.TaskSearchTarget;
import icbm.gangshao.task.TaskManager;
import icbm.gangshao.turret.TPaoDaiBase;
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
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import universalelectricity.core.vector.Vector3;

/**
 * Extend this class for all turrets that are automatic.
 * 
 * @author Rseifert
 */
public abstract class TPaoTaiZiDong extends TPaoDaiBase implements IAutoSentry
{
	/** CURRENT TARGET TO ATTACK */
	public Entity target;

	/** SHOULD TARGET PLAYERS */
	public boolean targetPlayers = false;
	/** SHOULD TARGET FLYING OBJECTS -> MISSILES, PLANES */
	public boolean targetAir = false;
	/** SHOULD TARGET MONSTERS */
	public boolean targetHostile = false;
	/** SHOULD TARGET ANIMALS, NPCS, SHEEP :( */
	public boolean targetFriendly = false;

	/** AI MANAGER */
	public final TaskManager taskManager = new TaskManager(this);

	/** DEFAULT TARGETING RANGE */
	public int baseTargetRange = 20;

	/** MAX TARGETING RANGE */
	public int maxTargetRange = 90;

	/** IDLE ROTATION SPEED */
	public float idleRtSpeed = 2f;

	/** ATTACKING ROTAION SPEED */
	public float targetRtSpeed = 6f;

	/** MAIN AMMO TYPE */
	public ProjectileTypes baseAmmoType = ProjectileTypes.CONVENTIONAL;

	@Override
	public void updateEntity()
	{
		super.updateEntity();

		this.taskManager.onUpdate();

		if (!this.taskManager.hasTasks())
		{
			this.taskManager.addTask(new TaskSearchTarget());
		}

		if (this.isRunning())
		{
			this.updateRotation();
		}
	}

	/** Adjusts the turret's rotation to its target rotation over time. */
	public void updateRotation()
	{
		if (Math.abs(this.currentRotationYaw - this.wantedRotationYaw) > 0.001f)
		{System.out.println("ROTATING YAW");
			float speedYaw;
			if (this.currentRotationYaw > this.wantedRotationYaw)
			{
				speedYaw = -this.getRotationSpeed();
			}
			else
			{
				speedYaw = this.getRotationSpeed();
			}

			this.currentRotationYaw += speedYaw;

			if (Math.abs(this.currentRotationYaw - this.wantedRotationYaw) < this.getRotationSpeed() + 0.1f)
			{
				this.currentRotationYaw = this.wantedRotationYaw;
			}
		}

		if (Math.abs(this.currentRotationPitch - this.wantedRotationPitch) > 0.001f)
		{
			float speedPitch;
			if (this.currentRotationPitch > this.wantedRotationPitch)
			{
				speedPitch = -this.getRotationSpeed();
			}
			else
			{
				speedPitch = this.getRotationSpeed();
			}

			this.currentRotationPitch += speedPitch;

			if (Math.abs(this.currentRotationPitch - this.wantedRotationPitch) < this.getRotationSpeed() + 0.1f)
			{
				this.currentRotationPitch = this.wantedRotationPitch;
			}
		}

		if (Math.abs(this.currentRotationPitch - this.wantedRotationPitch) <= 0.001f && Math.abs(this.currentRotationYaw - this.wantedRotationYaw) < 0.001f)
		{
			this.isRotating = false;
		}

		/** Wraps all the angels and cleans them up. */
		this.currentRotationPitch = MathHelper.wrapAngleTo180_float(this.currentRotationPitch);
		this.wantedRotationYaw = MathHelper.wrapAngleTo180_float(this.wantedRotationYaw);
		this.wantedRotationPitch = MathHelper.wrapAngleTo180_float(this.wantedRotationPitch);
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
		if (!this.isValidTarget(this.target) || override)
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
			if (this.lookHelper.isLookingAt(this.target, 10f))
			{
				return this.tickSinceFired == 0 && (this.getPlatform().wattsReceived >= this.getFiringRequest()) && this.getPlatform().hasAmmunition(this.baseAmmoType) != null;
			}
		}

		return false;
	}

	@Override
	public void onWeaponActivated()
	{
		super.onWeaponActivated();
		if (this.onFire())
		{
			Vector3 target = new Vector3(this.target).add(new Vector3(0, this.target.getEyeHeight(), 0));
			if (!this.worldObj.isRemote)
			{
				this.sendShotToClient(target);
			}
			else
			{
				this.renderShot(target);
				this.playFiringSound();
			}
		}
	}

	@Override
	public void renderShot(Vector3 target)
	{
		this.drawParticleStreamTo(target);
	}

	/** Does the actual firing process for the sentry */
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

				if (bullet.isDirectDamage(ammo.getStack()))
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
				this.getPlatform().useAmmunition(ammo);
			}

			return fired;
		}

		return false;
	}

	/** Writes a tile entity to NBT. */
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
			return this.baseTargetRange + Math.min(this.baseTargetRange * this.getPlatform().getUpgradePercent("TargetRange"), this.maxTargetRange);
		}
		return this.baseTargetRange;
	}

	public float getRotationSpeed()
	{
		if (this.speedUpRotation)
		{
			return Math.min(this.targetRtSpeed + (this.targetRtSpeed * this.getPlatform().getUpgradePercent("TargetSpeed")), 30f);
		}
		return this.idleRtSpeed;
	}

	@Override
	public boolean canApplyPotion(PotionEffect par1PotionEffect)
	{
		// TODO Auto-generated method stub
		return false;
	}
}
