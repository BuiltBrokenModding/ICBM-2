package com.builtbroken.icbm.content.missile.parts;

import com.builtbroken.icbm.api.modules.IGuidance;
import com.builtbroken.icbm.api.modules.IMissile;
import com.builtbroken.icbm.api.modules.IRocketEngine;
import com.builtbroken.icbm.api.modules.IWarhead;
import com.builtbroken.icbm.content.missile.data.casing.MissileCasingData;
import com.builtbroken.icbm.content.missile.parts.casing.MissileSize;
import com.builtbroken.icbm.content.missile.parts.engine.RocketEngine;
import com.builtbroken.icbm.content.missile.parts.guidance.Guidance;
import com.builtbroken.icbm.content.missile.parts.warhead.Warhead;
import com.builtbroken.mc.api.IHasMass;
import com.builtbroken.mc.api.explosive.IExplosiveHandler;
import com.builtbroken.mc.api.modules.IModule;
import com.builtbroken.mc.api.modules.IModuleItem;
import com.builtbroken.mc.lib.world.explosive.ExplosiveRegistry;
import com.builtbroken.mc.prefab.items.ItemStackWrapper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;
import java.util.List;

/**
 * Crafting object for the missile
 * Contains all the peaces that make up the
 * missile and allow it to function
 *
 * @author Darkguardsman
 */
public class Missile implements IMissile, IHasMass
{
    /** Size of the missile */
    public final MissileCasingData data;
    public ItemStack item;

    public String textureSkinID = "icbm:default";

    private IWarhead warhead;
    private IGuidance guidance;
    private IRocketEngine engine;

    public Missile(MissileCasingData data)
    {
        this.data = data;
    }

    @Override
    public final String getSaveID()
    {
        return MissileModuleBuilder.INSTANCE.getID(this);
    }

    @Override
    public void save(ItemStack stack)
    {
        save(stack.getTagCompound());
    }

    @Override
    public double getMass()
    {
        return data.getMass();
    }

    @Override
    public List<IModule> getSubModules()
    {
        List<IModule> module = new ArrayList();
        if (warhead != null)
        {
            module.add(warhead);
        }
        if (guidance != null)
        {
            module.add(guidance);
        }
        if (engine != null)
        {
            module.add(engine);
        }
        return module;
    }

    @Override
    public void load(NBTTagCompound nbt)
    {
        if (nbt.hasKey("warhead"))
        {
            IModule module = getModule(nbt, "warhead");
            setWarhead(module instanceof IWarhead ? (IWarhead) module : null);
        }
        if (nbt.hasKey("engine"))
        {
            IModule module = getModule(nbt, "engine");
            setEngine(module instanceof IRocketEngine ? (IRocketEngine) module : null);
        }
        if (nbt.hasKey("guidance"))
        {
            IModule module = getModule(nbt, "guidance");
            setGuidance(module instanceof IGuidance ? (IGuidance) module : null);
        }
    }

    private IModule getModule(NBTTagCompound nbt, String id)
    {
        ItemStack stack = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag(id));
        if (stack != null && stack.getItem() instanceof IModuleItem)
        {
            return ((IModuleItem) stack.getItem()).getModule(stack);
        }
        return null;
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbt)
    {
        if (getWarhead() != null)
        {
            nbt.setTag("warhead", getWarhead().toStack().writeToNBT(new NBTTagCompound()));
        }
        if (getEngine() != null)
        {
            nbt.setTag("engine", getEngine().toStack().writeToNBT(new NBTTagCompound()));
        }
        if (getGuidance() != null)
        {
            nbt.setTag("guidance", getGuidance().toStack().writeToNBT(new NBTTagCompound()));
        }

        return nbt;
    }

    @Override
    public boolean canInstallModule(IModule module)
    {
        return module instanceof RocketEngine || module instanceof Warhead || module instanceof Guidance;
    }

    @Override
    public boolean installModule(IModule module)
    {
        if (module instanceof RocketEngine && engine == null)
        {
            setEngine((RocketEngine) module);
            return getEngine() == module;
        }
        else if (module instanceof Warhead && warhead == null)
        {
            setWarhead((Warhead) module);
            return getWarhead() == module;
        }
        else if (module instanceof Guidance && guidance == null)
        {
            setGuidance((Guidance) module);
            return getGuidance() == module;
        }
        return false;
    }

    @Override
    public boolean canLaunch()
    {
        return getEngine() != null && getEngine().getMaxDistance(this) > 0 && getEngine().getSpeed(this) > 0;
    }

    public void setWarhead(IWarhead warhead)
    {
        this.warhead = warhead;
    }

    public void setGuidance(IGuidance guidance)
    {
        this.guidance = guidance;
    }

    public void setEngine(IRocketEngine engine)
    {
        this.engine = engine;
    }

    public IWarhead getWarhead()
    {
        return warhead;
    }

    public IGuidance getGuidance()
    {
        return guidance;
    }

    public IRocketEngine getEngine()
    {
        return engine;
    }

    @Override
    public int getMissileSize()
    {
        return data.getMissileBodySize();
    }

    @Override
    public String toString()
    {
        //TODO maybe cache being in missile enum to save a little cpu time?
        return data.getContentID() + " Missile[" + getWarhead() + ", " + getGuidance() + ", " + getEngine() + "]";
    }

    @Override
    public ItemStack toStack()
    {
        save(item);
        return item.copy();
    }

    @Override
    public String getUnlocalizedName()
    {
        return data.getContentID();
    }

    /**
     * Max number of hit points the missile has
     *
     * @return
     */
    public float getMaxHitPoints()
    {
        return 10; //TODO implement
    }

    /**
     * How tall is the missile, mainly used
     * for collsion box code and rendering
     *
     * @return height in meters
     */
    public double getHeight()
    {
        return 3; //TODO implement
    }


    /**
     * How wide is the missile, mainly used
     * for collsion box code and rendering
     *
     * @return height in meters
     */
    public double getWidth()
    {
        return 0.5; //TODO implement
    }


    public static Missile createMissile(MissileSize size, IExplosiveHandler handler)
    {
        Missile missile = new Missile(size.defaultMissileCasing);
        missile.setWarhead(MissileModuleBuilder.INSTANCE.buildWarhead(size.warhead_casing));
        List<ItemStackWrapper> items = ExplosiveRegistry.getItems(handler);
        if(items.size() > 0)
        {
            missile.getWarhead().setExplosiveStack(items.get(0).itemStack);
        }
        return missile;
    }
}
