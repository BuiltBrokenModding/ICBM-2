package com.builtbroken.icbm.content.launcher.items;

import com.builtbroken.mc.api.IWorldPosition;
import com.builtbroken.mc.api.items.IWorldPosItem;
import com.builtbroken.mc.lib.transform.vector.Location;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by Dark on 6/2/2015.
 */
public class ItemGPSData extends Item implements IWorldPosItem
{
    @Override
    public Location getLocation(ItemStack stack)
    {
        if(stack.getItem() == this && stack.hasTagCompound() && stack.getTagCompound().hasKey("linkPos"))
        {
            return new Location(stack.getTagCompound().getCompoundTag("linkPos"));
        }
        return null;
    }

    @Override
    public void setLocation(ItemStack stack, IWorldPosition loc)
    {
        if(stack.getItem() == this)
        {
            if (!stack.hasTagCompound())
                stack.setTagCompound(new NBTTagCompound());
            stack.getTagCompound().setTag("linkPos", new Location(loc).toNBT());
        }
    }

    @Override
    public boolean canAccessLocation(ItemStack stack, Object obj)
    {
        return false;
    }
}
