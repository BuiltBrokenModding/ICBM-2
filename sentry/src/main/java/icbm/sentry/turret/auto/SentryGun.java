package icbm.sentry.turret.auto;

import icbm.sentry.turret.block.TileTurret;
import icbm.sentry.turret.weapon.WeaponSystemProjectile;
import universalelectricity.api.vector.Vector3;

/** @author DarkGuardsman */
public class SentryGun extends SentryAuto
{
	public SentryGun(TileTurret host)
	{
		super(host);
		weaponSystem = new WeaponSystemProjectile(this, 5);
		centerOffset.y = 0.65;
		range = 20;
	}
}
