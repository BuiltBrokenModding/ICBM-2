package com.builtbroken.icbm.client.gui;

import com.builtbroken.icbm.api.modules.IRocketEngine;
import com.builtbroken.mc.api.modules.IModuleItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * A slot that will only accept warhead items
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/13/2016.
 */
public class SlotEngine extends Slot
{
    public SlotEngine(IInventory inventory, int id, int x, int y)
    {
        super(inventory, id, x, y);
    }

    @Override
    public boolean isItemValid(ItemStack compareStack)
    {
        if (compareStack != null && compareStack.getItem() instanceof IModuleItem)
        {
            return ((IModuleItem) compareStack.getItem()).getModule(compareStack) instanceof IRocketEngine;
        }
        return false;
    }
}
