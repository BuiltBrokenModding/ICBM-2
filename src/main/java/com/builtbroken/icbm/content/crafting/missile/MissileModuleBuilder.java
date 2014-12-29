package com.builtbroken.icbm.content.crafting.missile;

import com.builtbroken.icbm.content.crafting.AbstractModule;
import com.builtbroken.icbm.content.crafting.ModuleBuilder;
import com.builtbroken.icbm.content.crafting.missile.casing.MissileCasing;
import com.builtbroken.icbm.content.crafting.missile.engine.Engine;
import com.builtbroken.icbm.content.crafting.missile.guidance.Guidance;
import com.builtbroken.icbm.content.crafting.missile.warhead.Warhead;
import net.minecraft.item.ItemStack;

import java.util.HashMap;

/**
 * Created by robert on 12/28/2014.
 */
public class MissileModuleBuilder extends ModuleBuilder
{
    public static MissileModuleBuilder INSTANCE = new MissileModuleBuilder();

    public HashMap<String, Class<? extends Warhead>> registeredWarheads = new HashMap();
    public HashMap<String, Class<? extends Engine>> registeredEngines = new HashMap();
    public HashMap<String, Class<? extends MissileCasing>> registeredCasings = new HashMap();
    public HashMap<String, Class<? extends Guidance>> registeredGuidances = new HashMap();

    @Override
    public boolean register(String mod_id, String name, Class<? extends AbstractModule> clazz)
    {
        if (super.register(mod_id, name, clazz))
        {
            String id = mod_id + "." + name;
            if (Warhead.class.isAssignableFrom(clazz))
            {
                registeredWarheads.put(id, (Class<? extends Warhead>) clazz);
            }
            else if (Engine.class.isAssignableFrom(clazz))
            {
                registeredEngines.put(id, (Class<? extends Engine>) clazz);
            }
            else if (MissileCasing.class.isAssignableFrom(clazz))
            {
                registeredCasings.put(id, (Class<? extends MissileCasing>) clazz);
            }
            else if (Guidance.class.isAssignableFrom(clazz))
            {
                registeredGuidances.put(id, (Class<? extends Guidance>) clazz);
            }
            else
            {
                throw new IllegalArgumentException("MissileModuleBuilder, mod " + mod_id +" registered a module[" + name + " " + clazz + "] that can't be used on the missile");
            }
            return true;
        }
        return false;
    }

    public Missile buildMissile(ItemStack stack)
    {
        return new Missile(stack);
    }

    public MissileCasing buildCasing(ItemStack stack)
    {
        AbstractModule module = super.build(stack);
        if (module instanceof MissileCasing)
        {
            return (MissileCasing) module;
        }
        return null;
    }

    public Warhead buildWarhead(ItemStack stack)
    {
        AbstractModule module = super.build(stack);
        if (module instanceof Warhead)
        {
            return (Warhead) module;
        }
        return null;
    }

    public Engine buildEngine(ItemStack stack)
    {
        AbstractModule module = super.build(stack);
        if (module instanceof Engine)
        {
            return (Engine) module;
        }
        return null;
    }

    public Guidance buildGuidance(ItemStack stack)
    {
        AbstractModule module = super.build(stack);
        if (module instanceof Guidance)
        {
            return (Guidance) module;
        }
        return null;
    }
}
