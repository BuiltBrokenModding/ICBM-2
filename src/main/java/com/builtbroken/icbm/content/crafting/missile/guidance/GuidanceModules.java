package com.builtbroken.icbm.content.crafting.missile.guidance;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.content.crafting.AbstractModule;
import com.builtbroken.icbm.content.crafting.ModuleBuilder;
import com.builtbroken.icbm.content.crafting.missile.MissileModuleBuilder;
import com.builtbroken.icbm.content.crafting.missile.engine.RocketEngineCreative;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;

/**
 * Created by robert on 12/28/2014.
 */
public enum GuidanceModules
{
    //Engines
    CREATIVE_ENGINE("engine.creative", RocketEngineCreative.class);

    protected final String name;
    protected final Class<? extends AbstractModule> clazz;

    protected IIcon icon;

    GuidanceModules(String name, Class<? extends AbstractModule> clazz)
    {
        this.name = name;
        this.clazz = clazz;
    }

    public static GuidanceModules get(ItemStack stack)
    {
        return get(stack.getItemDamage());
    }

    public static GuidanceModules get(int meta)
    {
        if (meta >= 0 && meta < values().length)
        {
            return values()[meta];
        }
        return null;
    }

    public ItemStack newModuleStack()
    {
        ItemStack stack = new ItemStack(ICBM.itemEngineModules, 1, ordinal());
        stack.setTagCompound(new NBTTagCompound());
        stack.getTagCompound().setString(ModuleBuilder.SAVE_ID, "icbm." + name);
        return stack;
    }

    public Guidance newModule()
    {
        return MissileModuleBuilder.INSTANCE.buildGuidance(newModuleStack());
    }

    public static void register()
    {
    }
}
