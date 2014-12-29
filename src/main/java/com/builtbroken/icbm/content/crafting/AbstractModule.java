package com.builtbroken.icbm.content.crafting;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import resonant.api.ISave;
import resonant.lib.mod.AbstractMod;

/**
 * Created by robert on 12/28/2014.
 */
public class AbstractModule implements ISave
{
    /** ItemStack that represents this module */
    protected ItemStack item;

    public AbstractModule(ItemStack item)
    {
        this.item = item;
    }

    /** Loads from the item's NBT */
    public final ItemStack load()
    {
        load(item);
        return item;
    }

    /** Loads from an ItemStack's NBT, can be used to clone modules */
    public final void load(ItemStack stack)
    {
        if(stack.getTagCompound() != null)
            load(stack.getTagCompound());
    }

    public final ItemStack save()
    {
        save(item);
        return item;
    }

    /** Saves to an ItemStack's NBT, can be used to clone to an itemstakc */
    public final void save(ItemStack stack)
    {
        if(stack.getTagCompound() == null)
            stack.setTagCompound(new NBTTagCompound());
        save(stack.getTagCompound());
    }

    public ItemStack toStack()
    {
        save(item);
        return item;
    }

    @Override
    public void load(NBTTagCompound nbt)
    {

    }

    @Override
    public void save(NBTTagCompound nbt)
    {

    }
}
