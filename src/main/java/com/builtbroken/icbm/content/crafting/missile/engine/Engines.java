package com.builtbroken.icbm.content.crafting.missile.engine;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.content.crafting.AbstractModule;
import com.builtbroken.icbm.content.crafting.ModuleBuilder;
import com.builtbroken.icbm.content.crafting.missile.MissileModuleBuilder;
import com.builtbroken.icbm.content.crafting.missile.engine.solid.RocketEngineCoalPowered;
import com.builtbroken.mc.lib.helper.recipe.UniversalRecipe;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraftforge.oredict.ShapedOreRecipe;

/**
 * Created by robert on 12/28/2014.
 */
public enum Engines
{
    //Engines
    CREATIVE_ENGINE("engine.creative", RocketEngineCreative.class),
    COAL_ENGINE("engine.coal", RocketEngineCoalPowered.class);

    protected final String name;
    protected final Class<? extends AbstractModule> clazz;

    protected IIcon icon;

    Engines(String name, Class<? extends AbstractModule> clazz)
    {
        this.name = name;
        this.clazz = clazz;
    }

    public static Engines get(ItemStack stack)
    {
        return get(stack.getItemDamage());
    }

    public static Engines get(int meta)
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

    public RocketEngine newModule()
    {
        return MissileModuleBuilder.INSTANCE.buildEngine(newModuleStack());
    }

    public static void register()
    {
        for (Engines module : values())
        {
            MissileModuleBuilder.INSTANCE.register(ICBM.DOMAIN, module.name, module.clazz, true);
        }
        GameRegistry.addRecipe(new ShapedOreRecipe(Engines.COAL_ENGINE.newModuleStack(), "c", "f", "h", 'c', UniversalRecipe.CIRCUIT_T1.get(), 'f', Blocks.furnace, 'h', Blocks.hopper));
    }
}
