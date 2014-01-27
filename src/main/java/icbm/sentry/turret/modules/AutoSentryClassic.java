package icbm.sentry.turret.modules;

import icbm.sentry.turret.TileSentry;

public class AutoSentryClassic extends AutoSentry
{
    public AutoSentryClassic(TileSentry host)
    {
        super(host);
        this.maxHealth = 200;
        this.centerOffset.y = 0.65;
    }
}
