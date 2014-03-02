package icbm.sentry.turret.weapon;

import icbm.sentry.ICBMSentry;
import icbm.sentry.turret.Turret;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import universalelectricity.api.vector.Vector3;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Modular way of dealing with weapon systems in a way works with different object types
 * 
 * @author DarkGuardsman, tgame14
 */
public abstract class WeaponSystem
{
	protected Turret turret;
	protected float defaultTraceRange = 100;

	public WeaponSystem(Turret sentry)
	{
		this.turret = sentry;
	}

	/** Fires the weapon from the hip without a target */
	public void fire(float pitch, float yaw)
	{
		this.fire(pitch, yaw, -1);
	}

	/** Fires the weapon from the hip without a target */
	public void fire(float pitch, float yaw, float traceLimit)
	{
		Vector3 extend = turret.getAimOffset().clone();
		extend.rotate(turret.getServo().yaw, turret.getServo().pitch);
		extend.scale(traceLimit > 0 ? traceLimit : defaultTraceRange);
		extend.translate(turret.getHost().x(), turret.getHost().y(), turret.getHost().z());
		extend.translate(turret.getCenterOffset());
		this.fire(extend);
	}

	/** Fires the weapon at a location */
	public void fire(Vector3 target)
	{
		Vector3 hit = target.clone();
		MovingObjectPosition endTarget = getBarrelEnd().rayTrace(turret.getHost().world(), hit, true);

		if (endTarget != null)
		{
			hit = new Vector3(endTarget);
			if (endTarget.typeOfHit == EnumMovingObjectType.ENTITY)
			{
				onHitEntity(endTarget.entityHit);
			}
			else if (endTarget.typeOfHit == EnumMovingObjectType.TILE)
			{
				onHitBlock(hit);
			}
		}
	}

	/** Fires the weapon at an entity. */
	public void fire(Entity entity)
	{
		this.fire(Vector3.fromCenter(entity));
	}

	public void fireClient(Vector3 hit)
	{
		drawParticleStreamTo(turret.world(), getBarrelEnd(), hit);
	}

	/**
	 * Draws a particle stream towards a location.
	 * 
	 * @author Based on MachineMuse
	 */
	public void drawParticleStreamTo(World world, Vector3 start, Vector3 target)
	{
		Vector3 direction = start.toAngle(target).toVector();
		double scale = 0.02;
		Vector3 currentPoint = start.clone();
		Vector3 difference = target.clone().difference(start);
		double magnitude = difference.getMagnitude();

		while (currentPoint.distance(target) > scale)
		{
			world.spawnParticle("townaura", currentPoint.x, currentPoint.y, currentPoint.z, 0.0D, 0.0D, 0.0D);
			currentPoint.add(difference.clone().scale(scale));
		}
	}

	/** Gets the current end point for the barrel in the world */
	protected Vector3 getBarrelEnd()
	{
		return turret.getAbsoluteCenter().translate(turret.getAimOffset());
	}

	/** Called when the weapon hits an entity */
	public void onHitEntity(Entity entity)
	{

	}

	/** Called when the weapon hits a block */
	public void onHitBlock(Vector3 block)
	{

	}

	/**
	 * Ammunition check
	 * 
	 * @return
	 */
	public boolean canFire()
	{
		return true;
	}
}
