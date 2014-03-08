package icbm.sentry.turret.mounted;

import icbm.sentry.interfaces.ITurretProvider;
import icbm.sentry.turret.EntityMountableDummy;
import icbm.sentry.turret.Turret;
import net.minecraft.entity.player.EntityPlayer;
import universalelectricity.api.vector.EulerAngle;
import universalelectricity.api.vector.Vector3;

/** this sentry uses for mounting the player in position */
public abstract class TurretMounted extends Turret
{
	protected Vector3 riderOffset = new Vector3(0.5, 1.2, 0.5);

	public TurretMounted(ITurretProvider turretProvider)
	{
		super(turretProvider);
	}

	@Override
	public void update()
	{
		super.update();

		if (!world().isRemote)
		{
			EntityMountableDummy sentryEntity = getHost().getFakeEntity();

			if (sentryEntity.riddenByEntity instanceof EntityPlayer)
			{
				EntityPlayer mountedPlayer = (EntityPlayer) sentryEntity.riddenByEntity;
				getServo().yaw = -mountedPlayer.rotationYaw + 180;
				getServo().pitch = -mountedPlayer.rotationPitch;
			}
		}
	}

	public Vector3 getRiderOffset()
	{
		return riderOffset;
	}

	@Override
	public boolean mountable()
	{
		return true;
	}
}
