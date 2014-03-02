package icbm.sentry.turret.weapon;

import net.minecraft.util.DamageSource;

/**
 * Damage sources of ICBM turrets
 *
 * @since 19/02/14
 * @author tgame14
 */
public class TurretDamageSource extends DamageSource
{
    private TurretDamageSource (String source)
    {
        super(source);
    }

    /** used by the gun, AA sentry */
    public static DamageSource turretProjectile = new TurretDamageSource("TurretProjectile").setProjectile();
    /** used by the laser sentry */
    public static DamageSource turretLaser = new TurretDamageSource("TurretLaser");
}
