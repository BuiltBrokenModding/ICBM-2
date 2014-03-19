package icbm.sentry.platform.gui;

import calclavia.lib.access.Nodes;
import calclavia.lib.prefab.terminal.ITerminal;
import icbm.sentry.interfaces.ITurretUpgrade;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/** Slot that only accept upgrades for sentries
 * 
 * @author DarkGuardsman */
public class SlotTurretUpgrade extends Slot
{

    public SlotTurretUpgrade(IInventory par1iInventory, int par2, int par3, int par4)
    {
        super(par1iInventory, par2, par3, par4);
    }

    @Override
    public boolean isItemValid(ItemStack compareStack)
    {
        return compareStack != null && compareStack.getItem() instanceof ITurretUpgrade;
    }

    @Override
    public int getSlotStackLimit()
    {
        return 1;
    }

    @Override
    public boolean canTakeStack(EntityPlayer entityPlayer)
    {
        if (this.inventory instanceof ITerminal)
        {
            return ((ITerminal) this.inventory).canUse(Nodes.INV_TAKE, entityPlayer);
        }
        return false;
    }
}
