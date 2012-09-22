package icbm.rongqi;

import icbm.jiqi.TXiaoFaSheQi;
import icbm.slots.SDaoDan;
import net.minecraft.src.Container;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.InventoryPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Slot;

public class CXiaoFaSheQi extends Container
{
    private TXiaoFaSheQi tileEntity;

    public CXiaoFaSheQi(InventoryPlayer par1InventoryPlayer, TXiaoFaSheQi tileEntity)
    {
        this.tileEntity = tileEntity;
        this.addSlotToContainer(new SDaoDan(tileEntity, 0, 84, 47));
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
        
        tileEntity.openChest();
    }
    
    public void onCraftGuiClosed(EntityPlayer entityplayer)
    {
		super.onCraftGuiClosed(entityplayer);
		tileEntity.closeChest();
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

            if(par1 != 0)
            {
                if(SDaoDan.itemValid(var4))
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
