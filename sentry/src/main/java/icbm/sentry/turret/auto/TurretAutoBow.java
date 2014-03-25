package icbm.sentry.turret.auto;

import icbm.sentry.interfaces.ITurret;
import icbm.sentry.interfaces.ITurretProvider;
import icbm.sentry.turret.weapon.types.WeaponBow;

/** Automated cross bow like sentry
 * 
 * @author DarkGuardsman */
public class TurretAutoBow extends TurretAuto
{
    public TurretAutoBow(ITurretProvider host)
    {
        super(host);
        this.weaponSystem = new WeaponBow(this);
        applyTrait(ITurret.SEARCH_RANGE_TRAIT, 25.0);
        applyTrait(ITurret.MAX_HEALTH_TRAIT, 10.0);
        maxCooldown = 30;
        barrelLength = 1f;
    }

}
