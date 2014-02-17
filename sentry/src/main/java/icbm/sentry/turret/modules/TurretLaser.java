package icbm.sentry.turret.modules;

import icbm.Reference;
import icbm.sentry.ICBMSentry;
import icbm.sentry.turret.SentryTypes;
import icbm.sentry.turret.block.TileTurret;
import net.minecraft.util.MathHelper;
import universalelectricity.api.vector.Vector3;

public class TurretLaser extends AutoSentry
{
    /** Laser turret spins its barrels every shot. */
    public float barrelRotation;
    public float barrelRotationVelocity;

    public TurretLaser(TileTurret host)
    {
        super(host);
        maxHealth = 50;
    }

    @Override
    public void updateEntity()
    {
        super.updateEntity();
        if (this.world().isRemote)
        {
            this.barrelRotation = MathHelper.wrapAngleTo180_float(this.barrelRotation + this.barrelRotationVelocity);
            this.barrelRotationVelocity = Math.max(this.barrelRotationVelocity - 0.1f, 0);
        }
    }

    public void playFiringSound()
    {
        this.world().playSoundEffect(this.x(), this.y(), this.z(), Reference.PREFIX + "lasershot", 5F, 1F - (this.world().rand.nextFloat() * 0.2f));
    }

    public void renderShot(Vector3 target)
    {
        Vector3 center = new Vector3(this.x(), this.y(), this.z()).add(this.centerOffset);
        ICBMSentry.proxy.renderBeam(this.world(), Vector3.translate(center, Vector3.getDeltaPositionFromRotation(this.getHost().yaw() - 6, this.getHost().pitch() * 1.4f).scale(1.2)), target, 1, 0.4f, 0.4f, 5);
        ICBMSentry.proxy.renderBeam(this.world(), Vector3.translate(center, Vector3.getDeltaPositionFromRotation(this.getHost().yaw() + 6, this.getHost().pitch() * 1.4f).scale(1.2)), target, 1, 0.4f, 0.4f, 5);
        this.barrelRotationVelocity += 1;
    }
}
