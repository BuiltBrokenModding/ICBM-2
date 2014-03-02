package icbm.sentry.turret.auto;

import icbm.sentry.turret.block.TileTurret;
import icbm.sentry.turret.weapon.WeaponProjectile;

/** @author DarkGuardsman */
public class TurretGun extends TurretAuto
{
	public TurretGun(TileTurret host)
	{
		super(host);
		weaponSystem = new WeaponProjectile(this, 5);
		centerOffset.y = 0.65;
		range = 20;
	}
}
