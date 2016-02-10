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
import com.builtbroken.mc.prefab.items.ItemStackWrapper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.util.List;

/**
 * Container for explosive data to make implementing warhead like objects easier
 * Created by robert on 12/25/2014.
 */
public abstract class Warhead extends AbstractModule implements IWarhead, Cloneable
{
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
    }

    @Override
    public void load(NBTTagCompound nbt)
    {
        if (nbt.hasKey("exItem"))
        {
            explosive = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("exItem"));
        }
        //Legacy loading
        else if (nbt.hasKey(ExplosiveItemUtility.EXPLOSIVE_SAVE))
        {
            IExplosiveHandler ex = ExplosiveItemUtility.getExplosive(nbt);
            List<ItemStackWrapper> stacks = ExplosiveRegistry.getItems(ex);

            if (stacks != null && stacks.size() > 0)
            {
                explosive = stacks.get(0).itemStack;
                if (explosive.getItem() instanceof IExplosiveHolderItem)
                {
                    double size = Math.max(ExplosiveItemUtility.getSize(nbt), getExplosiveSize());
                    NBTTagCompound additionalExData = null;
                    if (nbt.hasKey("data"))
                    {
                        additionalExData = nbt.getCompoundTag("data");
                    }
                    ((IExplosiveHolderItem) explosive.getItem()).setExplosive(explosive, getExplosive(), size, additionalExData);
                }
            }
        }

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
        if (explosive != null && explosive.getItem() instanceof IExplosiveHolderItem)
        {
            return ((IExplosiveHolderItem) explosive.getItem()).setExplosive(explosive, ex, size, nbt);
        }
        return false;
    }

    @Override
    public boolean setExplosive(ItemStack stack)
    {
        this.explosive = stack != null ? stack.copy() : stack;
        return true;
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
        if (explosive != null && explosive.getItem() instanceof IExplosiveHolderItem)
        {
            return ((IExplosiveHolderItem) explosive.getItem()).getAdditionalExplosiveData(explosive);
        }
        return null;
    }

    @Override
    public double getExplosiveSize()
    {
        if (explosive != null && explosive.getItem() instanceof IExplosiveHolderItem)
        {
            return ((IExplosiveHolderItem) explosive.getItem()).getExplosiveSize(explosive);
        }
        return -1;
    }

    @Override
    public IExplosiveHandler getExplosive()
    {
        if (explosive != null && explosive.getItem() instanceof IExplosiveItem)
        {
            return ((IExplosiveItem) explosive.getItem()).getExplosive(explosive);
        }
        return null;
    }

    @Override
    public int getMissileSize()
    {
        return -1;
    }

    @Override
    public String toString()
    {
        return LanguageUtility.capitalizeFirst(casing.name().toLowerCase()) + "Warhead[" + explosive + "]";
    }

    @Override
    public abstract Warhead clone();

    public void copyDataInto(Warhead warhead)
    {
        warhead.explosive = explosive;
    }
}
