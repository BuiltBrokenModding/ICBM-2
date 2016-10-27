package com.builtbroken.icbm.content.crafting.missile.casing;

import com.builtbroken.mc.api.modules.IModule;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * Created by robert on 12/29/2014.
 */
public class MissileLarge extends Missile
{
    public MissileLarge(ItemStack stack)
    {
        super(stack, MissileCasings.LARGE);
    }

    @Override
    public double getHeight()
    {
        return 0;
    }

    @Override
    public double getWidth()
    {
        return 0;
    }
}
