package icbm.sentry.turret.sentries;

import icbm.Reference;
import universalelectricity.api.vector.Vector3;

public class TileEntityGunTurret extends TileAutoTurret
{
    public TileEntityGunTurret()
    {
        this.baseTargetRange = 13;
        this.maxTargetRange = 25;

        this.rotationSpeed = 2;

        this.baseFiringDelay = 18;
        this.minFiringDelay = 10;
    }

    @Override
    public int getMaxHealth()
    {
        return 200;
    }

   

    @Override
    public Vector3 pos()
    {
        return new Vector3(xCoord + 0.5, yCoord + 0.65, zCoord + 0.5);
    }
}
