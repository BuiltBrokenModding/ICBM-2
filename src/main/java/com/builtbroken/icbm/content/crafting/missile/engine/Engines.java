package com.builtbroken.icbm.content.crafting.missile.engine;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.content.crafting.AbstractModule;
import com.builtbroken.icbm.content.crafting.missile.MissileModuleBuilder;

/**
 * Created by robert on 12/28/2014.
 */
public enum Engines
{
    //Engines
    CREATIVE_ENGINE("creative_engine", RocketEngineCreative.class);

    private final String name;
    private final Class<? extends AbstractModule> clazz;

    private Engines(String name, Class<? extends AbstractModule> clazz)
    {
        this.name = name;
        this.clazz = clazz;
    }

    public AbstractModule newModule()
    {
        return MissileModuleBuilder.INSTANCE.build(ICBM.itemMissileModules.getModuleStackForModule(ICBM.DOMAIN + "." + name));
    }

    public static void register()
    {
        for (Engines module : values())
        {
            MissileModuleBuilder.INSTANCE.register(ICBM.DOMAIN, module.name, module.clazz, true);
        }
    }
}
