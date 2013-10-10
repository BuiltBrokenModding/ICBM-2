package icbm.sentry.task;

import icbm.sentry.turret.TPaoTaiBase;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import universalelectricity.core.vector.Vector3;

/**
 * Rotation always in degrees.
 * 
 * @author Calclavia, DarkGuardsman
 */
public class LookHelper
{
	public static final int PITCH_DISPLACEMENT = 0;
	private TPaoTaiBase sentry;

	public LookHelper(TPaoTaiBase turret)
	{
		this.sentry = turret;
	}

	/** Adjusts the turret target to look at a specific location. */
	public void lookAt(Vector3 target)
	{
		this.sentry.rotateTo(getYaw(sentry.getMuzzle(), target), getPitch(sentry.getMuzzle(), target));
	}

	public float[] getDeltaRotations(Vector3 target)
	{
		return new float[] { getYaw(sentry.getMuzzle(), target), getPitch(sentry.getMuzzle(), target) };
	}

	/** Tells the turret to look at a location using an entity */
	public void lookAtEntity(Entity entity)
	{
		this.lookAt(Vector3.add(new Vector3(entity), new Vector3(0, entity.getEyeHeight(), 0)));
	}

	/**
	 * checks to see if the sentry is looking the target location
	 * 
	 * @param target - xyz target
	 * @param allowedError - amount these sentry can be off in degrees from target
	 * @return true if its with in error range
	 */
	public boolean isLookingAt(Vector3 target, float allowedError)
	{
		float yaw = getYaw(sentry.getCenter(), target);
		float pitch = getPitch(sentry.getCenter(), target);

		if (Math.abs(getAngleDif(sentry.currentRotationYaw, yaw)) <= allowedError)
		{
			if (Math.abs(getAngleDif(sentry.currentRotationPitch, pitch)) <= allowedError)
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * checks to see if the sentry is looking the the entity
	 * 
	 * @param entity - entity be used for the location
	 * @param allowedError - amount these sentry can be off in degrees from target
	 * @return true if its with in error range
	 */
	public boolean isLookingAt(Entity entity, float allowedError)
	{
		return this.isLookingAt(new Vector3(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ), allowedError);
	}

	/** Gets the pitch angle between the two points */
	public static float getPitch(Vector3 position, Vector3 target)
	{
		Vector3 difference = Vector3.subtract(target, position);
		double verticleDistance = MathHelper.sqrt_double(difference.x * difference.x + difference.z * difference.z);
		return -MathHelper.wrapAngleTo180_float((float) (Math.atan2(difference.y, verticleDistance) * 180.0D / Math.PI) + PITCH_DISPLACEMENT);
	}

	/** Gets the rotation yaw between the two points in angles */
	public static float getYaw(Vector3 position, Vector3 target)
	{
		Vector3 difference = Vector3.subtract(target, position);
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
	{/*
	 * float rotationYaw = getYaw (this.sentry.getCenter(), target); float rotationPitch =
	 * getPitch(this .sentry.getCenter(), target); MovingObjectPosition mop = CalculationHelper.
	 * doCustomRayTrace (this.sentry.worldObj, this.sentry.getMuzzle(), rotationYaw, rotationPitch,
	 * true, this. sentry.getCenter().distanceTo (target));
	 * 
	 * if (mop != null) { if (mop.hitVec != null) { return new Vector3(mop.hitVec ).round(
	 * ).equals(target.round()); } } else {System.out.println ("TRUE"); return true; }
	 * 
	 * return false;
	 */
		return this.sentry.worldObj.clip(this.sentry.getMuzzle().toVec3(), target.toVec3()) == null;
	}

	public boolean canEntityBeSeen(Entity entity)
	{
		Vector3 target = Vector3.add(new Vector3(entity), new Vector3(0, entity.getEyeHeight(), 0));
		/*
		 * float rotationYaw = getYaw(this.sentry.getCenter(), target); float rotationPitch =
		 * getPitch(this.sentry.getCenter(), target); MovingObjectPosition mop =
		 * CalculationHelper.doCustomRayTrace(this.sentry.worldObj, this.sentry.getCenter(),
		 * rotationYaw, rotationPitch, true, this.sentry.getCenter().distanceTo(target));
		 * 
		 * if (mop != null ) {System.out.println(mop.hitVec); return entity.equals(mop.entityHit); }
		 * return false;
		 */
		return this.canPositionBeSeen(target);
	}
}
