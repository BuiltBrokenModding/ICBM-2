package icbm.sentry.turret.ai;

import icbm.sentry.interfaces.IAutoTurret;
import icbm.sentry.interfaces.ITurret;
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
    }

    public TurretAuto turret()
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

        if (turret() != null)
        {
            // Only get new target if the current is missing or it will switch targets each update
            if (turret().getTarget() == null && ticks % 20 == 0)
            {
                debug("\tSearching for target");
                turret().setTarget(findTarget(turret(), turret().getEntitySelector(), this.turret().getTrait(ITurret.SEARCH_RANGE_TRAIT)));
            }

            // If we have a target start aiming logic
            if (turret().getTarget() != null && isValidTarget(turret().getTarget(), false))
            {
                if (turret().canFire())
                {
                    if (canEntityBeSeen(turret().getTarget()))
                    {
                        debug("\tTarget can be seen");
                        if (isLookingAt(turret().getTarget(), 3))
                        {
                            debug("\tTarget locked and firing weapon");
                            turret().fire(turret().getTarget());
                        }
                        else
                        {
                            debug("\tPowering servos to aim at target");
                            lookAtEntity(turret().getTarget());
                        }

                        targetLostTimer = 0;
                    }
                    else
                    {
                        debug("\tSight on target lost");
                        // Drop the target after 2 seconds of no sight
                        if (targetLostTimer >= 100)
                            turret().setTarget(null);

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
                        Vector3 location = new Vector3(this.turret().x(), this.turret().y(), this.turret().z());
                        location.add(new Vector3(this.turret().world().rand.nextInt(40) - 20, 0, this.turret().world().rand.nextInt(40) - 20));
                        lookAt(location);
                    }

                    turret().setTarget(null);
                    rotationDelayTimer++;
                }

                targetLostTimer++;
            }
        }

    }

    protected Entity findTarget(ITurret sentry, IEntitySelector targetSelector, double target_range)
    {
        AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(turret().fromCenter().x, turret().fromCenter().y, turret().fromCenter().z, turret().fromCenter().x, turret().fromCenter().y, turret().fromCenter().z).expand(target_range, target_range, target_range);
        List<Entity> list = turret().world().selectEntitiesWithinAABB(Entity.class, aabb, targetSelector);
        Collections.sort(list, new ComparatorOptimalTarget(turret().fromCenter()));
        for (Entity entity : list)
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
        if (turret().getEntitySelector().isEntityApplicable(entity))
        {
            return skipSight || isTargetInBounds(entity) && canEntityBeSeen(entity);
        }
        return false;
    }

    // TODO: Add options to this for reversing the targeting filter
    public static class ComparatorOptimalTarget implements Comparator<Entity>
    {
        private final VectorWorld location;

        public ComparatorOptimalTarget(VectorWorld location)
        {
            this.location = location;
        }

        public int compare(Entity entityA, Entity entityB)
        {
            double distanceA = this.location.distance(entityA);
            double distanceB = this.location.distance(entityB);

            if (Math.abs(distanceA - distanceB) < 1.5)
            {
                float healthA = entityA instanceof EntityLivingBase ? ((EntityLivingBase) entityA).getHealth() : 0;
                float healthB = entityB instanceof EntityLivingBase ? ((EntityLivingBase) entityB).getHealth() : 0;
                return healthA < healthB ? -1 : (healthA != healthB ? 1 : 0);
            }

            return distanceA < distanceB ? -1 : (distanceA != distanceB ? 1 : 0);
        }
    }

    /** Adjusts the turret target to look at a specific location. */
    public void lookAt(Vector3 target)
    {
        turret().getServo().setTargetRotation(turret().fromCenter().toAngle(target));
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
        return isTargetInBounds(this.turret().fromCenter(), target);
    }

    public boolean isTargetInBounds(Vector3 start, Vector3 target)
    {
        EulerAngle angle = start.toAngle(target);
        return turret().getServo().isWithinLimit(angle);
    }

    /** checks to see if the tileTurret is looking the target location
     * 
     * @param target - xyz target
     * @param allowedError - amount these tileTurret can be off in degrees from target
     * @return true if its with in error range */
    public boolean isLookingAt(Vector3 target, float allowedError)
    {
        EulerAngle targetAngle = turret().fromCenter().toAngle(target);
        return turret().getServo().isWithin(targetAngle, allowedError);
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
        Vector3 traceStart = turret().fromCenter().translate(turret().getWeaponOffset());
        return canEntityBeSeen(traceStart, entity);
    }

    public boolean canEntityBeSeen(Vector3 traceStart, Entity entity)
    {
        MovingObjectPosition hitTarget = traceStart.clone().rayTrace(entity.worldObj, Vector3.fromCenter(entity), false);
        return hitTarget != null && entity.equals(hitTarget.entityHit);
    }

    public MovingObjectPosition rayTrace(double distance)
    {
        // Ray Tracing is needed for Mounted Sentries aswell
        Vector3 reach = turret.getServo().toVector().clone().scale(distance);
        MovingObjectPosition hitTarget = turret.fromCenter().translate(turret.getWeaponOffset()).rayTrace(turret.world(), reach, false);
        return hitTarget;
    }
}
