package icbm.sentry.turret.modules;

import icbm.Reference;
import icbm.sentry.ICBMSentry;
import icbm.sentry.turret.tiles.TileSentry;
import net.minecraft.util.MathHelper;
import universalelectricity.api.vector.Vector3;

public class AutoSentryTwinLaser extends AutoSentry
{
    /** Laser turret spins its barrels every shot. */
    public float barrelRotation;
    public float barrelRotationVelocity;

    public AutoSentryTwinLaser(TileSentry host)
    {
        super(host);
        this.maxHealth = 50;
    }

    @Override
    public void update()
    {
        super.update();
        if (this.host.worldObj.isRemote)
        {
            this.barrelRotation = MathHelper.wrapAngleTo180_float(this.barrelRotation + this.barrelRotationVelocity);
            this.barrelRotationVelocity = Math.max(this.barrelRotationVelocity - 0.1f, 0);
        }
    }

    public void playFiringSound()
    {
        this.host.worldObj.playSoundEffect(this.host.xCoord, this.host.yCoord, this.host.zCoord, Reference.PREFIX + "lasershot", 5F, 1F - (this.host.worldObj.rand.nextFloat() * 0.2f));
    }

    public void renderShot(Vector3 target)
    {
        Vector3 center = new Vector3(this.host.xCoord, this.host.yCoord, this.host.zCoord).add(this.centerOffset);
        ICBMSentry.proxy.renderBeam(this.host.worldObj, Vector3.translate(center, Vector3.getDeltaPositionFromRotation(this.host.getYawServo().getRotation() - 6, this.host.getPitchServo().getRotation() * 1.4f).scale(1.2)), target, 1, 0.4f, 0.4f, 5);
        ICBMSentry.proxy.renderBeam(this.host.worldObj, Vector3.translate(center, Vector3.getDeltaPositionFromRotation(this.host.getYawServo().getRotation() + 6, this.host.getPitchServo().getRotation() * 1.4f).scale(1.2)), target, 1, 0.4f, 0.4f, 5);
        this.barrelRotationVelocity += 1;
    }

    @Override
    public Modules getSentryType ()
    {
        return Modules.LASER;
    }
}
