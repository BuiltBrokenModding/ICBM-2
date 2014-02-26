package icbm.sentry.turret.ai;

import icbm.sentry.interfaces.ISentryContainer;
import icbm.sentry.turret.block.TileTurret;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import universalelectricity.api.vector.Vector3;
import universalelectricity.api.vector.VectorWorld;
import calclavia.lib.prefab.IServo;
import calclavia.lib.utility.MathUtility;

/** Rotation always in degrees.
 * 
 * @author Calclavia, DarkGuardsman */
//Look helper will be parted out during 1.7 update. Some method will moved to a math helper, and rest to AI handlers 
public class LookHelper
{
    private TileTurret tileTurret;

    public LookHelper(TileTurret tileSentry)
    {
        this.tileTurret = tileSentry;
    }

    /** Adjusts the turret target to look at a specific location. */
    public void lookAt(Vector3 target)
    {
        this.tileTurret.getYawServo().setTargetRotation(getYaw(getCenter(), target));
        this.tileTurret.getPitchServo().setTargetRotation(getPitch(getCenter(), target));
    }

    /** Tells the turret to look at a location using an entity */
    public void lookAtEntity(Entity entity)
    {
        this.lookAt(Vector3.fromCenter(entity));
    }

    public boolean isTargetInBounds(Entity target)
    {
        return isTargetInBounds(Vector3.fromCenter(target));
    }

    public boolean isTargetInBounds(Vector3 target)
    {
        return isTargetInBounds(this.getCenter(), target, this.tileTurret.getYawServo(), this.tileTurret.getPitchServo());
    }

    public static boolean isTargetInBounds(Vector3 start, Vector3 target, IServo yawServo, IServo pitchServo)
    {
        float yaw = getYaw(start, target);
        float pitch = getPitch(start, target);
        if (yaw >= yawServo.lowerLimit() && yaw <= yawServo.upperLimit())
        {
            if (pitch >= pitchServo.lowerLimit() && pitch <= pitchServo.upperLimit())
            {
                return true;
            }
        }
        return false;
    }

    /** checks to see if the tileTurret is looking the target location
     * 
     * @param target - xyz target
     * @param allowedError - amount these tileTurret can be off in degrees from target
     * @return true if its with in error range */
    public boolean isLookingAt(Vector3 target, float allowedError)
    {
        if (Math.abs(getAngleDif(tileTurret.getYawServo().getRotation(), getYaw(getCenter(), target))) <= allowedError)
        {
            if (Math.abs(getAngleDif(tileTurret.getPitchServo().getRotation(), getPitch(getCenter(), target))) <= allowedError)
            {
                return true;
            }
        }
        return false;
    }

    /** checks to see if the tileTurret is looking the the entity
     * 
     * @param entity - entity be used for the location
     * @param allowedError - amount these tileTurret can be off in degrees from target
     * @return true if its with in error range */
    public boolean isLookingAt(Entity entity, float allowedError)
    {
        return this.isLookingAt(Vector3.fromCenter(entity), allowedError);
    }

    /** Gets the pitch angle between the two points */
    public static float getPitch(Vector3 position, Vector3 target)
    {
        double pitchRadians = getPitchRadians(position, target);
        return (float) MathUtility.clampAngleTo360(-Math.toDegrees(pitchRadians));
    }

    public static double getPitchRadians(Vector3 position, Vector3 target)
    {
        Vector3 d = position.difference(target);
        return Math.atan2(Math.hypot(d.z, d.x), d.y) - (Math.PI / 2);
    }

    /** Gets the rotation yaw between the two points in angles */
    public static float getYaw(Vector3 position, Vector3 target)
    {
        double yawRadians = getYawRadians(position, target);
        return (float) MathUtility.clampAngleTo360(Math.toDegrees(yawRadians));

    }

    public static double getYawRadians(Vector3 position, Vector3 target)
    {
        Vector3 d = position.difference(target);
        return Math.atan2(d.z, d.x) - (Math.PI / 2);
    }

    /** gets the difference in degrees between the two angles */
    public static float getAngleDif(float angleOne, float angleTwo)
    {
        return (float) Math.abs(angleOne - angleTwo);
    }

    public boolean canEntityBeSeen(Entity entity)
    {
        Vector3 s = this.getCenterRayStart();
        for (int i = 0; i < 3; i++)
            entity.worldObj.spawnParticle("smoke", s.x, s.y, s.z, 0, .3, 0);
        return canPositionBeSeen(entity.worldObj, s, Vector3.fromCenter(entity));
    }

    public static boolean canEntityBeSeen(Vector3 center, Entity entity)
    {
        //TODO: if raytrace fails ray trace to feet and then head
        return canPositionBeSeen(entity.worldObj, center, Vector3.fromCenter(entity));
    }

    /** does a ray trace to the Entity to see if the turret can see it */
    public static boolean canPositionBeSeen(World world, Vector3 center, Vector3 target)
    {
        MovingObjectPosition hitTarget = center.rayTrace(world, target, false);
        return hitTarget != null && hitTarget.typeOfHit != EnumMovingObjectType.TILE;
    }

    public VectorWorld getCenterRayStart()
    {
        return getCenter(this.tileTurret);
    }

    public static VectorWorld getCenterRayStart(ISentryContainer container)
    {
        return new VectorWorld(container.world(), getCenter(container).translate(new Vector3(container.yaw(), container.pitch())));
    }

    public VectorWorld getCenter()
    {
        return getCenter(this.tileTurret);
    }

    public static VectorWorld getCenter(ISentryContainer container)
    {
        return new VectorWorld(container.world(), new Vector3(container.x(), container.y(), container.z()).translate(container.getSentry().getCenterOffset()));
    }
}