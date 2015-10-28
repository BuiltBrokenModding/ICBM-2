package com.builtbroken.icbm.content.crafting.missile.warhead;

import com.builtbroken.mc.api.explosive.IExplosiveHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by robert on 12/28/2014.
 */
public class WarheadLarge extends Warhead
{
    public WarheadLarge(ItemStack warhead)
    {
        super(warhead, WarheadCasings.EXPLOSIVE_LARGE);
    }
}
