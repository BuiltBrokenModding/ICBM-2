package icbm.sentry.turret.modules;

import icbm.Reference;
import icbm.sentry.turret.SentryTypes;
import icbm.sentry.turret.block.TileTurret;
import universalelectricity.api.vector.Vector3;

/** AA Turret, shoots down missiles and planes.
 * 
 * @author DarkGaurdsman */
public class TurretAntiAir extends AutoSentry
{
    public TurretAntiAir(TileTurret host)
    {
        super(host);
        this.centerOffset.y = 0.75;
    }

    @Override
    public void fireWeaponClient(Vector3 target)
    {
        this.world().playSoundEffect(this.x(), this.host.y(), this.host.z(), Reference.PREFIX + "aagun", 5F, 1F);
    }

    @Override
    public boolean fire ()
    {
        return false;
    }
}
