package icbm.sentry.turret.auto;

import icbm.sentry.turret.block.TileTurret;
import icbm.sentry.turret.weapon.WeaponTwinLaser;
import net.minecraft.util.MathHelper;
import universalelectricity.api.energy.EnergyStorageHandler;

public class TurretLaser extends TurretAuto
{
	/** Laser turret spins its barrels every shot. */
	public float barrelRotation;
	public float barrelRotationVelocity;

	public TurretLaser(TileTurret host)
	{
		super(host);
		energy = new EnergyStorageHandler(100000);
		maxHealth = 50;
		weaponSystem = new WeaponTwinLaser(this, 3);
		barrelLength = 1.2f;
		default_target_range = 50;
		maxCooldown = 15;
	}

	@Override
	public void update()
	{
		super.update();

		if (this.world().isRemote)
		{
			this.barrelRotation = MathHelper.wrapAngleTo180_float(this.barrelRotation + this.barrelRotationVelocity);
			this.barrelRotationVelocity = Math.max(this.barrelRotationVelocity - 0.1f, 0);
		}
	}
}
