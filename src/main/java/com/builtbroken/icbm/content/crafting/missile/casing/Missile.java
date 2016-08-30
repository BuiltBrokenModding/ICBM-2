package com.builtbroken.icbm.content.crafting.missile.casing;

import com.builtbroken.icbm.api.modules.IGuidance;
import com.builtbroken.icbm.api.modules.IMissile;
import com.builtbroken.icbm.api.modules.IRocketEngine;
import com.builtbroken.icbm.api.modules.IWarhead;
import com.builtbroken.icbm.content.crafting.AbstractModule;
import com.builtbroken.icbm.content.crafting.missile.engine.RocketEngine;
import com.builtbroken.icbm.content.crafting.missile.guidance.Guidance;
import com.builtbroken.icbm.content.crafting.missile.warhead.Warhead;
import com.builtbroken.mc.api.modules.IModule;
import com.builtbroken.mc.api.modules.IModuleItem;
import com.builtbroken.mc.lib.helper.LanguageUtility;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Crafting object for the missile
 * Contains all the peaces that make up the
 * missile and allow it to function
 *
 * @author Darkguardsman
 */
public abstract class Missile extends AbstractModule implements IMissile
{
    /** Size of the missile */
    public final MissileCasings casing;

    private IWarhead warhead;
    private IGuidance guidance;
    private IRocketEngine engine;

    public Missile(ItemStack stack, MissileCasings casing)
    {
        super(stack, "missile");
        this.casing = casing;
        load(stack);
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
    public boolean canInstallModule(ItemStack stack, IModule module)
    {
        return module instanceof RocketEngine || module instanceof Warhead || module instanceof Guidance;
    }

    @Override
    public boolean installModule(ItemStack stack, IModule module)
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
        return casing.ordinal();
    }

    @Override
    public String toString()
    {
        //TODO maybe cache being in missile enum to save a little cpu time?
        return LanguageUtility.capitalizeFirst(casing.name().toLowerCase()) + "Missile[" + getWarhead() + ", " + getGuidance() + ", " + getEngine() + "]";
    }

    /**
     * Max number of hit points the missile has
     *
     * @return
     */
    public float getMaxHitPoints()
    {
        return casing.getMaxHitPoints();
    }

    /**
     * How tall is the missile, mainly used
     * for collsion box code and rendering
     *
     * @return height in meters
     */
    public abstract double getHeight();

    /**
     * How wide is the missile, mainly used
     * for collsion box code and rendering
     *
     * @return height in meters
     */
    public abstract double getWidth();
}
