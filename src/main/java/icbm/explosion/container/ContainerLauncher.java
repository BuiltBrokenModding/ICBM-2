package icbm.explosion.container;

import icbm.core.prefab.ContainerBase;
import icbm.explosion.ICBMExplosion;
import icbm.explosion.machines.TileLauncherBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import calclavia.lib.prefab.slot.SlotSpecific;

public class ContainerLauncher extends ContainerBase
{
    private TileLauncherBase missileLauncher;

    public ContainerLauncher(InventoryPlayer par1InventoryPlayer, TileLauncherBase tileEntity)
    {
        super(tileEntity);
        this.missileLauncher = tileEntity;
        this.addSlotToContainer(new SlotSpecific(tileEntity, 0, 84, 47, new ItemStack(ICBMExplosion.itemMissile)));
        int var3;

        for (var3 = 0; var3 < 3; ++var3)
        {
            for (int var4 = 0; var4 < 9; ++var4)
            {
                this.addSlotToContainer(new Slot(par1InventoryPlayer, var4 + var3 * 9 + 9, 8 + var4 * 18, 84 + var3 * 18));
            }
        }

        for (var3 = 0; var3 < 9; ++var3)
        {
            this.addSlotToContainer(new Slot(par1InventoryPlayer, var3, 8 + var3 * 18, 142));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer par1EntityPlayer)
    {
        return this.missileLauncher.isUseableByPlayer(par1EntityPlayer);
    }

    /** Called to transfer a stack from one inventory to the other eg. when shift clicking. */
    @Override
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par1)
    {
        ItemStack var2 = null;
        Slot var3 = (Slot) this.inventorySlots.get(par1);

        if (var3 != null && var3.getHasStack())
        {
            ItemStack var4 = var3.getStack();
            var2 = var4.copy();

            if (par1 != 0)
            {
                if (this.getSlot(0).isItemValid(var4))
                {
                    if (!this.mergeItemStack(var4, 0, 1, false))
                    {
                        return null;
                    }
                }
            }
            else if (!this.mergeItemStack(var4, 3, 37, false))
            {
                return null;
            }

            if (var4.stackSize == 0)
            {
                var3.putStack((ItemStack) null);
            }
            else
            {
                var3.onSlotChanged();
            }

            if (var4.stackSize == var2.stackSize)
            {
                return null;
            }

            var3.onPickupFromSlot(par1EntityPlayer, var4);
        }

        return var2;
    }
}
