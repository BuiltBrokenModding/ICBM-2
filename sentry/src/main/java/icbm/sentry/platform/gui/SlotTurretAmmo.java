package icbm.sentry.platform.gui;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import universalelectricity.api.CompatibilityModule;
import calclavia.api.icbm.sentry.IAmmunition;

/** Slot that only accept upgrades for sentries
 * 
 * @author DarkGuardsman */
public class SlotTurretAmmo extends Slot
{

    public SlotTurretAmmo(IInventory par1iInventory, int par2, int par3, int par4)
    {
        super(par1iInventory, par2, par3, par4);
    }

    @Override
    public boolean isItemValid(ItemStack compareStack)
    {
        return compareStack != null && (compareStack.getItem() instanceof IAmmunition || CompatibilityModule.isHandler(compareStack.getItem()));
    }
}
