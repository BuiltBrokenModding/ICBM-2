package com.builtbroken.icbm.content.crafting.missile.engine.solid;

import com.builtbroken.icbm.content.crafting.missile.engine.RocketEngine;
import com.builtbroken.mc.api.tile.IInventoryProvider;
import com.builtbroken.mc.api.tile.node.IExternalInventory;
import com.builtbroken.mc.prefab.inventory.ExternalInventory;
import net.minecraft.item.ItemStack;
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
    public IExternalInventory getInventory()
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
}
