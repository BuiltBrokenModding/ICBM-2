package icbm.sentry.turret.mount;

import net.minecraft.entity.player.EntityPlayer;
import icbm.sentry.turret.EntityMountableDummy;
import icbm.sentry.turret.Turret;
import icbm.sentry.turret.block.TileTurret;
import universalelectricity.api.vector.Vector3;

/** this sentry uses for mounting the player in position */
public class MountedTurret extends Turret
{
	protected Vector3 riderOffset = new Vector3(0.5, 1.2, 0.5);

	public MountedTurret(TileTurret host)
	{
		super(host);
	}

	@Override
	public void update()
	{
		super.update();

		/*
		boolean flag = false;
		if (this.hasWorldObj() && (this.sentryEntity == null || this.sentryEntity.isDead))
		{
			this.sentryEntity = new EntityMountableDummy(this, true);
			this.worldObj.spawnEntityInWorld(this.sentryEntity);
			flag = true;
		}

		// TODO set up handling for non-player entities, low Priority
		if (flag && this.sentryEntity.riddenByEntity instanceof EntityPlayer)
		{
			EntityPlayer mountedPlayer = (EntityPlayer) this.sentryEntity.riddenByEntity;

			if (mountedPlayer.rotationPitch > this.getPitchServo().upperLimit())
			{
				mountedPlayer.rotationPitch = this.getPitchServo().upperLimit();
			}
			if (mountedPlayer.rotationPitch < this.getPitchServo().lowerLimit())
			{
				mountedPlayer.rotationPitch = this.getPitchServo().lowerLimit();
			}
			this.getPitchServo().setRotation(mountedPlayer.rotationPitch);
			this.getYawServo().setRotation(mountedPlayer.rotationYaw);
		}*/
	}

	public Vector3 getRiderOffset()
	{
		return this.riderOffset;
	}

	@Override
	public boolean mountable()
	{
		return true;
	}

	@Override
	public boolean fire(Vector3 vector3)
	{
		return false;
	}

}
