package com.builtbroken.icbm.content.crafting.missile.engine.solid;

import com.builtbroken.icbm.content.crafting.missile.engine.Engine;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import resonant.api.tile.IInventoryProvider;
import resonant.api.tile.node.IExternalInventory;
import resonant.lib.utility.inventory.ExternalInventory;

/**
 * Created by robert on 12/28/2014.
 */
public class EngineSolid extends Engine implements IInventoryProvider
{
    private ExternalInventory inventory;
    protected int inventory_size = 1;

    public EngineSolid(ItemStack item, String name)
    {
        super(item, name);
    }

    @Override
    public IExternalInventory getInventory()
    {
        if(inventory == null)
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
