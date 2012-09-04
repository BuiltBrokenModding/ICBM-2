package icbm;

import net.minecraft.src.Container;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.InventoryPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Slot;

public class ContainerRailgun extends Container
{
    private TileEntityRailgun tileEntity;

    public ContainerRailgun(InventoryPlayer par1InventoryPlayer, TileEntityRailgun tileEntity)
    {
        this.tileEntity = tileEntity;
        
        this.addSlotToContainer(new SlotBullet(tileEntity, 0, 53, 35));
        this.addSlotToContainer(new SlotBullet(tileEntity, 1, 71, 35));
        this.addSlotToContainer(new SlotBullet(tileEntity, 2, 89, 35));
        this.addSlotToContainer(new SlotBullet(tileEntity, 3, 107, 35));
        
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
        return this.tileEntity.isUseableByPlayer(par1EntityPlayer);
    }

    /**
     * Called to transfer a stack from one inventory to the other eg. when shift clicking.
     */
    @Override
    public ItemStack transferStackInSlot(int par1)
    {
        ItemStack var2 = null;
        Slot var3 = (Slot)this.inventorySlots.get(par1);

        if (var3 != null && var3.getHasStack())
        {
            ItemStack var4 = var3.getStack();
            var2 = var4.copy();

            if(par1 > 3)
            {
            	if(var4.getItem() instanceof ItemBullet)
                {
            		if(!this.mergeItemStack(var4, 0, 1, false))
                    {
                        return null;
                    }
            		/*
            		else if (!this.mergeItemStack(var4, 1, 2, false))
                    {
                        return null;
                    }
            		else if (!this.mergeItemStack(var4, 2, 3, false))
                    {
                        return null;
                    }
            		else if (!this.mergeItemStack(var4, 3, 4, false))
                    {
                        return null;
                    }*/
                }
                else if (par1 >= 3 && par1 < 30)
                {
                    if (!this.mergeItemStack(var4, 30, 40, false))
                    {
                        return null;
                    }
                }
                else if (par1 >= 30 && par1 < 40 && !this.mergeItemStack(var4, 4, 30, false))
                {
                    return null;
                }
            }
            else if (!this.mergeItemStack(var4, 3, 39, false))
            {
                return null;
            }

            if (var4.stackSize == 0)
            {
                var3.putStack((ItemStack)null);
            }
            else
            {
                var3.onSlotChanged();
            }

            if (var4.stackSize == var2.stackSize)
            {
                return null;
            }

            var3.onPickupFromSlot(var4);
        }

        return var2;
    }
}
