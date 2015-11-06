package com.builtbroken.icbm.content.crafting.missile.engine;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.content.crafting.AbstractModule;
import com.builtbroken.icbm.content.crafting.ModuleBuilder;
import com.builtbroken.icbm.content.crafting.missile.MissileModuleBuilder;
import com.builtbroken.icbm.content.crafting.missile.engine.fluid.RocketEngineFuel;
import com.builtbroken.icbm.content.crafting.missile.engine.fluid.RocketEngineOil;
import com.builtbroken.icbm.content.crafting.missile.engine.solid.RocketEngineCoalPowered;
import com.builtbroken.icbm.content.crafting.missile.engine.solid.RocketEngineGunpowder;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;

/**
 * Enum of engines implemented by ICBM
 * Created by robert on 12/28/2014.
 */
public enum Engines
{
    //https://en.wikipedia.org/wiki/Rocket_propellant
    //Engines
    CREATIVE_ENGINE("engine.creative", RocketEngineCreative.class),
    COAL_ENGINE("engine.coal", RocketEngineCoalPowered.class),
    OIL_ENGINE("engine.oil", RocketEngineOil.class),
    FUEL_ENGINE("engine.fuel", RocketEngineFuel.class),
    GUNPOWDER_ENGINE("engine.gunpowder", RocketEngineGunpowder.class);

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
    }
}
