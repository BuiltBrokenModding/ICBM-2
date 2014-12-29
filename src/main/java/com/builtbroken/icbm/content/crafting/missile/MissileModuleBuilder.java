package com.builtbroken.icbm.content.crafting.missile;

import com.builtbroken.icbm.content.crafting.AbstractModule;
import com.builtbroken.icbm.content.crafting.ModuleBuilder;
import com.builtbroken.icbm.content.crafting.missile.casing.Missile;
import com.builtbroken.icbm.content.crafting.missile.engine.Engine;
import com.builtbroken.icbm.content.crafting.missile.engine.Engines;
import com.builtbroken.icbm.content.crafting.missile.guidance.Guidance;
import com.builtbroken.icbm.content.crafting.missile.warhead.*;
import net.minecraft.item.ItemStack;
import resonant.api.explosive.IExplosive;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by robert on 12/28/2014.
 */
public class MissileModuleBuilder extends ModuleBuilder
{
    public static MissileModuleBuilder INSTANCE = new MissileModuleBuilder();

    public HashMap<String, Class<? extends Warhead>> registeredWarheads = new HashMap();
    public HashMap<String, Class<? extends Engine>> registeredEngines = new HashMap();
    public HashMap<String, Class<? extends Guidance>> registeredGuidances = new HashMap();
    public List<String> idToUseWithModuleItem = new ArrayList<String>();


    public boolean register(String mod_id, String name, Class<? extends AbstractModule> clazz, boolean useItem)
    {
        if (this.register(mod_id, name, clazz))
        {
            if (useItem)
            {
                idToUseWithModuleItem.add(mod_id + "." + name);
            }
            return true;
        }
        return false;
    }

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
            else if (Guidance.class.isAssignableFrom(clazz))
            {
                registeredGuidances.put(id, (Class<? extends Guidance>) clazz);
            }
            else
            {
                throw new IllegalArgumentException("MissileModuleBuilder, mod " + mod_id + " registered a module[" + name + " " + clazz + "] that can't be used on the missile");
            }
            return true;
        }
        return false;
    }

    public Missile buildMissile(ItemStack stack)
    {
        return new Missile(stack);
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

    public Missile buildMissile(MissileSizes missileSize, IExplosive ex)
    {
        return this.buildMissile(missileSize, ex, (Engine) Engines.CREATIVE_ENGINE.newModule(), null);
    }

    public Missile buildMissile(MissileSizes missileSize, IExplosive ex, Engine engine, Guidance guidance)
    {
        Missile missile = missileSize.getMissile();
        //Engine
        missile.setEngine(engine);

        //Guidance
        missile.setGuidance(guidance);

        //Warhead
        if(ex != null)
        {
            missile.setWarhead(missileSize.getWarhead(ex));
        }

        return missile;
    }
}
