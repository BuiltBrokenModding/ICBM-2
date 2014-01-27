package icbm.sentry.turret.weapon;

import icbm.sentry.turret.sentryhandler.Sentry;
import net.minecraft.entity.Entity;
import universalelectricity.api.vector.Vector3;
import universalelectricity.api.vector.VectorWorld;

/** Modular way of dealing with weapon systems in a way works with different object types
 * 
 * @author DarkGuardsman */
public class WeaponSystem
{
    protected Sentry sentry;

    public WeaponSystem(Sentry sentry)
    {
        this.sentry = sentry;
    }

    /** Fire the weapon at a location */
    public void fire(VectorWorld target)
    {

    }

    /** Fire the weapon at an entity. */
    public void fire(Entity entity)
    {
        this.fire((VectorWorld) new Vector3(entity).translate(0, entity.getEyeHeight(), 0));
    }
}
