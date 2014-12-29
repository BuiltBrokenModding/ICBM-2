package com.builtbroken.icbm.content.crafting;

import com.builtbroken.icbm.api.IModuleContainer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
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

    /** Called when the module is installed */
    public void onAdded(IModuleContainer container)
    {

    }

    /** Called when the module is removed */
    public void onRemoved(IModuleContainer container)
    {

    }

    /** Called to get the stack to return when removing the module */
    public ItemStack getRemovedStack(IModuleContainer container)
    {
        return toStack();
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIcon(ItemStack stack, int pass)
    {
        return null;
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register)
    {

    }
}
