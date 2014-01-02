package icbm.sentry.turret.weapon;

import universalelectricity.api.vector.VectorWorld;
import icbm.sentry.interfaces.IWeaponPlatform;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;

/** Modular way of dealing with weapon systems in a way works with different object types
 * 
 * @author DarkGuardsman */
public class WeaponSystem
{
    /** Inventory to pull ammo from */
    protected IInventory inventory;
    /** Object that will shoot the weapon */
    protected IWeaponPlatform shooter;

    /** @param shooter - object that will be shooting the weapon
     * @param inv - inventory to pull ammo from */
    public WeaponSystem(IWeaponPlatform shooter, IInventory inv)
    {
        this.shooter = shooter;
        this.inventory = inv;
    }

    /** Can the weapon be fired */
    public boolean canFire()
    {
        return true;
    }

    /** Fire the weapon at a location */
    public void fire(VectorWorld target)
    {

    }

    /** Fire the weapon at an entity. */
    public void fire(Entity entity)
    {

    }
}
