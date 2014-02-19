package icbm.sentry.turret.weapon;

import net.minecraft.util.DamageSource;

/**
 * @since 19/02/14
 * @author tgame14
 */
public class TurretDamageSource extends DamageSource
{
    private TurretDamageSource (String source)
    {
        super(source);
        this.setProjectile();
    }

    public static DamageSource TurretProjectile = new TurretDamageSource("Turret Projectile Damge");
}
