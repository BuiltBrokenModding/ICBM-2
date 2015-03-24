package com.builtbroken.icbm.content.crafting.missile;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.content.crafting.AbstractModule;
import com.builtbroken.icbm.content.crafting.ModuleBuilder;
import com.builtbroken.icbm.content.crafting.missile.casing.Missile;
import com.builtbroken.icbm.content.crafting.missile.casing.MissileCasings;
import com.builtbroken.icbm.content.crafting.missile.engine.RocketEngine;
import com.builtbroken.icbm.content.crafting.missile.engine.Engines;
import com.builtbroken.icbm.content.crafting.missile.guidance.Guidance;
import com.builtbroken.icbm.content.crafting.missile.warhead.Warhead;
import com.builtbroken.icbm.content.crafting.missile.warhead.WarheadCasings;
import com.builtbroken.mc.api.explosive.IExplosiveHandler;
import net.minecraft.item.ItemStack;

import java.lang.reflect.InvocationTargetException;
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
    public HashMap<String, Class<? extends RocketEngine>> registeredEngines = new HashMap();
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
            else if (RocketEngine.class.isAssignableFrom(clazz))
            {
                registeredEngines.put(id, (Class<? extends RocketEngine>) clazz);
            }
            else if (Guidance.class.isAssignableFrom(clazz))
            {
                registeredGuidances.put(id, (Class<? extends Guidance>) clazz);
            }
            return true;
        }
        return false;
    }

    public Missile buildMissile(ItemStack stack)
    {
        AbstractModule module = super.build(stack);
        if (module instanceof Missile)
        {
            return (Missile) module;
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

    public RocketEngine buildEngine(ItemStack stack)
    {
        AbstractModule module = super.build(stack);
        if (module instanceof RocketEngine)
        {
            return (RocketEngine) module;
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

    public Missile buildMissile(MissileCasings missileSize, IExplosiveHandler ex)
    {
        return this.buildMissile(missileSize, ex, (RocketEngine) Engines.CREATIVE_ENGINE.newModule(), null);
    }

    public Warhead buildWarhead(WarheadCasings size, IExplosiveHandler ex)
    {
        try
        {

            Warhead warhead = size.warhead_clazz.getConstructor(ItemStack.class).newInstance(new ItemStack(ICBM.blockExplosive, 1, size.ordinal()));
            warhead.ex = ex;
            return warhead;
        } catch (InstantiationException e)
        {
            e.printStackTrace();
        } catch (IllegalAccessException e)
        {
            e.printStackTrace();
        } catch (NoSuchMethodException e)
        {
            e.printStackTrace();
        } catch (InvocationTargetException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public Missile buildMissile(MissileCasings missileSize, IExplosiveHandler ex, RocketEngine engine, Guidance guidance)
    {
        try
        {
            Missile missile = missileSize.missile_clazz.getConstructor(ItemStack.class).newInstance(new ItemStack(ICBM.itemMissile, 1, missileSize.ordinal()));

            //Engine
            missile.setEngine(engine);

            //Guidance
            missile.setGuidance(guidance);

            //Warhead
            if (ex != null)
            {
                missile.setWarhead(buildWarhead(missileSize.warhead_casing, ex));
            }

            return missile;
        } catch (InstantiationException e)
        {
            e.printStackTrace();
        } catch (IllegalAccessException e)
        {
            e.printStackTrace();
        } catch (NoSuchMethodException e)
        {
            e.printStackTrace();
        } catch (InvocationTargetException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
