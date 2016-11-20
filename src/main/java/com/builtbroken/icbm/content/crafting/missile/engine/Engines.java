package com.builtbroken.icbm.content.crafting.missile.engine;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.mc.prefab.module.AbstractModule;
import com.builtbroken.mc.prefab.module.ModuleBuilder;
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
    CREATIVE_ENGINE("engine.creative", RocketEngineCreative.class, 0),
    //TODO redo coal engine when mass if fully implemented
    COAL_ENGINE("engine.coal", RocketEngineCoalPowered.class, 500000),
    OIL_ENGINE("engine.oil", RocketEngineOil.class, 104326),
    FUEL_ENGINE("engine.fuel", RocketEngineFuel.class, 90326),
    GUNPOWDER_ENGINE("engine.gunpowder", RocketEngineGunpowder.class, 500);

    /** Mass of the engine without it's fuel */
    public final double mass;

    /** Name used to register and reference the engine module */
    protected final String moduleName;
    /** Class of the module */
    protected final Class<? extends AbstractModule> clazz;

    protected IIcon icon;

    Engines(String name, Class<? extends AbstractModule> clazz, double mass)
    {
        this.moduleName = name;
        this.clazz = clazz;
        this.mass = mass;
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
        stack.getTagCompound().setString(ModuleBuilder.SAVE_ID, "icbm." + moduleName);
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
            MissileModuleBuilder.INSTANCE.register(ICBM.DOMAIN, module.moduleName, module.clazz, true);
        }
    }
}
