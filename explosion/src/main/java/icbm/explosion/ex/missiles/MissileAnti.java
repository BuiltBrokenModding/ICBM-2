package icbm.explosion.ex.missiles;

import icbm.Reference;
import icbm.explosion.entities.EntityMissile;
import icbm.explosion.explosive.blast.BlastRepulsive;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import universalelectricity.api.vector.Vector3;
import calclavia.api.icbm.ITarget;
import calclavia.api.icbm.ITarget.TargetType;

/** Antiballistic missile.
 * 
 * @author Calclavia */
public class MissileAnti extends Missile
{
    public static final int ABMRange = 30;

    public MissileAnti()
    {
        super("antiBallistic", 2);
        this.hasBlock = false;
        this.modelName = "missile_antimatter.tcn";
    }

    @Override
    public void update(EntityMissile missileObj)
    {
        if (missileObj.lockedTarget != null)
        {
            Vector3 target = new Vector3(missileObj.lockedTarget);

            if (missileObj.lockedTarget.isDead)
            {
                missileObj.explode();
                return;
            }

            if (missileObj.lockedTarget instanceof ITarget && ((ITarget) missileObj.lockedTarget).getType() == TargetType.MISSILE)
            {
                if (((ITarget) missileObj.lockedTarget).canBeTargeted(this))
                {
                    target = ((ITarget) missileObj.lockedTarget).getPredictedPosition(4);
                }
            }

            missileObj.motionX = (target.x - missileObj.posX) * (0.3F);
            missileObj.motionY = (target.y - missileObj.posY) * (0.3F);
            missileObj.motionZ = (target.z - missileObj.posZ) * (0.3F);

            return;
        }

        AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(missileObj.posX - ABMRange, missileObj.posY - ABMRange, missileObj.posZ - ABMRange, missileObj.posX + ABMRange, missileObj.posY + ABMRange, missileObj.posZ + ABMRange);
        // TODO: Check if this works.
        Entity nearestEntity = missileObj.worldObj.findNearestEntityWithinAABB(ITarget.class, bounds, missileObj);

        if (nearestEntity instanceof ITarget && ((ITarget) nearestEntity).getType() == TargetType.MISSILE)
        {
            if (((ITarget) nearestEntity).canBeTargeted(this))
            {
                // Lock target onto missileObj missile
                missileObj.lockedTarget = nearestEntity;
                missileObj.didTargetLockBefore = true;
                missileObj.worldObj.playSoundAtEntity(missileObj, Reference.PREFIX + "targetlocked", 5F, 0.9F);
            }
        }
        else
        {
            missileObj.motionX = missileObj.deltaPathX / missileObj.missileFlightTime;
            missileObj.motionZ = missileObj.deltaPathZ / missileObj.missileFlightTime;

            if (missileObj.didTargetLockBefore == true)
            {
                missileObj.explode();
            }
        }
    }

    @Override
    public boolean isCruise()
    {
        return true;
    }

    @Override
    public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
    {
        new BlastRepulsive(world, entity, x, y, z, 6).setDestroyItems().explode();
    }
}
