package icbm.sentry.turret.auto;

import icbm.sentry.interfaces.ITurret;
import icbm.sentry.interfaces.ITurretUpgrade;
import icbm.sentry.turret.block.TileTurret;
import icbm.sentry.turret.traits.SentryTraitDouble;
import icbm.sentry.turret.weapon.types.WeaponConventional;

/** @author DarkGuardsman */
public class TurretGun extends TurretAuto
{
    public TurretGun(TileTurret host)
    {
        super(host);
        weaponSystem = new WeaponConventional(this, 8);
        centerOffset.y = 0.3;
        barrelOffset.y = 0.3;
        applyTrait(new SentryTraitDouble(ITurret.SEARCH_RANGE_TRAIT, ITurretUpgrade.TARGET_RANGE, 20.0));
        applyTrait(new SentryTraitDouble(ITurret.MAX_HEALTH_TRAIT, 50.0));
        maxCooldown = 10;
        barrelLength = 1f;
    }
}
