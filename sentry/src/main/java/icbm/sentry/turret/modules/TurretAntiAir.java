package icbm.sentry.turret.modules;

import icbm.Reference;
import icbm.sentry.turret.SentryTypes;
import icbm.sentry.turret.block.TileSentry;
import universalelectricity.api.vector.Vector3;

/** AA Turret, shoots down missiles and planes.
 * 
 * @author DarkGaurdsman */
public class TurretAntiAir extends AutoSentry
{
    public TurretAntiAir(TileSentry host)
    {
        super(host);
        this.centerOffset.y = 0.75;
    }

    @Override
    public void fireWeaponClient(Vector3 target)
    {
        this.host.worldObj.playSoundEffect(this.host.xCoord, this.host.yCoord, this.host.zCoord, Reference.PREFIX + "aagun", 5F, 1F);
    }
}
