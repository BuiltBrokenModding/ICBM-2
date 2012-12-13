package icbm.common.rongqi;

import icbm.common.ItZiDan;
import icbm.common.jiqi.TCiGuiPao;
import icbm.common.slots.SZiDan;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class CCiGuiPao extends Container
{
	private TCiGuiPao tileEntity;

	public CCiGuiPao(InventoryPlayer par1InventoryPlayer, TCiGuiPao tileEntity)
	{
		this.tileEntity = tileEntity;

		this.addSlotToContainer(new SZiDan(tileEntity, 0, 107, 35));

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
	public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par1)
	{
		ItemStack var2 = null;
		Slot var3 = (Slot) this.inventorySlots.get(par1);

		if (var3 != null && var3.getHasStack())
		{
			ItemStack var4 = var3.getStack();
			var2 = var4.copy();

			if (par1 > 3)
			{
				if (var4.getItem() instanceof ItZiDan)
				{
					if (!this.mergeItemStack(var4, 0, 1, false))
					{
						return null;
					}
					else if (!this.mergeItemStack(var4, 3, 4, false)) { return null; }
				}
				else if (par1 >= 3 && par1 < 30)
				{
					if (!this.mergeItemStack(var4, 30, 40, false)) { return null; }
				}
				else if (par1 >= 30 && par1 < 40 && !this.mergeItemStack(var4, 4, 30, false)) { return null; }
			}
			else if (!this.mergeItemStack(var4, 3, 39, false)) { return null; }

			if (var4.stackSize == 0)
			{
				var3.putStack((ItemStack) null);
			}
			else
			{
				var3.onSlotChanged();
			}

			if (var4.stackSize == var2.stackSize) { return null; }

			var3.onPickupFromSlot(par1EntityPlayer, var4);
		}

		return var2;
	}
}
