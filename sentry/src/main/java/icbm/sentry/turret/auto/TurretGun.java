package icbm.sentry.turret.auto;

import icbm.sentry.turret.block.TileTurret;
import icbm.sentry.turret.weapon.WeaponSystemProjectile;
import universalelectricity.api.vector.Vector3;

/** @author DarkGuardsman */
public class TurretGun extends SentryAuto
{
    public TurretGun(TileTurret host)
    {
        super(host);
        this.weaponSystem = new WeaponSystemProjectile(this, 5);
        this.centerOffset.y = 0.65;
    }
}
