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

    public static DamageSource TurretProjectile = new TurretDamageSource("TurretProjectile").setProjectile();
    public static DamageSource TurretLaser = new TurretDamageSource("TurretLaser").setFireDamage();
}
