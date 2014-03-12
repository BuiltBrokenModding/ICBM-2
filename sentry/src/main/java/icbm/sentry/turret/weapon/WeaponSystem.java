package icbm.sentry.turret.weapon;

import icbm.sentry.interfaces.IWeaponSystem;
import icbm.sentry.turret.Turret;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import universalelectricity.api.vector.Vector3;

/** Modular way of dealing with weapon systems in a way works with different object types
 * 
 * @author DarkGuardsman, tgame14 */
public abstract class WeaponSystem implements IWeaponSystem
{
    protected Turret turret;
    protected float defaultTraceRange = 100;

    public WeaponSystem(Turret sentry)
    {
        this.turret = sentry;
    }

    @Override
    public void fire(Vector3 target)
    {
        doFire(target);
    }

    @Override
    public void fire(Entity entity)
    {
        fire(Vector3.fromCenter(entity));
    }

    /** Internal version of fire(Vector3) allowing repeat fire events */
    protected void doFire(Vector3 target)
    {
        Vector3 hit = target.clone();
        MovingObjectPosition endTarget = getBarrelEnd().rayTrace(turret.getHost().world(), hit, true);
        if (endTarget != null)
        {
            if (endTarget.typeOfHit == EnumMovingObjectType.ENTITY)
            {
                onHitEntity(endTarget.entityHit);
            }
            else if (endTarget.typeOfHit == EnumMovingObjectType.TILE)
            {
                onHitBlock(new Vector3(endTarget.hitVec));
            }
        }
    }

    @Override
    public void fireClient(Vector3 hit)
    {
        drawParticleStreamTo(turret.world(), getBarrelEnd(), hit);
    }

    /** Draws a particle stream towards a location.
     * 
     * @author Based on MachineMuse */
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
    protected void onHitEntity(Entity entity)
    {

    }

    /** Called when the weapon hits a block */
    protected void onHitBlock(Vector3 block)
    {

    }

    @Override
    public boolean canFire()
    {
        return true;
    }
}
