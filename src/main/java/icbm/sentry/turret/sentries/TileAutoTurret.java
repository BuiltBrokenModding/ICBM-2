package icbm.sentry.turret.sentries;

import icbm.api.sentry.IAATarget;
import icbm.core.ICBMCore;
import icbm.sentry.ICBMSentry;
import icbm.sentry.ProjectileType;
import icbm.sentry.damage.TileDamageSource;
import icbm.sentry.interfaces.IAmmunition;
import icbm.sentry.interfaces.IAutoSentry;
import icbm.sentry.task.TaskKillTarget;
import icbm.sentry.task.TaskManager;
import icbm.sentry.task.TaskSearchTarget;
import icbm.sentry.turret.TileTurret;
import icbm.sentry.turret.upgrades.ItemSentryUpgrade.TurretUpgradeType;

import java.io.IOException;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import universalelectricity.api.vector.Vector3;
import calclavia.lib.network.PacketHandler;

import com.google.common.io.ByteArrayDataInput;

/**
 * Extend this class for all turrets that are automatic.
 * 
 * @author Rseifert
 */
public abstract class TileAutoTurret extends TileTurret implements IAutoSentry
{
	/** CURRENT TARGET TO ATTACK */
	public Entity target;

	/** AI MANAGER */
	public final TaskManager taskManager;

	/** DEFAULT TARGETING RANGE */
	public int baseTargetRange = 20;

	/** MAX TARGETING RANGE */
	public int maxTargetRange = 90;

	/** IDLE ROTATION SPEED */
	public float rotationSpeed = 3;

	/** MAIN AMMO TYPE */
	public ProjectileType projectileType = ProjectileType.CONVENTIONAL;

	public int lastRotateTick;

	protected boolean allowFreePitch;

	public TileAutoTurret()
	{
		taskManager = new TaskManager(this);
		this.loadTasks();
	}

	public void loadTasks()
	{
		this.taskManager.addTask(new TaskSearchTarget());
		this.taskManager.addTask(new TaskKillTarget());
	}

	@Override
	public void updateEntity()
	{
		super.updateEntity();
		if (lastRotateTick > 0)
			lastRotateTick--;

		if (!this.worldObj.isRemote)
		{
			this.taskManager.onUpdate();

			if (!this.taskManager.hasTasks())
			{
				this.loadTasks();
			}
		}
	}

	@Override
	public float getRotationSpeed()
	{
		return this.rotationSpeed;
	}

	public AxisAlignedBB getTargetingBox()
	{
		return AxisAlignedBB.getBoundingBox(xCoord - this.maxTargetRange, this.yCoord - 5, zCoord - this.maxTargetRange, xCoord + this.maxTargetRange, yCoord + this.maxTargetRange, zCoord + this.maxTargetRange);
	}

	@Override
	public Entity getTarget()
	{
		return this.target;
	}

	@Override
	public void setTarget(Entity target)
	{
		this.target = target;
	}

	public boolean isValidTarget(Entity entity)
	{
		if (entity != null)
		{
			if (!entity.isDead && !entity.isEntityInvulnerable())
			{
				if (this.pos().distance(new Vector3(entity)) <= this.maxTargetRange)
				{
					float[] rotations = this.lookHelper.getDeltaRotations(new Vector3(entity).translate(new Vector3(0, entity.getEyeHeight(), 0)));

					if ((rotations[1] <= this.getPitchServo().getLimits().left() && rotations[1] >= this.getPitchServo().getLimits().right()) || this.allowFreePitch)
					{
						if (this.lookHelper.canEntityBeSeen(entity))
						{
							if (entity instanceof IMob)
							{
								return true;
							}
						}
					}
				}
			}
		}

		return false;
	}

	public boolean canActivateWeapon()
	{
		if (this.isValidTarget(this.getTarget()))
		{
			if (this.lookHelper.isLookingAt(this.target, 5))
			{
				return this.tickSinceFired == 0;
			}
		}

		return false;
	}

	@Override
	public void onReceivePacket(int id, ByteArrayDataInput data, EntityPlayer player, Object... extra) throws IOException
	{
		if (id == TurretPacketType.SHOT.ordinal())
		{
			Vector3 target = new Vector3(data.readDouble(), data.readDouble(), data.readDouble());
			this.getYawServo().setRotation(data.readFloat());
			this.getYawServo().setRotation(data.readFloat());
			this.drawParticleStreamTo(target);
		}

		super.onReceivePacket(id, data, player, extra);
	}

	/** Sends the firing info to the client to render tracer effects */
	public void sendShotToClient(Vector3 position)
	{
		PacketHandler.sendPacketToClients(ICBMCore.PACKET_TILE.getPacket(this, TurretPacketType.SHOT.ordinal(), this, position, this.getYawServo().getRotation(), this.getPitchServo().getRotation()), this.worldObj, new Vector3(this), 40);
	}

	/** Server side only */
	@Override
	public void onWeaponActivated()
	{
		super.onWeaponActivated();

		if (!this.worldObj.isRemote)
		{
			if (this.onFire())
			{
				this.sendShotToClient(this.getTargetPosition());
			}
		}
	}

	/** Does the actual firing process for the sentry */
	protected boolean onFire()
	{
		if (!this.worldObj.isRemote)
		{
			ItemStack ammoStack = this.getPlatform().hasAmmunition(this.projectileType);

			if (this.getPlatform() != null && (ammoStack != null || this.projectileType == ProjectileType.UNKNOWN))
			{
				boolean fired = false;
				IAmmunition bullet = null;

				if (ammoStack != null)
				{
					bullet = (IAmmunition) ammoStack.getItem();
				}

				if (this.target instanceof EntityLivingBase)
				{
					// this.getPlatform().provideElectricity(ForgeDirection.UP,
					// ElectricityPack.getFromWatts(this.getFiringRequest(), this.getVoltage()),
					// true);

					if (this instanceof TileEntityLaserGun)
					{
						this.target.attackEntityFrom(TileDamageSource.doLaserDamage(this), 2);
					}
					else if (bullet != null)
					{
						if (bullet.getType(ammoStack) == ProjectileType.CONVENTIONAL)
						{
							this.target.attackEntityFrom(TileDamageSource.doBulletDamage(this), bullet.getDamage());
						}
					}

					fired = true;
				}
				else if (this.target instanceof IAATarget)
				{
					if (this.worldObj.rand.nextFloat() > 0.2)
					{
						int damage = ((IAATarget) this.target).doDamage(8);

						if (damage == -1 && this.worldObj.rand.nextFloat() > 0.7)
						{
							((IAATarget) this.target).destroyCraft();
						}
						else if (damage <= 0)
						{
							((IAATarget) this.target).destroyCraft();
						}
					}

					fired = true;
				}

				if (fired && this.projectileType != ProjectileType.UNKNOWN)
				{
					if (this.getPlatform().useAmmunition(ammoStack))
					{
						boolean drop = true;

						if (this.getPlatform().getUpgradeCount(TurretUpgradeType.COLLECTOR) > 0)
						{
							this.getPlatform().damageUpgrade(TurretUpgradeType.COLLECTOR);
							if (this.getPlatform().addStackToInventory(ICBMSentry.bulletShell.copy()))
							{
								drop = false;
							}
						}

						if (drop && this.worldObj.rand.nextFloat() < 0.9)
						{
							Vector3 spawnPos = this.getAimingDirection();
							EntityItem entityShell = new EntityItem(this.worldObj, spawnPos.x, spawnPos.y, spawnPos.z, ICBMSentry.bulletShell.copy());
							entityShell.delayBeforeCanPickup = 20;
							this.worldObj.spawnEntityInWorld(entityShell);
						}
					}

				}

				return fired;
			}
		}

		return false;
	}

	/** Writes a tile entity to NBT. */
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
	}

	@Override
	public boolean canApplyPotion(PotionEffect par1PotionEffect)
	{
		return false;
	}

	/** @return Gets the target position accounting target's height. */
	public Vector3 getTargetPosition()
	{
		if (this.getTarget() != null)
		{
			return new Vector3(this.getTarget()).add(new Vector3(0, this.getTarget().getEyeHeight(), 0));
		}

		return null;
	}
}
