package com.builtbroken.icbm.content.missile.parts;

import com.builtbroken.icbm.api.ICBM_API;
import com.builtbroken.icbm.api.modules.IWarhead;
import com.builtbroken.icbm.api.warhead.ITrigger;
import com.builtbroken.icbm.content.missile.parts.engine.RocketEngine;
import com.builtbroken.icbm.content.missile.parts.guidance.Guidance;
import com.builtbroken.icbm.content.missile.parts.trigger.Trigger;
import com.builtbroken.icbm.content.missile.parts.trigger.Triggers;
import com.builtbroken.icbm.content.missile.parts.warhead.*;
import com.builtbroken.mc.api.explosive.IExplosiveHandler;
import com.builtbroken.mc.api.modules.IModule;
import com.builtbroken.mc.api.modules.IModuleItem;
import com.builtbroken.mc.framework.explosive.ExplosiveRegistry;
import com.builtbroken.mc.lib.data.item.ItemStackWrapper;
import com.builtbroken.mc.prefab.module.ModuleBuilder;
import net.minecraft.item.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by robert on 12/28/2014.
 */
@Deprecated //Need to find a replacement, as this is not going to work for the long term
//What I want to replace this with is a factory system using lambda expressions HashMap<ID, FactoryMethod>
//Then maybe move each area to its own registry, apply a registry event like used in 1.12
//This will allow the registries to be separated from mods more easily
//Follow this up with an API.class that has references and quick access methods to each registry
// API { Registry registry;  public IObject getObject(String key) }
public class MissileModuleBuilder extends ModuleBuilder<IModule>
{
    public static MissileModuleBuilder INSTANCE = new MissileModuleBuilder();

    public HashMap<String, Class<? extends Warhead>> registeredWarheads = new HashMap();
    public HashMap<String, Class<? extends RocketEngine>> registeredEngines = new HashMap();
    public HashMap<String, Class<? extends Guidance>> registeredGuidances = new HashMap();
    public List<String> idToUseWithModuleItem = new ArrayList<String>();


    public boolean register(String mod_id, String name, Class<? extends IModule> clazz, boolean useItem)
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
    public boolean register(String mod_id, String name, Class<? extends IModule> clazz)
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

    public IWarhead buildWarhead(ItemStack stack)
    {
        IModule module = build(stack);
        if (module instanceof IWarhead)
        {
            return (IWarhead) module;
        }
        return null;
    }

    public RocketEngine buildEngine(ItemStack stack)
    {
        IModule module = build(stack);
        if (module instanceof RocketEngine)
        {
            return (RocketEngine) module;
        }
        return null;
    }

    public Guidance buildGuidance(ItemStack stack)
    {
        IModule module = build(stack);
        if (module instanceof Guidance)
        {
            return (Guidance) module;
        }
        return null;
    }

    public Trigger buildTrigger(ItemStack stack)
    {
        IModule module = build(stack);
        if (module instanceof Trigger)
        {
            return (Trigger) module;
        }
        return null;
    }

    @Override
    public IModule build(ItemStack stack)
    {
        if (stack != null && stack.getItem() instanceof IModuleItem)
        {
            //IModule module = ((IModuleItem) stack.getItem()).getModule(stack);
            //if (module != null)
            //{
            //    return module;
           // }
        }
        return super.build(stack);
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
        Warhead warhead = buildWarhead(size);

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
        //TODO create factory
        switch (size)
        {
            case EXPLOSIVE_MICRO:
                return new WarheadMicro(new ItemStack(ICBM_API.blockWarhead, 1, size.ordinal()));
            case EXPLOSIVE_SMALL:
                return new WarheadSmall(new ItemStack(ICBM_API.blockWarhead, 1, size.ordinal()));
            case EXPLOSIVE_STANDARD:
                return new WarheadStandard(new ItemStack(ICBM_API.blockWarhead, 1, size.ordinal()));
            case EXPLOSIVE_MEDIUM:
                return new WarheadMedium(new ItemStack(ICBM_API.blockWarhead, 1, size.ordinal()));
            case EXPLOSIVE_LARGE:
                return new WarheadLarge(new ItemStack(ICBM_API.blockWarhead, 1, size.ordinal()));
        }
        return null;
    }

    public ITrigger buildTrigger(Triggers trigger)
    {
        //TODO replace reflection
        try
        {
            return trigger.clazz.getConstructor(ItemStack.class).newInstance(new ItemStack(ICBM_API.itemTrigger, 1, trigger.ordinal()));
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
