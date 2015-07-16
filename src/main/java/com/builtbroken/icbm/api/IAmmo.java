package com.builtbroken.icbm.api;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

/**
 * Created by robert on 12/28/2014.
 */
//TODO move to common core
public interface IAmmo
{
    /**
     * Is the itemstack ammo for a weapon
     *
     * @param stack - stack that is ammo
     * @return true if it is ammo
     */
    boolean isAmmo(ItemStack stack);

    /**
     * Is the ammo item a clip of ammo rather than single rounds
     * If you return true the item will be treated as a clip.
     * Stack size will not be used to count how many rounds are
     * in the stack. Instead getAmmoCount will be called to
     * get how many rounds are left in the clip.
     *
     * @return true if it is a clip item.
     */
    boolean isClip(ItemStack stack);

    /**
     * Gets the type of ammo. For example
     *
     * @param stack - stack that is ammo
     * @return type
     */
    IAmmoType getAmmoType(ItemStack stack);

    /**
     * How many rounds are in the stack.
     * called if isClip returns true
     *
     * @return number of rounds in the stack, or stack
     * size if you don't use clip based logic
     */
    int getAmmoCount(ItemStack ammoStack);

    /**
     * Called when the bullet is fired by an entity
     *
     * @param weapon       - weapon it was fired from
     * @param ammoStack    - ammo stack that was fired
     * @param firingEntity - entity that will be used for rotation, position, and source
     *                     of any damage caused by the bullet.
     */
    void fireAmmo(IWeapon weapon, ItemStack weaponStack, ItemStack ammoStack, Entity firingEntity);

    /**
     * Called to consume ammo from the stack. Use this to define how ammo is consumed
     * when using stack size is not enough.
     *
     * @param weapon     - weapon the round was fired from
     * @param ammoStack  - ammo that was fired
     * @param shotsFired - number of shots that were fired
     */
    void consumeAmmo(IWeapon weapon, ItemStack weaponStack, ItemStack ammoStack, int shotsFired);


}
