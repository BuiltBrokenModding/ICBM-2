package icbm.gangshao.turret.mount;

import icbm.gangshao.turret.TPaoDaiBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MovingObjectPosition;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.network.PacketManager;
import calclavia.lib.CalculationHelper;
import calclavia.lib.multiblock.IMultiBlock;

/**
 * Mountable Turret
 * 
 * @author Calclavia
 * 
 */
public abstract class TPaoTaiQi extends TPaoDaiBase implements IMultiBlock
{
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
				this.currentRotationPitch = this.wantedRotationPitch = mountedPlayer.rotationPitch;
				this.currentRotationYaw = this.wantedRotationYaw = mountedPlayer.rotationYaw;
			}
		}
	}

	/**
	 * Performs a ray trace for the distance specified and using the partial tick time. Args:
	 * distance, partialTickTime
	 */
	public MovingObjectPosition rayTrace(double distance)
	{
		return CalculationHelper.doCustomRayTrace(this.worldObj, this.getMuzzle(), this.wantedRotationYaw, this.wantedRotationPitch, true, distance);
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
						if (!this.worldObj.isRemote)
						{
							PacketManager.sendPacketToClients(this.getRotationPacket());
						}

						entityPlayer.mountEntity(null);
						this.entityFake.setDead();
						this.entityFake = null;

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
				this.worldObj.spawnEntityInWorld(this.entityFake);
			}

			entityPlayer.rotationYaw = this.currentRotationYaw;
			entityPlayer.rotationPitch = this.currentRotationPitch;

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
