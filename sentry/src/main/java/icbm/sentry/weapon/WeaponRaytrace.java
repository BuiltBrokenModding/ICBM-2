package icbm.sentry.weapon;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MovingObjectPosition;
import universalelectricity.api.vector.IVector3;
import universalelectricity.api.vector.IVectorWorld;
import universalelectricity.api.vector.Vector3;

/** Basic weapon system that uses a raytrace to see if it hits a target. Most commonly used for
 * conventional fire arms.
 * 
 * @author Darkguardsman */
public abstract class WeaponRaytrace extends WeaponSystem
{
    public WeaponRaytrace(IVectorWorld loc)
    {
        super(loc);
    }

    public WeaponRaytrace(TileEntity entity)
    {
        super(entity);
    }

    public WeaponRaytrace(Entity entity)
    {
        super(entity);
    }

    @Override
    protected void doFire(IVector3 target)
    {
        Vector3 hit = new Vector3(target);
        MovingObjectPosition endTarget = getBarrelEnd().rayTrace(world(), hit, true);
        if (endTarget != null)
        {
            if (endTarget.typeOfHit == EnumMovingObjectType.ENTITY)
            {
                onHitEntity(endTarget.entityHit);
            }
            else if (endTarget.typeOfHit == EnumMovingObjectType.TILE)
            {
                onHitBlock(new Vector3(endTarget.hitVec));
            }
        }
    }

    @Override
    public void fireClient(IVector3 hit)
    {
        drawParticleStreamTo(world(), getBarrelEnd(), hit);
    }
}
