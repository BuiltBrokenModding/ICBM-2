package icbm.sentry.interfaces;

import universalelectricity.api.vector.VectorWorld;

/** Apply this to object that will act as a host to a weapon system
 * 
 * @author DarkGuardsman */
public interface IWeaponPlatform
{
    /** Gets the direction and world the object is aiming in. Should only be outside of the collision
     * box of the object so ray tracing will work. */
    public VectorWorld getAimingDirection();

    /** Gets the weapons systems and slots that can host weapons systems */
    public IWeaponSystem[] getWeaponSystems();

    /** Used to see if a weapon can be placed in the weapon system slot
     * 
     * @param slot - index in the weapon system array
     * @param system - system that is looking to be added */
    public boolean canSupportWeaponSystem(int slot, IWeaponSystem system);

    /** Adds a weapon system to the slot
     * 
     * @param slot - index in the weapon system array
     * @param system - weapon system that is being added
     * @return true if its added */
    public boolean addWeaponSystem(int slot, IWeaponSystem system);

    /** Removes a weapon system from the slot
     * 
     * @param slot - index in the weapon system array
     * @param system - weapon system that is being added
     * @return true if its been removed */
    public boolean removeWeaponSystem(int slot, IWeaponSystem system);
}
