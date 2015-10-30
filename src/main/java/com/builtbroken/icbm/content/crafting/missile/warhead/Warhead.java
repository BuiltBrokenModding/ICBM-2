package com.builtbroken.icbm.content.crafting.missile.warhead;

import com.builtbroken.icbm.api.modules.IWarhead;
import com.builtbroken.icbm.content.crafting.AbstractModule;
import com.builtbroken.mc.api.event.TriggerCause;
import com.builtbroken.mc.api.explosive.IExplosiveHandler;
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
public abstract class Warhead extends AbstractModule implements IWarhead
{
    public IExplosiveHandler ex;
    public double size = 1;
    public NBTTagCompound tag = new NBTTagCompound();
    public ItemStack explosive;

    protected WarheadCasings casing;

    public Warhead(ItemStack warhead, WarheadCasings casing)
    {
        super(warhead, "warhead");
        this.casing = casing;
    }

    @Override
    public void load(NBTTagCompound nbt)
    {
        if (nbt.hasKey(ExplosiveItemUtility.EXPLOSIVE_SAVE))
            ex = ExplosiveItemUtility.getExplosive(nbt);
        if (nbt.hasKey("data"))
            tag = nbt.getCompoundTag("data");
        size = ExplosiveItemUtility.getSize(nbt);
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbt)
    {
        if (ex != null)
            ExplosiveItemUtility.setExplosive(nbt, ex);
        if (tag != null)
            nbt.setTag("data", tag);
        ExplosiveItemUtility.setSize(nbt, size);
        return nbt;
    }

    @Override
    public WorldChangeHelper.ChangeResult trigger(TriggerCause triggerCause, World world, double x, double y, double z)
    {
        if(!world.isRemote)
        {
            return ExplosiveRegistry.triggerExplosive(world, x, y, z, ex, triggerCause, size + (size * casing.ordinal()) + 5, tag);
        }
        return WorldChangeHelper.ChangeResult.COMPLETED;
    }

    @Override
    public boolean setExplosive(IExplosiveHandler ex, double size, NBTTagCompound nbt)
    {
        this.ex = ex;
        this.size = size;
        this.tag = nbt;
        return true;
    }

    @Override
    public IExplosiveHandler getExplosive()
    {
        return null;
    }
}
