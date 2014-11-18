package icbm.sentry.turret.mounted;

import icbm.sentry.interfaces.IMountedTurret;
import icbm.sentry.interfaces.ITurretProvider;
import icbm.sentry.turret.EntityMountableDummy;
import icbm.sentry.turret.Turret;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import universalelectricity.api.vector.Vector3;

/** this sentry uses for mounting the player in position */
public abstract class TurretMounted extends Turret implements IMountedTurret
{
    protected Vector3 riderOffset = new Vector3();
    public EntityMountableDummy sentryEntity;

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
            EntityMountableDummy sentryEntity = getFakeEntity();

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
        Vector3 start = this.fromCenter().translate(this.getWeaponOffset());
        Vector3 end = this.fromCenter().translate(new Vector3(getServo()).scale(500).translate(barrelOffset));
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
    public boolean canMount(Entity entity)
    {
        return entity instanceof EntityLivingBase;
    }
    

    @Override
    public EntityMountableDummy getFakeEntity()
    {
        if (sentryEntity == null || sentryEntity.isDead)
        {
            if (!world().isRemote)
            {
                EntityMountableDummy entity = new EntityMountableDummy(this);
                world().spawnEntityInWorld(entity);
                setFakeEntity(entity);
            }
        }

        return sentryEntity;
    }

    public void setFakeEntity(EntityMountableDummy entitySentryFake)
    {
        this.sentryEntity = entitySentryFake;
    }
}
