package com.builtbroken.icbm.api.crafting;

import net.minecraft.item.ItemStack;

/** Applied to missile items that support the missile workstation
 * Created by robert on 3/12/2015.
 */
public interface IModularMissileItem
{
    /** Grabs the item that is used as the missile's engine */
    ItemStack getEngine(ItemStack missile);

    /**
     * Sets the engine itemstack
     * @param stack - item stack that is most likely an instance of IModuleItem. In
     *              which the module is an instance of Engine
     * @param simulate - if true then we are checking for can insert item
     * @return true if everything went correctly
     */
    boolean setEngine(ItemStack missile, ItemStack stack, boolean simulate);

    /** Grabs the item that is used as the missile's warhead */
    ItemStack getWarhead(ItemStack missile);

    /**
     * Sets the warhead itemstack
     * @param stack - item stack that is most likely an instance of IModuleItem. In
     *              which the module is an instance of Warhead
     * @param simulate - if true then we are checking for can insert item
     * @return true if everything went correctly
     */
    boolean setWarhead(ItemStack missile, ItemStack stack, boolean simulate);

    /** Grabs the item that is used as the missile's guidance */
    ItemStack getGuidance(ItemStack missile);

    /**
     * Sets the guidance itemstack
     * @param stack - item stack that is most likely an instance of IModuleItem. In
     *              which the module is an instance of guidance
     * @param simulate - if true then we are checking for can insert item
     * @return true if everything went correctly
     */
    boolean setGuidance(ItemStack missile, ItemStack stack, boolean simulate);
}
