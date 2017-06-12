package com.builtbroken.icbm.content.crafting.missile.casing;

import net.minecraft.item.ItemStack;

/**
 * Created by robert on 12/29/2014.
 */
public class MissileMedium extends Missile
{
    public MissileMedium(ItemStack stack)
    {
        super(stack, MissileCasings.MEDIUM);
    }

    @Override
    public double getHeight()
    {
        return 12; //TODO check
    }

    @Override
    public double getWidth()
    {
        return 0.5;
    }
}
