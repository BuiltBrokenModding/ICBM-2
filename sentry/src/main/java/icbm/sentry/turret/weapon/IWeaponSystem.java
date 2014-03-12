package icbm.sentry.turret.weapon;

import net.minecraft.entity.Entity;
import universalelectricity.api.vector.Vector3;

/** Applied to objects that act as a weapons. Most cases these methods will use an object as the
 * starting point or host for firing events.
 * 
 * @author DarkGuardsman */
public interface IWeaponSystem
{
    /** called to fire at a location */
    public void fire(Vector3 vector);

    /** called to fire at an entity, should just pass into fire(Vector3) */
    public void fire(Entity entity);

    /** called client side to render effect of the weapon firing */
    public void fireClient(Vector3 hit);

    /** checks to see if the weapon can fire */
    public boolean canFire();
}
