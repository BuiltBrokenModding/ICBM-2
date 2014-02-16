package icbm.sentry.turret.modules.mount;

import icbm.sentry.turret.Sentry;
import icbm.sentry.turret.SentryTypes;
import icbm.sentry.turret.block.TileTurret;
import universalelectricity.api.vector.Vector3;

/** this sentry uses for mounting the player in position */
public class MountedSentry extends Sentry
{
    protected Vector3 riderOffset = new Vector3(0.5, 1.2, 0.5);

    public MountedSentry(TileTurret host)
    {
        super(host);
    }

    @Override
    public void updateLoop ()
    {

    }

    public Vector3 getRiderOffset()
    {
        return this.riderOffset;
    }

}
