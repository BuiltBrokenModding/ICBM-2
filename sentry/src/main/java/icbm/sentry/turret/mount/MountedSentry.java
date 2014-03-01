package icbm.sentry.turret.mount;

import icbm.sentry.turret.Sentry;
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

    public Vector3 getRiderOffset()
    {
        return this.riderOffset;
    }

    @Override
    public boolean mountable()
    {
        return true;
    }

    @Override
    public boolean fire (Vector3 vector3)
    {
        return false;
    }

}
