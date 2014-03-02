package icbm.sentry.turret.auto;

import universalelectricity.api.vector.Vector3;
import icbm.sentry.ICBMSentry;
import icbm.sentry.turret.block.TileTurret;
import icbm.sentry.turret.weapon.WeaponTwinLaser;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class TurretLaser extends AutoSentry
{
	/** Laser turret spins its barrels every shot. */
	public float barrelRotation;
	public float barrelRotationVelocity;

	public TurretLaser(TileTurret host)
	{
		super(host);
		maxHealth = 50;
		weaponSystem = new WeaponTwinLaser(this, 15);
		barrelLength = 1.2f;
	}

	@Override
	public void updateEntity()
	{
		super.updateEntity();

		Vector3 aim = new Vector3(this.getHost().x(), this.getHost().y(), this.getHost().z()).add(getAimOffset());
		getHost().world().spawnParticle("smoke", aim.x, aim.y, aim.z, 0, 0, 0);

		if (this.world().isRemote)
		{
			this.barrelRotation = MathHelper.wrapAngleTo180_float(this.barrelRotation + this.barrelRotationVelocity);
			this.barrelRotationVelocity = Math.max(this.barrelRotationVelocity - 0.1f, 0);
		}
	}
}
