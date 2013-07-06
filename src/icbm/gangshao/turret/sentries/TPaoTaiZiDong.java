package icbm.gangshao.turret.sentries;

import icbm.api.sentry.IAATarget;
import icbm.gangshao.IAmmunition;
import icbm.gangshao.IAutoSentry;
import icbm.gangshao.ProjectileType;
import icbm.gangshao.ZhuYaoGangShao;
import icbm.gangshao.damage.TileDamageSource;
import icbm.gangshao.task.TaskManager;
import icbm.gangshao.task.TaskSearchTarget;
import icbm.gangshao.turret.TPaoDaiBase;
import icbm.gangshao.turret.upgrades.ItPaoTaiUpgrades.TurretUpgradeType;

import java.io.IOException;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.INpc;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.network.PacketManager;

import com.google.common.io.ByteArrayDataInput;

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

	public boolean canTargetAir = false;

	/** AI MANAGER */
	public final TaskManager taskManager = new TaskManager(this);

	/** DEFAULT TARGETING RANGE */
	public int baseTargetRange = 20;

	/** MAX TARGETING RANGE */
	public int maxTargetRange = 90;

	/** IDLE ROTATION SPEED */
	public float rotationSpeed = 3;

	/** MAIN AMMO TYPE */
	public ProjectileType projectileType = ProjectileType.CONVENTIONAL;

	@Override
	public void onReceivePacket(int packetID, EntityPlayer player, ByteArrayDataInput dataStream) throws IOException
	{
		super.onReceivePacket(packetID, player, dataStream);

		if (packetID == TurretPacketType.SHOT.ordinal())
		{
			this.renderShot(new Vector3(dataStream.readDouble(), dataStream.readDouble(), dataStream.readDouble()));
			this.currentRotationPitch = dataStream.readFloat();
			this.currentRotationYaw = dataStream.readFloat();
			this.playFiringSound();
		}
	}

	@Override
	public void updateEntity()
	{
		super.updateEntity();

		if (!this.worldObj.isRemote && this.isRunning())
		{
			this.taskManager.onUpdate();

			if (!this.taskManager.hasTasks())
			{
				this.taskManager.addTask(new TaskSearchTarget());
			}
		}
	}

	public float getRotationSpeed()
	{
		return this.rotationSpeed;
	}

	@Override
	public AxisAlignedBB getTargetingBox()
	{
		return AxisAlignedBB.getBoundingBox(xCoord - this.getDetectRange(), this.yCoord - 5, zCoord - this.getDetectRange(), xCoord + this.getDetectRange(), yCoord + 5, zCoord + this.getDetectRange());
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

	@Override
	public boolean isValidTarget(Entity entity)
	{
		if (entity != null)
		{
			if (!entity.isDead && !entity.isEntityInvulnerable())
			{
				if (this.getCenter().distanceTo(new Vector3(entity)) <= this.getDetectRange())
				{
					float[] rotations = this.lookHelper.getDeltaRotations(new Vector3(entity).add(new Vector3(0, entity.getEyeHeight(), 0)));

					if ((rotations[1] <= this.maxPitch && rotations[1] >= this.minPitch) || this.allowFreePitch)
					{
						if (this.lookHelper.canEntityBeSeen(entity))
						{
							if (this.targetAir && this.canTargetAir)
							{
								if (this.isAir(entity))
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
								if (entity instanceof IMob && !this.isAir(entity))
								{
									return true;
								}
							}

							if (this.targetFriendly)
							{
								if ((entity instanceof IAnimals || entity instanceof INpc || entity instanceof IMerchant) && !this.isAir(entity))
								{
									return true;
								}
							}
						}
					}
				}
			}
		}

		return false;
	}

	protected boolean isAir(Entity entity)
	{
		return (entity instanceof IMob && entity instanceof EntityFlying) || (entity instanceof IAATarget && ((IAATarget) entity).canBeTargeted(this)) || entity instanceof EntityWither || entity instanceof EntityDragon;
	}

	@Override
	public boolean canActivateWeapon()
	{
		if (this.isValidTarget(this.target) && this.getPlatform() != null)
		{
			if (this.lookHelper.isLookingAt(this.target, 5))
			{
				return this.tickSinceFired == 0 && (this.getPlatform().wattsReceived >= this.getFiringRequest()) && (this.getPlatform().hasAmmunition(this.projectileType) != null || this.projectileType == ProjectileType.UNKNOWN);
			}
		}

		return false;
	}

	/**
	 * Sends the firing info to the client to render tracer effects
	 */
	public void sendShotToClient(Vector3 position)
	{
		PacketManager.sendPacketToClients(PacketManager.getPacket(ZhuYaoGangShao.CHANNEL, this, TurretPacketType.SHOT.ordinal(), position.x, position.y, position.z, this.currentRotationPitch, this.currentRotationYaw), this.worldObj, new Vector3(this), 40);
	}

	/**
	 * Server side only
	 */
	@Override
	public void onWeaponActivated()
	{
		super.onWeaponActivated();

		if (!this.worldObj.isRemote)
		{
			if (this.onFire())
			{
				this.sendShotToClient(this.getTargetPosition());
				this.playFiringSound();
				this.getPlatform().wattsReceived = Math.max(this.getPlatform().wattsReceived - this.getFiringRequest(), 0);
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
		if (!this.worldObj.isRemote)
		{
			ItemStack ammoStack = this.getPlatform().hasAmmunition(this.projectileType);

			if (this.getPlatform() != null && ammoStack != null)
			{
				boolean fired = false;
				IAmmunition bullet = (IAmmunition) ammoStack.getItem();

				if (this.target instanceof EntityLiving)
				{
					this.getPlatform().wattsReceived -= this.getFiringRequest();

					if (bullet.getType(ammoStack) == ProjectileType.CONVENTIONAL)
					{
						this.target.attackEntityFrom(TileDamageSource.doBulletDamage(this), bullet.getDamage());
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
					if (this.worldObj.rand.nextFloat() < 0.95)
					{
						Vector3 spawnPos = this.getMuzzle();
						EntityItem entityShell = new EntityItem(this.worldObj, spawnPos.x, spawnPos.y, spawnPos.z, ZhuYaoGangShao.bulletShell.copy());
						entityShell.delayBeforeCanPickup = 20;
						this.worldObj.spawnEntityInWorld(entityShell);
					}

					this.getPlatform().useAmmunition(ammoStack);
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
			return Math.min((float) (this.baseTargetRange + (this.maxTargetRange - this.baseTargetRange) * (float) ((float) this.getPlatform().getUpgradeCount(TurretUpgradeType.RANGE) / 64f)), this.maxTargetRange);
		}

		return this.baseTargetRange;
	}

	@Override
	public boolean canApplyPotion(PotionEffect par1PotionEffect)
	{
		return false;
	}

	/**
	 * @return Gets the target position accounting target's height.
	 */
	public Vector3 getTargetPosition()
	{
		if (this.getTarget() != null)
		{
			return new Vector3(this.getTarget()).add(new Vector3(0, this.getTarget().getEyeHeight(), 0));
		}

		return null;
	}
}
