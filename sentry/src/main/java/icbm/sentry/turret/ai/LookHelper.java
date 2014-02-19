package icbm.sentry.turret.ai;

import icbm.sentry.turret.block.TileTurret;
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
    private TileTurret tileTurret;
    private VectorWorld center;

    public LookHelper(TileTurret tileSentry)
    {
        this.tileTurret = tileSentry;
        this.update();
    }

    public void update()
    {
        this.center = new VectorWorld(this.tileTurret);
        this.center.add(this.tileTurret.getSentry().getCenterOffset());
    }

    /** Adjusts the turret target to look at a specific location. */
    public void lookAt(Vector3 target)
    {
        this.tileTurret.getYawServo().setTargetRotation(getYaw(center, target));
        this.tileTurret.getPitchServo().setTargetRotation(getPitch(center, target));
        System.out.println("LookHelper yaw: " + this.tileTurret.getYawServo().getRotation());
        System.out.println("lookHelper pitch: " + this.tileTurret.getPitchServo().getRotation());
    }

    /** Tells the turret to look at a location using an entity */
    public void lookAtEntity(Entity entity)
    {
        this.lookAt(new Vector3(entity));
    }

    public float[] getDeltaRotations(Vector3 target)
    {
        return new float[] { getYaw(center, target), getPitch(center, target) };
    }

    /** checks to see if the tileTurret is looking the target location
     * 
     * @param target - xyz target
     * @param allowedError - amount these tileTurret can be off in degrees from target
     * @return true if its with in error range */
    public boolean isLookingAt(Vector3 target, float allowedError)
    {
        float yaw = getYaw(center, target);
        float pitch = getPitch(center, target);

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
        Vector3 difference = position.difference(target);

        double flatDist = Math.hypot(difference.x, difference.z);
        float pitch = (float) (Math.toDegrees(Math.atan(difference.y / flatDist))) + PITCH_DISPLACEMENT;
        //System.out.println(pitch + " calculated pitch angle");
        return pitch;
    }

    /** Gets the rotation yaw between the two points in angles */
    public static float getYaw(Vector3 position, Vector3 target)
    {
        Vector3 difference = position.difference(target);
        float yaw = (float) Math.toDegrees(Math.atan(difference.z / difference.x)) - 90.0F;
        //System.out.println(yaw + " yaw angle");
        return yaw;

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
        return this.tileTurret.worldObj.clip(center.toVec3(), target.toVec3()) == null;
    }

    public boolean canEntityBeSeen(Entity entity)
    {
        Vector3 target = Vector3.translate(new Vector3(entity), new Vector3(0, entity.getEyeHeight(), 0));
        return this.canPositionBeSeen(target);
    }

    public VectorWorld getCenter()
    {
        return this.center;
    }
}
