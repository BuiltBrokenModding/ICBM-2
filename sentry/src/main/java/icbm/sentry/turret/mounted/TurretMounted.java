package icbm.sentry.turret.mounted;

import icbm.sentry.interfaces.ITurretProvider;
import icbm.sentry.turret.EntityMountableDummy;
import icbm.sentry.turret.Turret;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import universalelectricity.api.vector.Vector3;

/** this sentry uses for mounting the player in position */
public abstract class TurretMounted extends Turret
{
    protected Vector3 riderOffset = new Vector3();

    public TurretMounted(ITurretProvider turretProvider)
    {
        super(turretProvider);
    }

    @Override
    public void update()
    {
        super.update();

        if (!world().isRemote)
        {
            EntityMountableDummy sentryEntity = getHost().getFakeEntity();

            if (sentryEntity.riddenByEntity instanceof EntityPlayer)
            {
                EntityPlayer mountedPlayer = (EntityPlayer) sentryEntity.riddenByEntity;
                getServo().yaw = -mountedPlayer.rotationYaw + 180;
                getServo().pitch = -mountedPlayer.rotationPitch;
                getServo().update();
            }

            if (world().isBlockIndirectlyGettingPowered((int) getHost().x(), (int) getHost().y() - 1, (int) getHost().z()))
            {
                if (canFire())
                {
                    onRedstone();
                }
            }
        }
    }

    public void fire()
    {
        Vector3 start = this.getAbsoluteCenter().translate(this.getAimOffset());
        Vector3 end = this.getAbsoluteCenter().translate(new Vector3(getServo()).scale(500).translate(aimOffset));
        MovingObjectPosition endTarget = start.rayTrace(world(), end, true);
        Vector3 hit = end;
        if (endTarget != null)
        {
            hit = new Vector3(endTarget.hitVec);
        }
        fire(hit);
    }

    public void onRedstone()
    {

    }

    public Vector3 getRiderOffset()
    {
        return riderOffset;
    }

    @Override
    public boolean mountable()
    {
        return true;
    }
}
