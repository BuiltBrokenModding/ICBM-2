package icbm.sentry.turret;

import icbm.sentry.turret.tiles.TileSentry;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import universalelectricity.api.vector.Vector3;
import universalelectricity.api.vector.VectorWorld;

/** Rotation always in degrees.
 * 
 * @author Calclavia, DarkGuardsman */
public class LookHelper
{
    public static final int PITCH_DISPLACEMENT = 0;
    private TileSentry sentry;
    private VectorWorld center;

    public LookHelper(TileSentry tileSentry)
    {
        this.sentry = tileSentry;
        this.update();
    }

    public void update()
    {
        this.center = new VectorWorld(this.sentry);
        System.out.println(this.sentry.getSentry());
        System.out.println(this.sentry.getSentry().getCenterOffset());
        this.center.add(this.sentry.getSentry().getCenterOffset());
    }

    /** Adjusts the turret target to look at a specific location. */
    public void lookAt(Vector3 target)
    {
        this.sentry.getYawServo().setTargetRotation(getYaw(center, target));
        this.sentry.getPitchServo().setTargetRotation(getPitch(center, target));
    }

    /** Tells the turret to look at a location using an entity */
    public void lookAtEntity(Entity entity)
    {
        this.lookAt(Vector3.translate(new Vector3(entity), new Vector3(0, entity.getEyeHeight(), 0)));
    }

    public float[] getDeltaRotations(Vector3 target)
    {
        return new float[] { getYaw(center, target), getPitch(center, target) };
    }

    /** checks to see if the sentry is looking the target location
     * 
     * @param target - xyz target
     * @param allowedError - amount these sentry can be off in degrees from target
     * @return true if its with in error range */
    public boolean isLookingAt(Vector3 target, float allowedError)
    {
        float yaw = getYaw(center, target);
        float pitch = getPitch(center, target);

        if (Math.abs(getAngleDif(sentry.getYawServo().getRotation(), yaw)) <= allowedError)
        {
            if (Math.abs(getAngleDif(sentry.getPitchServo().getRotation(), pitch)) <= allowedError)
            {
                return true;
            }
        }

        return false;
    }

    /** checks to see if the sentry is looking the the entity
     * 
     * @param entity - entity be used for the location
     * @param allowedError - amount these sentry can be off in degrees from target
     * @return true if its with in error range */
    public boolean isLookingAt(Entity entity, float allowedError)
    {
        return this.isLookingAt(new Vector3(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ), allowedError);
    }

    /** Gets the pitch angle between the two points */
    public static float getPitch(Vector3 position, Vector3 target)
    {
        Vector3 difference = target.clone().difference(target);
        double verticleDistance = MathHelper.sqrt_double(difference.x * difference.x + difference.z * difference.z);
        return -MathHelper.wrapAngleTo180_float((float) (Math.atan2(difference.y, verticleDistance) * 180.0D / Math.PI) + PITCH_DISPLACEMENT);
    }

    /** Gets the rotation yaw between the two points in angles */
    public static float getYaw(Vector3 position, Vector3 target)
    {
        Vector3 difference = target.clone().difference(target);
        return MathHelper.wrapAngleTo180_float((float) (Math.atan2(difference.z, difference.x) * 180.0D / Math.PI) - 90.0F);
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
        return this.sentry.worldObj.clip(center.toVec3(), target.toVec3()) == null;
    }

    public boolean canEntityBeSeen(Entity entity)
    {
        Vector3 target = Vector3.translate(new Vector3(entity), new Vector3(0, entity.getEyeHeight(), 0));
        return this.canPositionBeSeen(target);
    }
}
