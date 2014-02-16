package icbm.sentry.turret.modules;

import icbm.sentry.turret.SentryTypes;
import icbm.sentry.turret.block.TileSentry;

public class TurretGun extends AutoSentry
{
    public TurretGun(TileSentry host)
    {
        super(host);
        maxHealth = 200;
        this.centerOffset.y = 0.65;
    }
    
}
