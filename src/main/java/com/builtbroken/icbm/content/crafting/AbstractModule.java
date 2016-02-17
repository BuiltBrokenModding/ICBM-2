package com.builtbroken.icbm.content.crafting;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.mc.api.modules.IModule;
import com.builtbroken.mc.api.modules.IModuleContainer;
import com.builtbroken.icbm.content.crafting.missile.MissileModuleBuilder;
import com.builtbroken.mc.api.ISave;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;

/**
 * Created by robert on 12/28/2014.
 */
public class AbstractModule implements ISave, IModule
{
    /** ItemStack that represents this module */
    protected ItemStack item;
    protected String name;

    public AbstractModule(ItemStack item, String name)
    {
        this.item = item.copy();
        this.name = name;
    }

    public String getUnlocalizedName()
    {
        return "module." + ICBM.PREFIX + name;
    }

    /** Loads from the item's NBT */
    public final AbstractModule load()
    {
        load(item);
        return this;
    }

    /** Loads from an ItemStack's NBT, can be used to clone modules */
    public final void load(ItemStack stack)
    {
        if(stack.getTagCompound() != null)
            load(stack.getTagCompound());
    }

    public final ItemStack save()
    {
        ItemStack stack = item.copy();
        save(stack);
        return stack;
    }

    /** Saves to an ItemStack's NBT, can be used to clone to an itemstakc */
    public final void save(ItemStack stack)
    {
        stack.setTagCompound(new NBTTagCompound());
        save(stack.getTagCompound());

        stack.getTagCompound().setString(ModuleBuilder.SAVE_ID, MissileModuleBuilder.INSTANCE.getID(this));
    }

    public ItemStack toStack()
    {
        ItemStack stack = item.copy();
        save(stack);
        return stack;
    }

    @Override
    public void load(NBTTagCompound nbt)
    {

    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbt)
    {
        return nbt;
    }

    /** Called when the module is installed */
    public void onAdded(IModuleContainer container)
    {

    }

    /** Called when the module is removed */
    public void onRemoved(IModuleContainer container)
    {

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
