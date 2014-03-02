package icbm.sentry.turret.auto;

import icbm.sentry.turret.block.TileTurret;
import icbm.sentry.turret.weapon.WeaponProjectile;

/** @author DarkGuardsman */
public class TurretGun extends TurretAuto
{
	public TurretGun(TileTurret host)
	{
		super(host);
		weaponSystem = new WeaponProjectile(this, 10);
		centerOffset.y = 0.27;
		range = 20;
		maxCooldown = 30;
		barrelLength = 0.7f;
	}
}
