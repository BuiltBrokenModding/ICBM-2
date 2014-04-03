package icbm.sentry.turret.ai;

import icbm.sentry.interfaces.IAutoTurret;
import icbm.sentry.interfaces.ITurret;
import icbm.sentry.turret.Turret;
import icbm.sentry.turret.auto.TurretAuto;
import icbm.sentry.turret.traits.SentryTrait;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
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
    public boolean debugMode = false;
    private Turret turret;
    private int rotationDelayTimer = 0;
    private int targetLostTimer = 0;
    private int ticks = 0;
    private double search_range = 10;

    public TurretAI(Turret turret)
    {
        this.turret = turret;
    }

    public void debug(String str)
    {
        if (debugMode)
        {
            System.out.println("[Sentry AI] " + str);
        }
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
            this.search_range = SentryTrait.asDouble(turret().getTrait(ITurret.SEARCH_RANGE_TRAIT), 10);
            // Only get new target if the current is missing or it will switch targets each update
            if (turret().getTarget() == null)
            {

                debug("\tSearching for target within " + search_range + " blocks");
                turret().setTarget(findTarget(turret(), turret().getEntitySelector(), search_range));
            }

            // If we have a target start aiming logic
            if (turret().getTarget() != null && isValidTarget(turret().getTarget(), false))
            {
                if (canEntityBeSeen(turret().getTarget()))
                {
                    debug("\tTarget can be seen");
                    if (isLookingAt(turret().getTarget(), 3))
                    {
                        if (turret().canFire())
                        {
                            debug("\tTarget locked and firing weapon");
                            turret().fire(turret().getTarget());
                        }
                        else
                        {
                            debug("\tTarget locked but weapon is not ready");
                        }
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
                    {
                        turret().setTarget(null);
                    }

                    targetLostTimer++;
                }

            }
            else
            {
                debug("\tInvalid or null target");
                // Only start random rotation after a second of no target
                if (targetLostTimer >= 20)
                {
                    debug("\tTarget market as lost, resetting target");
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
        debug("\t" + list.size() + "targets in range ");
        for (Entity entity : list)
        {
            debug("\t\tNext target " + entity.toString());
            if (isValidTarget(entity, false))
            {
                debug("\t\t\tValid");
                return entity;
            }
        }

        return null;
    }

    public boolean isValidTarget(Entity entity, boolean skipSight)
    {
        if (turret().getEntitySelector().isEntityApplicable(entity))
        {
            if (turret().fromCenter().distance(entity) <= search_range)
            {
                boolean bound_flag = isTargetInBounds(entity);
                boolean sight_flag = canEntityBeSeen(entity);
                debug("\t\tEntity: " + entity.toString() + " Sight: " + sight_flag + " Bounds:" + bound_flag);
                return skipSight || bound_flag && sight_flag;
            }
        }
        return false;
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
        Vector3 reach = new Vector3(turret.getServo()).scale(distance);
        MovingObjectPosition hitTarget = turret.fromCenter().translate(turret.getWeaponOffset()).rayTrace(turret.world(), reach, false);
        return hitTarget;
    }

    // TODO: Add options to this for reversing the targeting filter
    public static class ComparatorOptimalTarget implements Comparator<Entity>
    {
        private final VectorWorld location;

        public ComparatorOptimalTarget(VectorWorld location)
        {
            this.location = location;
        }

        @Override
        public int compare(Entity entityA, Entity entityB)
        {
            Double distanceA = location.distance(entityA);
            Double distanceB = location.distance(entityB);
            return distanceA.compareTo(distanceB);
        }
    }
}
