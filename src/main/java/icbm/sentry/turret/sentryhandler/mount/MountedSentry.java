package icbm.sentry.turret.sentryhandler.mount;

import icbm.sentry.turret.sentryhandler.Sentry;
import icbm.sentry.turret.tiles.TileSentry;
import universalelectricity.api.vector.Vector3;

/** Fake Manager? this sentry uses for mounting the player in position */
public class MountedSentry extends Sentry
{
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
