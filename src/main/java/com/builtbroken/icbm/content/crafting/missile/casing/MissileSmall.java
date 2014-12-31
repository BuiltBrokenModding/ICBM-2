package com.builtbroken.icbm.content.crafting.missile.casing;

import com.builtbroken.icbm.content.crafting.missile.MissileSizes;
import net.minecraft.item.ItemStack;

/**
 * Created by robert on 12/29/2014.
 */
public class MissileSmall extends Missile
{
    public MissileSmall(ItemStack stack)
    {
        super(stack, MissileSizes.SMALL);
    }
}
