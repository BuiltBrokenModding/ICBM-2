package com.builtbroken.icbm.content.crafting.station.warhead;

import com.builtbroken.mc.lib.world.explosive.ExplosiveRegistry;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * A slot that will only accept explosive items
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/13/2016.
 */
public class SlotExplosive extends Slot
{
    public SlotExplosive(IInventory inventory, int id, int x, int y)
    {
        super(inventory, id, x, y);
    }

    @Override
    public boolean isItemValid(ItemStack compareStack)
    {
        return ExplosiveRegistry.get(compareStack) != null;
    }
}
