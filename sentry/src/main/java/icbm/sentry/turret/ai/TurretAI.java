package icbm.sentry.turret.ai;

import icbm.sentry.interfaces.IAutoTurret;
import icbm.sentry.interfaces.ITurret;
import icbm.sentry.interfaces.ITurretProvider;
import icbm.sentry.turret.Turret;
import icbm.sentry.turret.auto.TurretAuto;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import universalelectricity.api.vector.EulerAngle;
import universalelectricity.api.vector.Vector3;
import universalelectricity.api.vector.VectorWorld;

/** AI for the sentry objects
 * 
 * @author DarkGuardsman, Calclavia, Tgame14 */
public class TurretAI
{
    private Turret turret;
    private IEntitySelector entitySelector;
    private int rotationDelayTimer = 0;
    private int targetLostTimer = 0;
    private int ticks = 0;

    public static final boolean debugMode = false;

    public static void debug(String str)
    {
        if (debugMode)
            System.out.println("[Sentry AI] " + str);
    }

    public TurretAI(Turret turret)
    {
        this.turret = turret;
        // TODO get selector from sentry at a later date
        this.entitySelector = new TurretEntitySelector(this.turret);
    }

    public TurretAuto sentry()
    {
        if (turret != null && turret != null && turret instanceof IAutoTurret)
        {
            return (TurretAuto) turret;
        }
        return null;
    }

    /** This update should only be called server side. */
    public void update()
    {
        ticks++;

        if (sentry() != null)
        {
            // Used to debug and force the sentry to look at player to make correct model rotation
            // adjustments.
            /*
             * List<EntityLivingBase> list =
             * turret.world().selectEntitiesWithinAABB(EntityLivingBase.class,
             * AxisAlignedBB.getBoundingBox(turret.getAbsoluteCenter().x,
             * turret.getAbsoluteCenter().y, turret.getAbsoluteCenter().z,
             * turret.getAbsoluteCenter().x, turret.getAbsoluteCenter().y,
             * turret.getAbsoluteCenter().z).expand(10, 10, 10), null);
             * for (EntityLivingBase entity : list)
             * {
             * if (entity instanceof EntityPlayer)
             * {
             * lookAtEntity(entity);
             * return;
             * }
             * }
             */

            // Only get new target if the current is missing or it will switch targets each update
            if (sentry().getTarget() == null && ticks % 20 == 0)
            {
                debug("\tSearching for target");
                sentry().setTarget(findTarget(turret, this.entitySelector, this.turret.getRange()));
            }

            // If we have a target start aiming logic
            if (sentry().getTarget() != null && isValidTarget(sentry().getTarget(), false))
            {
                if (sentry().canFire())
                {
                    if (canEntityBeSeen(sentry().getTarget()))
                    {
                        debug("\tTarget can be seen");
                        if (isLookingAt(sentry().getTarget(), 3))
                        {
                            debug("\tTarget locked and firing weapon");
                            turret.fire(sentry().getTarget());
                        }
                        else
                        {
                            debug("\tPowering servos to aim at target");
                            lookAtEntity(sentry().getTarget());
                        }

                        targetLostTimer = 0;
                    }
                    else
                    {
                        debug("\tSight on target lost");
                        // Drop the target after 2 seconds of no sight
                        if (targetLostTimer >= 100)
                            sentry().setTarget(null);

                        targetLostTimer++;
                    }
                }
            }
            else
            {
                // Only start random rotation after a second of no target
                if (targetLostTimer >= 20)
                {
                    if (rotationDelayTimer >= 60)
                    {
                        debug("\tNo Target Selected. Wandering.");
                        rotationDelayTimer = 0;
                        Vector3 location = new Vector3(this.turret.x(), this.turret.y(), this.turret.z());
                        location.add(new Vector3(this.turret.world().rand.nextInt(40) - 20, 0, this.turret.world().rand.nextInt(40) - 20));
                        lookAt(location);
                    }

                    sentry().setTarget(null);
                    rotationDelayTimer++;
                }

                targetLostTimer++;
            }
        }

    }

    protected EntityLivingBase findTarget(ITurret sentry, IEntitySelector targetSelector, int range)
    {
        AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(turret.getAbsoluteCenter().x, turret.getAbsoluteCenter().y, turret.getAbsoluteCenter().z, turret.getAbsoluteCenter().x, turret.getAbsoluteCenter().y, turret.getAbsoluteCenter().z).expand(range, range, range);
        List<EntityLivingBase> list = turret.world().selectEntitiesWithinAABB(EntityLivingBase.class, aabb, targetSelector);
        Collections.sort(list, new ComparatorOptimalTarget(turret.getAbsoluteCenter()));
        for (EntityLivingBase entity : list)
        {
            if (isValidTarget(entity, false))
            {
                return entity;
            }
        }

        return null;
    }

    public boolean isValidTarget(Entity entity, boolean skipSight)
    {
        if (this.entitySelector.isEntityApplicable(entity))
        {
            return skipSight || isTargetInBounds(entity) && canEntityBeSeen(entity);
        }
        return false;
    }

    // TODO: Add options to this for reversing the targeting filter
    public static class ComparatorOptimalTarget implements Comparator<EntityLivingBase>
    {
        private final VectorWorld location;

        public ComparatorOptimalTarget(VectorWorld location)
        {
            this.location = location;
        }

        public int compare(EntityLivingBase entityA, EntityLivingBase entityB)
        {
            double distanceA = this.location.distance(entityA);
            double distanceB = this.location.distance(entityB);

            if (Math.abs(distanceA - distanceB) < 1.5)
            {
                float healthA = entityA.getHealth();
                float healthB = entityB.getHealth();
                return healthA < healthB ? -1 : (healthA > healthB ? 1 : 0);
            }

            return distanceA < distanceB ? -1 : (distanceA > distanceB ? 1 : 0);
        }
    }

    /** Adjusts the turret target to look at a specific location. */
    public void lookAt(Vector3 target)
    {
        turret.getServo().setTargetRotation(turret.getAbsoluteCenter().toAngle(target));
    }

    /** Tells the turret to look at a location using an entity */
    public void lookAtEntity(Entity entity)
    {
        lookAt(Vector3.fromCenter(entity));
    }

    /** Checks to see if target is within the range of the turret.
     * 
     * @param target
     * @return */
    public boolean isTargetInBounds(Entity target)
    {
        return isTargetInBounds(Vector3.fromCenter(target));
    }

    public boolean isTargetInBounds(Vector3 target)
    {
        return isTargetInBounds(this.turret.getAbsoluteCenter(), target);
    }

    public boolean isTargetInBounds(Vector3 start, Vector3 target)
    {
        EulerAngle angle = start.toAngle(target);
        return turret.getServo().isWithinLimit(angle);
    }

    /** checks to see if the tileTurret is looking the target location
     * 
     * @param target - xyz target
     * @param allowedError - amount these tileTurret can be off in degrees from target
     * @return true if its with in error range */
    public boolean isLookingAt(Vector3 target, float allowedError)
    {
        EulerAngle targetAngle = turret.getAbsoluteCenter().toAngle(target);
        return turret.getServo().isWithin(targetAngle, allowedError);
    }

    /** Checks to see if the tileTurret is looking the the entity
     * 
     * @param entity - entity be used for the location
     * @param allowedError - amount these tileTurret can be off in degrees from target
     * @return true if its with in error range */
    public boolean isLookingAt(Entity entity, float allowedError)
    {
        return isLookingAt(Vector3.fromCenter(entity), allowedError);
    }

    /** Translates the aim offset "out of the turret block" to prevent the turret itself from block
     * the ray trace.
     * 
     * @param entity
     * @return */
    public boolean canEntityBeSeen(Entity entity)
    {
        Vector3 traceStart = turret.getAbsoluteCenter().translate(turret.getAimOffset());
        return canEntityBeSeen(traceStart, entity);
    }

    public boolean canEntityBeSeen(Vector3 traceStart, Entity entity)
    {
        MovingObjectPosition hitTarget = traceStart.clone().rayTrace(entity.worldObj, Vector3.fromCenter(entity), false);
        return hitTarget != null && entity.equals(hitTarget.entityHit);
    }
}
