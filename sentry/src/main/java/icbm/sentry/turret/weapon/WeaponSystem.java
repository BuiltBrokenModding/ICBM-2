package icbm.sentry.turret.weapon;

import icbm.sentry.ICBMSentry;
import icbm.sentry.interfaces.ISentry;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MovingObjectPosition;
import universalelectricity.api.vector.Vector3;

/** Modular way of dealing with weapon systems in a way works with different object types
 * 
 * @author DarkGuardsman, tgame14 */
public abstract class WeaponSystem
{
    protected ISentry sentry;
    protected float defaultTraceTrange = 100;

    public WeaponSystem(ISentry sentry)
    {
        this.sentry = sentry;
    }

    /** Fires the weapon from the hip without a target */
    public void fire(float pitch, float yaw)
    {
        this.fire(pitch, yaw, -1);
    }

    /** Fires the weapon from the hip without a target */
    public void fire(float pitch, float yaw, float traceLimit)
    {
        Vector3 extend = sentry.getAimOffset().clone();
        extend.rotate(sentry.getHost().yaw(), sentry.getHost().pitch());
        extend.scale(traceLimit > 0 ? traceLimit : defaultTraceTrange);
        extend.translate(sentry.getHost().x(), sentry.getHost().y(), sentry.getHost().z());
        extend.translate(sentry.getCenterOffset());

        this.fire(extend);
    }

    /** Fires the weapon at a location */
    public void fire(Vector3 target)
    {
        Vector3 hit = target.clone();
        Vector3 barrel = this.sentry.getCenterOffset();
        barrel.add(this.sentry.getAimOffset());
        barrel.rotate(this.sentry.getHost().yaw(), this.sentry.getHost().pitch());
        barrel.add(new Vector3(sentry.getHost().x(), sentry.getHost().y(), sentry.getHost().z()));

        MovingObjectPosition endTarget = barrel.rayTrace(sentry.getHost().world(), hit, true);

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
        ICBMSentry.proxy.renderBeam(sentry.getHost().world(), barrel, hit, 1F, 1F, 1F, 10);
    }

    /** Fires the weapon at an entity. */
    public void fire(Entity entity)
    {
        this.fire(new Vector3(entity).translate(0, entity.getEyeHeight(), 0));
    }

    /** Called when the weapon hits an entity */
    public void onHitEntity(Entity entity)
    {

    }

    /** Called when the weapon hits a block */
    public void onHitBlock(Vector3 block)
    {

    }
}
