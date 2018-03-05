package com.builtbroken.icbm.content.missile.parts.engine;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.api.ICBM_API;
import com.builtbroken.icbm.content.missile.parts.MissileModuleBuilder;
import com.builtbroken.icbm.content.missile.parts.engine.fluid.RocketEngineFuel;
import com.builtbroken.icbm.content.missile.parts.engine.fluid.RocketEngineOil;
import com.builtbroken.icbm.content.missile.parts.engine.solid.RocketEngineCoalPowered;
import com.builtbroken.icbm.content.missile.parts.engine.solid.RocketEngineGunpowder;
import com.builtbroken.mc.prefab.module.AbstractModule;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import java.util.function.Function;

/**
 * Enum of engines implemented by ICBM
 * Created by robert on 12/28/2014.
 */
public enum Engines
{
    //https://en.wikipedia.org/wiki/Rocket_propellant
    //Engines
    CREATIVE_ENGINE("engine.creative", RocketEngineCreative.class, 0, i -> new RocketEngineCreative(i)),
    //TODO redo coal engine when mass if fully implemented
    COAL_ENGINE("engine.coal", RocketEngineCoalPowered.class, 500000, i -> new RocketEngineCoalPowered(i)),
    OIL_ENGINE("engine.oil", RocketEngineOil.class, 104326, i -> new RocketEngineOil(i)),
    FUEL_ENGINE("engine.fuel", RocketEngineFuel.class, 90326, i -> new RocketEngineFuel(i)),
    GUNPOWDER_ENGINE("engine.gunpowder", RocketEngineGunpowder.class, 500, i -> new RocketEngineGunpowder(i));

    /** Mass of the engine without it's fuel */
    public final double mass;

    /** Name used to register and reference the engine module */
    protected final String moduleName;
    /** Class of the module */
    protected final Class<? extends AbstractModule> clazz;

    protected Function<ItemStack, RocketEngine> moduleFactory;

    protected IIcon icon;

    Engines(String name, Class<? extends AbstractModule> clazz, double mass, Function<ItemStack, RocketEngine> moduleFactory)
    {
        this.moduleName = name;
        this.clazz = clazz;
        this.mass = mass;
        this.moduleFactory = moduleFactory;
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
        return new ItemStack(ICBM_API.itemEngineModules, 1, ordinal());
    }

    public RocketEngine newModule()
    {
        return moduleFactory.apply(newModuleStack());
    }

    public RocketEngine buildModule(ItemStack stack)
    {
        return moduleFactory.apply(stack);
    }

    public static void register()
    {
        for (Engines module : values())
        {
            MissileModuleBuilder.INSTANCE.register(ICBM.DOMAIN, module.moduleName, module.clazz, true);
        }
    }
}
