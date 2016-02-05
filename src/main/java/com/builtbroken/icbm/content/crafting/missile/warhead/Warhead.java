package com.builtbroken.icbm.content.crafting.missile.warhead;

import com.builtbroken.icbm.api.modules.IWarhead;
import com.builtbroken.icbm.content.crafting.AbstractModule;
import com.builtbroken.mc.api.event.TriggerCause;
import com.builtbroken.mc.api.explosive.IExplosiveHandler;
import com.builtbroken.mc.api.items.IExplosiveHolderItem;
import com.builtbroken.mc.api.items.IExplosiveItem;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.lib.helper.LanguageUtility;
import com.builtbroken.mc.lib.world.edit.WorldChangeHelper;
import com.builtbroken.mc.lib.world.explosive.ExplosiveItemUtility;
import com.builtbroken.mc.lib.world.explosive.ExplosiveRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * Container for explosive data to make implementing warhead like objects easier
 * Created by robert on 12/25/2014.
 */
public abstract class Warhead extends AbstractModule implements IWarhead, Cloneable
{
    /** Handler used to trigger an explosion. */
    public IExplosiveHandler ex;
    /** Size of the explosion. */
    public double size = 1;
    /** Additional data for triggering an explosion. */
    public NBTTagCompound additionalExData = new NBTTagCompound();
    /** Explosive item used to ID the explosive handler. */
    public ItemStack explosive;

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
        this.size = 1 + casing.ordinal();
    }

    @Override
    public void load(NBTTagCompound nbt)
    {
        if (nbt.hasKey(ExplosiveItemUtility.EXPLOSIVE_SAVE))
        {
            ex = ExplosiveItemUtility.getExplosive(nbt);
        }
        if (nbt.hasKey("data"))
        {
            additionalExData = nbt.getCompoundTag("data");
        }
        if (nbt.hasKey("exItem"))
        {
            explosive = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("exItem"));
        }
        size = ExplosiveItemUtility.getSize(nbt);
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbt)
    {
        if (ex != null)
        {
            ExplosiveItemUtility.setExplosive(nbt, ex);
        }
        if (additionalExData != null)
        {
            nbt.setTag("data", additionalExData);
        }
        if (explosive != null)
        {
            nbt.setTag("exItem", explosive.writeToNBT(new NBTTagCompound()));
        }
        ExplosiveItemUtility.setSize(nbt, size);
        return nbt;
    }

    @Override
    public WorldChangeHelper.ChangeResult trigger(TriggerCause triggerCause, World world, double x, double y, double z)
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
            return ExplosiveRegistry.triggerExplosive(world, x, y, z, getExplosive(), triggerCause, getExplosiveSize(), getAdditionalExplosiveData());
        }
        return WorldChangeHelper.ChangeResult.FAILED; //Maybe switch to completed or better error result
    }

    @Override
    public boolean setExplosive(IExplosiveHandler ex, double size, NBTTagCompound nbt)
    {
        this.ex = ex;
        this.size = size;
        this.additionalExData = nbt;
        return true;
    }

    @Override
    public boolean setExplosive(ItemStack stack)
    {
        this.explosive = stack != null ? stack.copy() : stack;
        return true;
    }

    public Warhead setSize(double size)
    {
        this.size = size;
        return this;
    }

    @Override
    public NBTTagCompound getAdditionalExplosiveData()
    {
        //TODO if presents an issue merge warhead nbt with item nbt in case mods code to the warhead nbt
        if (explosive != null && explosive.getItem() instanceof IExplosiveHolderItem)
        {
            return ((IExplosiveHolderItem) explosive.getItem()).getAdditionalExplosiveData(explosive);
        }
        return additionalExData;
    }

    @Override
    public double getExplosiveSize()
    {
        if (explosive != null && explosive.getItem() instanceof IExplosiveHolderItem)
        {
            return ((IExplosiveHolderItem) explosive.getItem()).getExplosiveSize(explosive);
        }
        return size;
    }

    @Override
    public IExplosiveHandler getExplosive()
    {
        if (explosive != null && explosive.getItem() instanceof IExplosiveItem)
        {
            return ((IExplosiveItem) explosive.getItem()).getExplosive(explosive);
        }
        return ex;
    }

    @Override
    public int getMissileSize()
    {
        return -1;
    }

    @Override
    public String toString()
    {
        return LanguageUtility.capitalizeFirst(casing.name().toLowerCase()) + "Warhead[" + 1 + " x " + ex.getID() + "]";
    }

    @Override
    public abstract Warhead clone();

    public void copyDataInto(Warhead warhead)
    {
        warhead.ex = ex;
        warhead.additionalExData = additionalExData;
        warhead.size = size;
        warhead.explosive = explosive;
    }
}
