package com.builtbroken.icbm.content.crafting.missile.warhead;

import com.builtbroken.icbm.content.crafting.AbstractModule;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import resonant.api.TriggerCause;
import resonant.api.explosive.IExplosive;
import resonant.lib.world.edit.WorldChangeHelper;
import resonant.lib.world.explosive.ExplosiveItemUtility;
import resonant.lib.world.explosive.ExplosiveRegistry;

/** Container for explosive data to make implementing warhead like objects easier
 * Created by robert on 12/25/2014.
 */
public abstract class Warhead extends AbstractModule
{
    public IExplosive ex;
    public int size = 1;
    public NBTTagCompound tag = new NBTTagCompound();
    public ItemStack explosive;

    public Warhead(ItemStack warhead)
    {
        super(warhead, "warhead");
    }

    @Override
    public void load(NBTTagCompound nbt)
    {
        ex = ExplosiveItemUtility.getExplosive(nbt);
        size = ExplosiveItemUtility.getSize(nbt);
        tag = nbt.getCompoundTag("data");
    }

    @Override
    public void save(NBTTagCompound nbt)
    {
        ExplosiveItemUtility.setExplosive(nbt, ex);
        ExplosiveItemUtility.setSize(nbt, size);
        nbt.setTag("data", tag);
    }

    /** Triggers the warhead to set its explosive off */
    public WorldChangeHelper.ChangeResult trigger(TriggerCause triggerCause, World world, double x, double y, double z)
    {
       return ExplosiveRegistry.triggerExplosive(world, x, y, z, ex, triggerCause, size, tag);
    }
}
