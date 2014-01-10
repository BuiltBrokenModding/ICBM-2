package icbm.sentry.turret.sentries;

import icbm.Reference;
import universalelectricity.api.vector.Vector3;

/** AA Turret, shoots down missiles and planes.
 * 
 * @author DarkGaurdsman */
public class TileEntityAAGun extends TileEntityAutoTurret
{
    public TileEntityAAGun()
    {
        this.baseTargetRange = 80;
        this.maxTargetRange = 120;

        this.rotationSpeed = 9;

        this.minFiringDelay = 8;
        this.baseFiringDelay = 15;
        this.getPitchServo().setLimits(90, 40);
        this.allowFreePitch = true;
    }

    @Override
    public int getMaxHealth()
    {
        return 180;
    }

    public void playFiringSound()
    {
        this.worldObj.playSoundEffect(this.xCoord, this.yCoord, this.zCoord, Reference.PREFIX + "aagun", 5F, 1F);
    }

    @Override
    public Vector3 pos()
    {
        return new Vector3(xCoord + 0.5, yCoord + 0.75, zCoord + 0.5);
    }

}
