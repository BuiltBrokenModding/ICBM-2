package icbm.sentry.turret.auto;

import icbm.sentry.turret.block.TileTurret;
import icbm.sentry.turret.weapon.WeaponSystemProjectile;

/** AA Turret, shoots down missiles and planes.
 * 
 * @author DarkGaurdsman */
public class TurretAntiAir extends AutoSentry
{
    public TurretAntiAir(TileTurret host)
    {
        super(host);
        this.weaponSystem = new WeaponSystemProjectile(this, 10);
        this.centerOffset.y = 0.75;
    }
}
