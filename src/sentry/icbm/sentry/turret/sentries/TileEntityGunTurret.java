package icbm.sentry.turret.sentries;

import icbm.core.ICBMCore;
import universalelectricity.api.vector.Vector3;

public class TileEntityGunTurret extends TileEntityAutoTurret
{
    public TileEntityGunTurret()
    {
        this.targetPlayers = true;
        this.targetHostile = true;

        this.baseTargetRange = 13;
        this.maxTargetRange = 25;

        this.rotationSpeed = 2;

        this.baseFiringDelay = 18;
        this.minFiringDelay = 10;
    }

    @Override
    public float getVoltage()
    {
        return 240;
    }

    @Override
    public int getMaxHealth()
    {
        return 200;
    }

    @Override
    public long getFiringRequest()
    {
        return 1;
    }

    @Override
    public void playFiringSound()
    {
        this.worldObj.playSoundEffect(this.xCoord, this.yCoord, this.zCoord, ICBMCore.PREFIX + "machinegun", 5F, 1F);
    }

    @Override
    public Vector3 getMuzzle()
    {
        return this.getCenter().translate(Vector3.scale(Vector3.getDeltaPositionFromRotation(this.currentRotationYaw, this.currentRotationPitch), 1));
    }

    @Override
    public Vector3 getCenter()
    {
        return new Vector3(this).add(new Vector3(0.5, 0.65, 0.5));
    }
}
