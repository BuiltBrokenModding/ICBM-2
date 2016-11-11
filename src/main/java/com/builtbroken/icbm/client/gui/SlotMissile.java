package com.builtbroken.icbm.client.gui;

import com.builtbroken.icbm.api.missile.IMissileItem;
import com.builtbroken.icbm.api.modules.IMissile;
import com.builtbroken.mc.api.modules.IModule;
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
public class SlotMissile extends Slot
{
    private final int size;

    public SlotMissile(IInventory inventory, int id, int x, int y, int size)
    {
        super(inventory, id, x, y);
        this.size = size;
    }

    @Override
    public boolean isItemValid(ItemStack compareStack)
    {
        if (compareStack != null)
        {
            if (compareStack.getItem() instanceof IMissileItem)
            {
                IMissile missile = ((IMissileItem) compareStack.getItem()).toMissile(compareStack);
                return missile != null && missile.getMissileSize() == size;
            }
            else if (compareStack.getItem() instanceof IModuleItem)
            {
                IModule module = ((IModuleItem) compareStack.getItem()).getModule(compareStack);
                return module instanceof IMissile && ((IMissile) module).getMissileSize() == size;
            }
        }
        return false;
    }
}
