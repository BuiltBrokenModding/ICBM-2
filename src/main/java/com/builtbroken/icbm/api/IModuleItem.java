package com.builtbroken.icbm.api;

import com.builtbroken.icbm.content.crafting.AbstractModule;
import net.minecraft.item.ItemStack;

/**
 * Created by robert on 12/28/2014.
 */
public interface IModuleItem
{
    /** Gets the module that this item represents */
    AbstractModule getModule(ItemStack stack);
}
