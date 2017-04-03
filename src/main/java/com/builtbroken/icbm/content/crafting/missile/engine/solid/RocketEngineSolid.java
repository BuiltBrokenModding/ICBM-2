package com.builtbroken.icbm.content.crafting.missile.engine.solid;

import com.builtbroken.icbm.api.missile.IMissileEntity;
import com.builtbroken.icbm.api.modules.IMissile;
import com.builtbroken.icbm.content.crafting.missile.engine.RocketEngine;
import com.builtbroken.mc.api.tile.provider.IInventoryProvider;
import com.builtbroken.mc.prefab.inventory.ExternalInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Engine that uses items as fuel
 * Created by robert on 12/28/2014.
 */
public class RocketEngineSolid extends RocketEngine implements IInventoryProvider
{
    private ExternalInventory inventory;
    protected int inventory_size = 1;

    public RocketEngineSolid(ItemStack item, String name)
    {
        super(item, name);
    }

    @Override
    public ExternalInventory getInventory()
    {
        if (inventory == null)
        {
            inventory = new ExternalInventory(this, inventory_size);
        }
        return inventory;
    }

    @Override
    public boolean canStore(ItemStack stack, int slot, ForgeDirection side)
    {
        return false;
    }

    @Override
    public boolean canRemove(ItemStack stack, int slot, ForgeDirection side)
    {
        return true;
    }

    /**
     * Fuel in the rocket
     *
     * @return stack being used as fuel, or null if no fuel
     */
    public ItemStack fuelStack()
    {
        return inventory != null ? getInventory().getStackInSlot(0) : null;
    }

    @Override
    public boolean generatesFire(IMissileEntity missile, IMissile missileModule)
    {
        return fuelStack() != null && fuelStack().stackSize > 0;
    }

    @Override
    public void load(NBTTagCompound nbt)
    {
        if (nbt.hasKey("inventory"))
        {
            getInventory().load(nbt.getCompoundTag("inventory"));
        }
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbt)
    {
        if (getInventory().getStackInSlot(0) != null)
        {
            nbt.setTag("inventory", getInventory().save(new NBTTagCompound()));
        }
        return nbt;
    }

}
