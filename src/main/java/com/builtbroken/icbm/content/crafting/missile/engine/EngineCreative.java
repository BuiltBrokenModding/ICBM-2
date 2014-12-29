package com.builtbroken.icbm.content.crafting.missile.engine;

import com.builtbroken.icbm.api.IModuleContainer;
import net.minecraft.item.ItemStack;

/**
 * Created by robert on 12/28/2014.
 */
public class EngineCreative extends Engine
{
    public EngineCreative(ItemStack item)
    {
        super(item);
    }

    @Override
    public ItemStack getRemovedStack(IModuleContainer container)
    {
        return null;
    }
}
