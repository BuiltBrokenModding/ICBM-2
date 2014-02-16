package icbm.sentry.turret.modules.mount;

import icbm.Reference;
import icbm.sentry.turret.SentryTypes;
import icbm.sentry.turret.block.TileTurret;
import universalelectricity.api.vector.Vector3;
import calclavia.lib.multiblock.fake.IMultiBlock;

/** Railgun
 * 
 * @author Calclavia */
public class MountedRailGun extends MountedSentry implements IMultiBlock
{
    private int gunChargingTicks = 0;

    private boolean redstonePowerOn = false;
    /** Is current ammo antimatter */
    private boolean isAntimatter;

    private float explosionSize;

    private int explosionDepth;

    /** A counter used client side for the smoke and streaming effects of the Railgun after a shot. */
    private int endTicks = 0;

    public MountedRailGun(TileTurret sentry)
    {
        super(sentry);
        this.host.getPitchServo().setLimits(60, -60);
    }

    @SuppressWarnings("unchecked")
    public void onFire()
    {
        
    }

    public void renderShot(Vector3 target)
    {
        this.endTicks = 20;
    }

    public void playFiringSound()
    {
        this.host.worldObj.playSoundEffect(this.host.xCoord, this.host.yCoord, this.host.zCoord, Reference.PREFIX + "railgun", 5F, 1F);
    }

    @Override
    public Vector3[] getMultiBlockVectors()
    {
        return new Vector3[] { new Vector3(0, 1, 0) };
    }
}
