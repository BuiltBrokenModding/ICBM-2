package icbm.sentry.turret.mount;

import icbm.sentry.turret.Sentry;
import icbm.sentry.turret.TileSentry;
import universalelectricity.api.vector.Vector3;

public class MountedSentry extends Sentry
{
    /** Fake entity this sentry uses for mounting the player in position */
    protected Vector3 riderOffset = new Vector3(0.5, 1.2, 0.5);

    public MountedSentry(TileSentry host)
    {
        super(host);
    }

    public Vector3 getRiderOffset()
    {
        return this.riderOffset;
    }

}
