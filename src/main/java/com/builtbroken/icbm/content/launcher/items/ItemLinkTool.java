package com.builtbroken.icbm.content.launcher.items;

import com.builtbroken.mc.api.IWorldPosition;
import com.builtbroken.mc.api.items.IPassCodeItem;
import com.builtbroken.mc.api.items.IWorldPosItem;
import com.builtbroken.mc.api.tile.IPassCode;
import com.builtbroken.mc.lib.transform.vector.Location;
import net.minecraft.dispenser.ILocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * Created by robert on 4/15/2015.
 */
public class ItemLinkTool extends Item implements IWorldPosItem, IPassCodeItem
{
    public ItemLinkTool()
    {
        this.setMaxStackSize(1);
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hit_x, float hit_y, float hit_z)
    {
        if(player.isSneaking())
        {
            Location location = new Location(world, x, y, z);
            setLocation(stack, location);
            if(location.getTileEntity() instanceof IPassCode)
            {
                setCode(stack, ((IPassCode) location.getTileEntity()).getCode());
            }
            return true;
        }
        return false;
    }

    @Override
    public short getCode(ItemStack stack)
    {
        if(stack.getItem() == this && stack.hasTagCompound() && stack.getTagCompound().hasKey("passShort"))
        {
            return stack.getTagCompound().getShort("passShort");
        }
        return 0;
    }

    @Override
    public void setCode(ItemStack stack, short code)
    {
        if(stack.getItem() == this)
        {
            if(!stack.hasTagCompound())
                stack.setTagCompound(new NBTTagCompound());
            stack.getTagCompound().setShort("passShort", code);
        }
    }

    @Override
    public IWorldPosition getLocation(ItemStack stack)
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
}
