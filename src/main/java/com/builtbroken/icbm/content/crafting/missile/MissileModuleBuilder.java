package com.builtbroken.icbm.content.crafting.missile;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.content.crafting.AbstractModule;
import com.builtbroken.icbm.content.crafting.ModuleBuilder;
import com.builtbroken.icbm.content.crafting.missile.casing.Missile;
import com.builtbroken.icbm.content.crafting.missile.casing.MissileCasings;
import com.builtbroken.icbm.content.crafting.missile.engine.Engines;
import com.builtbroken.icbm.content.crafting.missile.engine.RocketEngine;
import com.builtbroken.icbm.content.crafting.missile.guidance.Guidance;
import com.builtbroken.icbm.content.crafting.missile.guidance.GuidanceModules;
import com.builtbroken.icbm.content.crafting.missile.warhead.Warhead;
import com.builtbroken.icbm.content.crafting.missile.warhead.WarheadCasings;
import com.builtbroken.mc.api.explosive.IExplosiveHandler;
import com.builtbroken.mc.api.modules.IModule;
import com.builtbroken.mc.lib.world.explosive.ExplosiveRegistry;
import com.builtbroken.mc.prefab.items.ItemStackWrapper;
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
        IModule module = super.build(stack);
        if (module instanceof Missile)
        {
            return (Missile) module;
        }
        return null;
    }

    public Warhead buildWarhead(ItemStack stack)
    {
        IModule module = super.build(stack);
        if (module instanceof Warhead)
        {
            return (Warhead) module;
        }
        return null;
    }

    public RocketEngine buildEngine(ItemStack stack)
    {
        IModule module = super.build(stack);
        if (module instanceof RocketEngine)
        {
            return (RocketEngine) module;
        }
        return null;
    }

    public Guidance buildGuidance(ItemStack stack)
    {
        IModule module = super.build(stack);
        if (module instanceof Guidance)
        {
            return (Guidance) module;
        }
        return null;
    }

    @Deprecated
    public Missile buildMissile(MissileCasings missileSize, IExplosiveHandler ex)
    {
        return this.buildMissile(missileSize, getExplosiveItem(ex));
    }

    /**
     * Builds a missile with the explosive item. Will create all modules required to function. This
     * includes the warhead for the explosive to be inserted into.
     *
     * @param missileSize - casing
     * @param ex          - explosive item, is not validated.
     * @return new Missile
     */
    public Missile buildMissile(MissileCasings missileSize, ItemStack ex)
    {
        return this.buildMissile(missileSize, ex, Engines.CREATIVE_ENGINE.newModule(), GuidanceModules.CHIP_THREE.newModule());
    }

    /**
     * Builds a warhead using a size and explosive handler
     *
     * @param size - casing size
     * @param ex   - handler, will use this to find an item to use before using directly.
     * @return new Warhead instance of the size
     * @Deprecated In favor of creating warhead directly with an explosive item
     */
    @Deprecated
    public Warhead buildWarhead(WarheadCasings size, IExplosiveHandler ex)
    {
        //TODO replace reflection
        try
        {
            Warhead warhead = size.warhead_clazz.getConstructor(ItemStack.class).newInstance(new ItemStack(ICBM.blockWarhead, 1, size.ordinal()));

            //Set explosive item, instead of just explosive
            ItemStack explosive = getExplosiveItem(ex);
            if (explosive != null)
            {
                warhead.setExplosiveStack(explosive);
            }
            else
            {
                warhead.setExplosive(ex, -1, null);
            }
            return warhead;
        }
        catch (InstantiationException e)
        {
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
        catch (NoSuchMethodException e)
        {
            e.printStackTrace();
        }
        catch (InvocationTargetException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private ItemStack getExplosiveItem(IExplosiveHandler ex)
    {
        List<ItemStackWrapper> stacks = ExplosiveRegistry.getItems(ex);
        if (stacks != null && stacks.size() > 0)
        {
            return stacks.get(0).itemStack;
        }
        return null;
    }

    /**
     * Builds a warhead using a size and explosive handler
     *
     * @param size      - casing size
     * @param explosive - item to use as an explosive, does not check if valid
     * @return new Warhead instance of the size
     */
    public Warhead buildWarhead(WarheadCasings size, ItemStack explosive)
    {
        Warhead warhead = buildWarhead(size);
        if (explosive != null)
        {
            warhead.setExplosiveStack(explosive);
        }
        return warhead;
    }

    /**
     * Builds a warhead using a size given
     *
     * @param size - casing size
     * @return new Warhead instance of the size
     */
    public Warhead buildWarhead(WarheadCasings size)
    {
        //TODO replace reflection
        try
        {
            return size.warhead_clazz.getConstructor(ItemStack.class).newInstance(new ItemStack(ICBM.blockWarhead, 1, size.ordinal()));
        }
        catch (InstantiationException e)
        {
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
        catch (NoSuchMethodException e)
        {
            e.printStackTrace();
        }
        catch (InvocationTargetException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Builds a missile with the provided parts
     *
     * @param missileSize - casing
     * @param explosive   - item to use as the explosive
     * @param engine      - movement system
     * @param guidance    - guidance system
     * @return new Missile
     */
    public Missile buildMissile(MissileCasings missileSize, ItemStack explosive, RocketEngine engine, Guidance guidance)
    {
        //TODO replace reflection
        try
        {
            Missile missile = missileSize.missile_clazz.getConstructor(ItemStack.class).newInstance(missileSize.newModuleStack());

            //Engine
            missile.setEngine(engine);

            //Guidance
            missile.setGuidance(guidance);

            //Warhead
            if (explosive != null)
            {
                missile.setWarhead(buildWarhead(missileSize.warhead_casing, explosive));
            }
            return missile;
        }
        catch (InstantiationException e)
        {
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
        catch (NoSuchMethodException e)
        {
            e.printStackTrace();
        }
        catch (InvocationTargetException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
