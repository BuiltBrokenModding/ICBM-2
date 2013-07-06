package icbm.gangshao.turret.mount;

import icbm.gangshao.turret.TPaoDaiBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MovingObjectPosition;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.multiblock.IMultiBlock;
import universalelectricity.prefab.network.PacketManager;
import calclavia.lib.CalculationHelper;

/**
 * Mountable Turret
 * 
 * @author Calclavia
 * 
 */
public abstract class TPaoTaiQi extends TPaoDaiBase implements IMultiBlock
{
	/** OFFSET OF BARREL ROTATION */
	public final float barrelOffset = 0.0175f;

	/** Fake entity this sentry uses for mounting the player in position */
	protected EJia entityFake = null;

	@Override
	public void updateEntity()
	{
		super.updateEntity();

		if (this.entityFake != null)
		{
			if (this.entityFake.riddenByEntity instanceof EntityPlayer)
			{
				EntityPlayer mountedPlayer = (EntityPlayer) this.entityFake.riddenByEntity;

				if (mountedPlayer.rotationPitch > this.maxPitch)
				{
					mountedPlayer.rotationPitch = this.maxPitch;
				}
				if (mountedPlayer.rotationPitch < this.minPitch)
				{
					mountedPlayer.rotationPitch = this.minPitch;
				}
				this.currentRotationPitch = this.wantedRotationPitch = mountedPlayer.rotationPitch * barrelOffset;
				this.currentRotationYaw = this.wantedRotationYaw = mountedPlayer.rotationYaw * barrelOffset;
			}/*
			 * else { this.entityFake.setDead(); this.entityFake = null; }
			 */
		}
	}

	/**
	 * Performs a ray trace for the distance specified and using the partial tick time. Args:
	 * distance, partialTickTime
	 */
	public MovingObjectPosition rayTrace(double distance)
	{
		return CalculationHelper.doCustomRayTrace(this.worldObj, this.getMuzzle(), this.wantedRotationYaw / this.barrelOffset, this.wantedRotationPitch / this.barrelOffset, true, distance);
	}

	@Override
	public boolean onActivated(EntityPlayer entityPlayer)
	{
		if (entityPlayer.isSneaking())
		{
			this.tryActivateWeapon();
		}
		else
		{
			if (this.entityFake != null)
			{
				if (this.entityFake.riddenByEntity instanceof EntityPlayer)
				{
					// Unmount
					EntityPlayer mountedPlayer = (EntityPlayer) this.entityFake.riddenByEntity;

					if (entityPlayer == mountedPlayer)
					{
						entityPlayer.unmountEntity(this.entityFake);
						this.entityFake.setDead();
						this.entityFake = null;

						if (!this.worldObj.isRemote)
						{
							PacketManager.sendPacketToClients(this.getRotationPacket());
						}

						return true;
					}
				}

				return false;
			}
			else
			{
				this.mount(entityPlayer);
			}
		}

		return true;
	}

	public void mount(EntityPlayer entityPlayer)
	{
		if (!this.worldObj.isRemote)
		{
			// Creates a fake entity to be mounted on
			if (this.entityFake == null)
			{
				this.entityFake = new EJia(this.worldObj, new Vector3(this.xCoord + 0.5, this.yCoord + 1.2, this.zCoord + 0.5), this, true);
				this.worldObj.spawnEntityInWorld(entityFake);
			}

			entityPlayer.rotationYaw = this.currentRotationYaw / this.barrelOffset;
			entityPlayer.rotationPitch = this.currentRotationPitch / this.barrelOffset;

			entityPlayer.mountEntity(this.entityFake);
		}
	}

	public void tryActivateWeapon()
	{
		if (this.canActivateWeapon())
		{
			this.onWeaponActivated();
		}
	}

	@Override
	public boolean canApplyPotion(PotionEffect par1PotionEffect)
	{
		return false;
	}

}
