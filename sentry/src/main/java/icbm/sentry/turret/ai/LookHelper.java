package icbm.sentry.turret.ai;

import icbm.sentry.turret.block.TileTurret;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import universalelectricity.api.vector.Vector3;
import universalelectricity.api.vector.VectorWorld;

/** Rotation always in degrees.
 * 
 * @author Calclavia, DarkGuardsman */
@Deprecated
//Look helper will be parted out. Some method will moved to a math helper, and rest to AI handlers 
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
        this.lookAt(new Vector3(entity));
    }

    public float[] getDeltaRotations(Vector3 target)
    {
        return new float[] { getYaw(getCenter(), target), getPitch(getCenter(), target) };
    }

    /** checks to see if the tileTurret is looking the target location
     * 
     * @param target - xyz target
     * @param allowedError - amount these tileTurret can be off in degrees from target
     * @return true if its with in error range */
    public boolean isLookingAt(Vector3 target, float allowedError)
    {
        float yaw = getYaw(getCenter(), target);
        float pitch = getPitch(getCenter(), target);

        if (Math.abs(getAngleDif(tileTurret.getYawServo().getRotation(), yaw)) <= allowedError)
        {
            if (Math.abs(getAngleDif(tileTurret.getPitchServo().getRotation(), pitch)) <= allowedError)
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
        return this.isLookingAt(new Vector3(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ), allowedError);
    }

    /** Gets the pitch angle between the two points */
    public static float getPitch(Vector3 position, Vector3 target)
    {
        double pitchRadians = getPitchRadians(position, target);
        double pitch = (float) Math.toDegrees(pitchRadians);
        System.out.println("[DebugLookHelper] Pitch: " + pitchRadians + " As Degrees: " + pitch);
        while (pitch > 360 || pitch < 0)
        {
            if (pitch > 360)
                pitch -= 360;
            else
                pitch += 360;
        }

        return (float) pitch;
    }

    public static double getPitchRadians(Vector3 position, Vector3 target)
    {
        Vector3 d = position.difference(target);
        return Math.atan2(Math.sqrt(d.z * d.z + d.x * d.x), d.y) - Math.toRadians(90);
    }

    /** Gets the rotation yaw between the two points in angles */
    public static float getYaw(Vector3 position, Vector3 target)
    {
        double yawRadians = getYawRadians(position, target);
        double yaw = Math.toDegrees(yawRadians);
        System.out.println("[DebugLookHelper] Yaw: " + yawRadians + " As Degrees: " + yaw);
        while (yaw > 360 || yaw < 0)
        {
            if (yaw > 360)
                yaw -= 360;
            else
                yaw += 360;
        }
        return (float) yaw;

    }

    public static double getYawRadians(Vector3 position, Vector3 target)
    {
        Vector3 d = position.difference(target);
        return Math.atan2(d.z, d.x) - Math.toRadians(90);
    }

    /** gets the difference in degrees between the two angles */
    public static float getAngleDif(float angleOne, float angleTwo)
    {
        double dif = (Math.max(angleOne, angleTwo) - Math.min(angleOne, angleTwo));
        return MathHelper.wrapAngleTo180_float((float) dif);
    }

    /** does a ray trace to the Entity to see if the turret can see it */
    public boolean canPositionBeSeen(Vector3 target)
    {
        return this.tileTurret.worldObj.clip(getCenter().toVec3(), target.toVec3()) == null;
    }

    public boolean canEntityBeSeen(Entity entity)
    {
        Vector3 target = Vector3.translate(new Vector3(entity), new Vector3(0, entity.getEyeHeight(), 0));
        return this.canPositionBeSeen(target);
    }

    public VectorWorld getCenter()
    {
        return new VectorWorld(tileTurret.getWorldObj(), new Vector3(tileTurret.x(), tileTurret.y(), tileTurret.x()).translate(this.tileTurret.getSentry().getCenterOffset()));
    }
}
