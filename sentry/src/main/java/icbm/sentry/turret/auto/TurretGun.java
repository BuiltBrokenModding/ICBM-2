package icbm.sentry.turret.auto;

import icbm.sentry.interfaces.ITurret;
import icbm.sentry.turret.block.TileTurret;
import icbm.sentry.turret.weapon.types.WeaponConventional;

/** @author DarkGuardsman */
public class TurretGun extends TurretAuto
{
    public TurretGun(TileTurret host)
    {
        super(host);
        weaponSystem = new WeaponConventional(this, 8);
        centerOffset.y = 0.3;
        aimOffset.y = 0.3;
        applyTrait(ITurret.SEARCH_RANGE_TRAIT, 20.0);
        applyTrait(ITurret.MAX_HEALTH_TRAIT, 50.0);
        maxCooldown = 10;
        barrelLength = 1f;
    }
}
