package com.builtbroken.icbm.content.missile.parts.warhead;

import com.builtbroken.icbm.api.modules.IMissileModule;
import com.builtbroken.icbm.api.modules.IWarhead;
import com.builtbroken.icbm.api.warhead.ITrigger;
import com.builtbroken.icbm.api.warhead.ITriggerAccepter;
import com.builtbroken.icbm.content.missile.parts.MissileModule;
import com.builtbroken.icbm.content.missile.entity.EntityMissile;
import com.builtbroken.mc.api.event.TriggerCause;
import com.builtbroken.mc.api.explosive.IExplosiveHandler;
import com.builtbroken.mc.api.items.explosives.IExplosiveHolderItem;
import com.builtbroken.mc.api.items.explosives.IExplosiveItem;
import com.builtbroken.mc.api.modules.IModule;
import com.builtbroken.mc.api.modules.IModuleComponent;
import com.builtbroken.mc.api.modules.IModuleItem;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.lib.helper.LanguageUtility;
import com.builtbroken.mc.lib.world.edit.WorldChangeHelper;
import com.builtbroken.mc.framework.explosive.ExplosiveRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * Container for explosive data to make implementing warhead like objects easier
 * Created by robert on 12/25/2014.
 */
public abstract class Warhead extends MissileModule implements IWarhead, ITriggerAccepter, Cloneable
{
    /** Explosive item used to ID the explosive handler. */
    public ItemStack explosive;
    /** Logic object used to check if the warhead needs to trigger. */
    public ITrigger trigger;

    /** Size of the warhead case. */
    public final WarheadCasings casing;

    /**
     * Creates a new warhead instance.
     *
     * @param warhead - ItemStack that represents the warhead and it's data.
     * @param casing  - size of the warhead.
     */
    public Warhead(ItemStack warhead, WarheadCasings casing)
    {
        super(warhead, "warhead");
        this.casing = casing;
    }

    @Override
    public double getMass()
    {
        return casing.mass;
    }

    @Override
    public void load(NBTTagCompound nbt)
    {
        explosive = loadExplosiveItemFromNBT(nbt);
    }

    /**
     * Loads the explosive item from the item save for the warhead.
     *
     * @param nbt - warhead save data
     * @return new ItemStack as long as the nbt has the tag exItem
     */
    public static ItemStack loadExplosiveItemFromNBT(NBTTagCompound nbt)
    {
        if (nbt != null && nbt.hasKey("exItem"))
        {
            ItemStack explosive = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("exItem"));
            if (explosive == null)
            {
                Engine.error("Failed to load explosive item in warhead");
            }
            return explosive;
        }
        return null;
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbt)
    {
        if (explosive != null)
        {
            nbt.setTag("exItem", explosive.writeToNBT(new NBTTagCompound()));
        }
        return nbt;
    }

    @Override
    public void update(EntityMissile missile)
    {
        if(getTrigger() instanceof IMissileModule)
        {
            ((IMissileModule) getTrigger()).update(missile);
        }
    }

    @Override
    public WorldChangeHelper.ChangeResult trigger(TriggerCause triggerCause, World world, double x, double y, double z)
    {
        //Assume no trigger is an all trigger LEGACY SUPPORT DO NOT REMOVE (Trigger == null)
        if(trigger == null || trigger.shouldTrigger(triggerCause, world, x, y, z))
        {
            if (getExplosive() != null)
            {
                //Rare this will happen but check is added just in case, Notes: happens client side if triggered incorrectly
                if (world == null || Double.isNaN(x) || Double.isNaN(y) || Double.isNaN(z))
                {
                    if (Engine.runningAsDev)
                    {
                        Engine.error("Warhead trigger with an invalid location " + world + " " + x + "x " + y + "y " + z + "z ");
                    }
                    return WorldChangeHelper.ChangeResult.FAILED;
                }
                return ExplosiveRegistry.triggerExplosive(world, x, y, z, getExplosive(), triggerCause, getExplosiveSize() * triggerCause.effectScaleChange, getAdditionalExplosiveData());
            }
        }
        return WorldChangeHelper.ChangeResult.FAILED; //Maybe switch to completed or better error result
    }

    @Override
    public boolean setExplosiveStack(ItemStack stack)
    {
        this.explosive = stack != null ? stack.copy() : stack;
        return true;
    }

    @Deprecated
    public boolean setExplosive(IExplosiveHandler ex, double size, NBTTagCompound nbt)
    {
        if (explosive != null && explosive.getItem() instanceof IExplosiveHolderItem)
        {
            return ((IExplosiveHolderItem) explosive.getItem()).setExplosive(explosive, ex, size, nbt);
        }
        return false;
    }

    @Override
    public ItemStack getExplosiveStack()
    {
        return explosive;
    }

    @Override
    public NBTTagCompound getAdditionalExplosiveData()
    {
        //TODO if presents an issue merge warhead nbt with item nbt in case mods code to the warhead nbt
        if (explosive != null && explosive.getItem() instanceof IExplosiveItem)
        {
            return ((IExplosiveItem) explosive.getItem()).getAdditionalExplosiveData(explosive);
        }
        return null;
    }

    @Override
    public double getExplosiveSize()
    {
        return ExplosiveRegistry.getExplosiveSize(explosive);
    }

    @Override
    public IExplosiveHandler getExplosive()
    {
        return ExplosiveRegistry.get(explosive);
    }

    @Override
    public int getMissileSize()
    {
        return -1;
    }

    @Override
    public ITrigger getTrigger()
    {
        return trigger;
    }

    @Override
    public boolean setTrigger(ITrigger trigger)
    {
        if (this.trigger instanceof IModuleComponent)
        {
            ((IModuleComponent)this.trigger).removedFromDevice(this);
        }
        this.trigger = trigger;
        if (this.trigger instanceof IModuleComponent)
        {
            ((IModuleComponent)this.trigger).addedToDevice(this);
        }
        return true;
    }

    @Override
    public ItemStack setTrigger(ItemStack stack)
    {
        ItemStack temp = trigger != null ? trigger.toStack() : null;
        if (stack == null)
        {
            if (setTrigger((ITrigger) null))
            {
                return temp;
            }
        }
        else if (stack.getItem() instanceof IModuleItem)
        {
            IModule module = ((IModuleItem) stack.getItem()).getModule(stack);
            if (module instanceof ITrigger)
            {
                if (setTrigger((ITrigger) module))
                {
                    return temp;
                }
            }
        }
        return null;
    }

    @Override
    public String toString()
    {
        if (explosive != null)
        {
            return LanguageUtility.capitalizeFirst(casing.name().toLowerCase()) + "Warhead[" + explosive.getDisplayName() + " x " + explosive.stackSize + "]";
        }
        return LanguageUtility.capitalizeFirst(casing.name().toLowerCase()) + "Warhead[" + explosive + "]";
    }

    @Override
    public abstract Warhead clone();

    public void copyDataInto(Warhead warhead)
    {
        if (explosive != null)
        {
            warhead.explosive = explosive.copy();
        }
        else
        {
            warhead.explosive = null;
        }
    }
}
