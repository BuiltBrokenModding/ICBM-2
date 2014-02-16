package icbm.sentry.turret.modules;

import icbm.sentry.turret.SentryTypes;
import icbm.sentry.turret.block.TileTurret;

public class TurretGun extends AutoSentry
{
    public TurretGun(TileTurret host)
    {
        super(host);
        maxHealth = 200;
        this.centerOffset.y = 0.65;
    }
    
}
