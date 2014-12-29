package com.builtbroken.icbm.content.crafting.missile;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.content.crafting.AbstractModule;
import com.builtbroken.icbm.content.crafting.missile.engine.EngineCreative;
import com.builtbroken.icbm.content.crafting.missile.warhead.WarheadLarge;
import com.builtbroken.icbm.content.crafting.missile.warhead.WarheadMedium;
import com.builtbroken.icbm.content.crafting.missile.warhead.WarheadMicro;
import com.builtbroken.icbm.content.crafting.missile.warhead.WarheadSmall;

/**
 * Created by robert on 12/28/2014.
 */
public enum EnumModule
{
    //Engines
    CREATIVE_ENGINE("creative_engine", EngineCreative.class);

    private final String name;
    private final Class<? extends AbstractModule> clazz;

    private EnumModule(String name, Class<? extends AbstractModule> clazz)
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
        for (EnumModule module : values())
        {
            MissileModuleBuilder.INSTANCE.register(ICBM.DOMAIN, module.name, module.clazz);
        }
    }
}
