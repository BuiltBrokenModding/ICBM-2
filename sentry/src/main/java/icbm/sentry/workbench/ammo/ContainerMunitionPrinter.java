package icbm.sentry.workbench.ammo;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerMunitionPrinter extends Container
{

    private TileMunitionPrinter tileEntity;

    public ContainerMunitionPrinter(InventoryPlayer inventoryPlayer, TileMunitionPrinter tile)
    {
        this.tileEntity = tile;

        this.addSlotToContainer(new Slot(tileEntity, 0, 134, 22)
        {
            @Override
            public boolean isItemValid(ItemStack itemstack)
            {
                return false;
            }
        });
        this.addSlotToContainer(new Slot(tileEntity, 1, 134, 48)
        {
            @Override
            public boolean isItemValid(ItemStack itemstack)
            {
                return false;
            }
        });

        for (int i = 0; i < 3; ++i)
        {
            for (int var4 = 0; var4 < 9; ++var4)
            {
                this.addSlotToContainer(new Slot(inventoryPlayer, var4 + i * 9 + 9, 8 + var4 * 18, 84 + i * 18));
            }
        }

        for (int i = 0; i < 9; ++i)
        {
            this.addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 142));
        }

    }

    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        return true;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par1)
    {
        return null;
    }
}
