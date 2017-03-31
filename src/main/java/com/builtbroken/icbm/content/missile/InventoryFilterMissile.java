package com.builtbroken.icbm.content.missile;

import com.builtbroken.icbm.api.missile.IMissileItem;
import com.builtbroken.icbm.api.modules.IMissile;
import com.builtbroken.mc.api.modules.IModule;
import com.builtbroken.mc.api.modules.IModuleItem;
import com.builtbroken.mc.api.IInventoryFilter;
import net.minecraft.item.ItemStack;

/**
 * Used in inventories to filter missiles
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/15/2016.
 */
public class InventoryFilterMissile implements IInventoryFilter
{
    private final int size;

    public InventoryFilterMissile(int size)
    {
        this.size = size;
    }

    @Override
    public boolean isStackInFilter(ItemStack compareStack)
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
        return false;
    }
}
