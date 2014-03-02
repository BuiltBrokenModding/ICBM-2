package icbm.sentry.turret.auto;

import universalelectricity.api.vector.Vector3;
import icbm.sentry.ICBMSentry;
import icbm.sentry.turret.block.TileTurret;
import icbm.sentry.turret.weapon.WeaponTwinLaser;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class SentryLaser extends SentryAuto
{
	/** Laser turret spins its barrels every shot. */
	public float barrelRotation;
	public float barrelRotationVelocity;

	public SentryLaser(TileTurret host)
	{
		super(host);
		maxHealth = 50;
		weaponSystem = new WeaponTwinLaser(this, 15);
		barrelLength = 1.2f;
		range = 30;
	}

	@Override
	public void updateEntity()
	{
		super.updateEntity();

		/**
		 * Use to calibrate barrel length
		 * 
		 * <pre>
		 * Vector3 aim = new Vector3(this.getHost().x(), this.getHost().y(), this.getHost().z()).add(getAimOffset());
		 * getHost().world().spawnParticle("smoke", aim.x, aim.y, aim.z, 0, 0, 0);
		 * </pre>
		 */
		
		if (this.world().isRemote)
		{
			this.barrelRotation = MathHelper.wrapAngleTo180_float(this.barrelRotation + this.barrelRotationVelocity);
			this.barrelRotationVelocity = Math.max(this.barrelRotationVelocity - 0.1f, 0);
		}
	}
}
