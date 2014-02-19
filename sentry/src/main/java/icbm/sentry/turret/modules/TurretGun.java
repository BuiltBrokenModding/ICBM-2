package icbm.sentry.turret.modules;

import icbm.sentry.turret.block.TileTurret;
import universalelectricity.api.vector.Vector3;

public class TurretGun extends AutoSentry
{
    public TurretGun(TileTurret host)
    {
        super(host);
        maxHealth = 200;
        this.centerOffset.y = 0.65;
    }

    @Override
    public boolean fire (Vector3 vector3)
    {
        return false;
    }
}
