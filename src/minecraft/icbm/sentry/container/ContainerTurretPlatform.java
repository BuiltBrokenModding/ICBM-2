package icbm.sentry.container;

import icbm.api.SlotSpecific;
import icbm.sentry.ICBMSentry;
import icbm.sentry.platform.TileEntityTurretPlatform;
import icbm.sentry.turret.ItemAmmo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import universalelectricity.prefab.modifier.IModifier;

public class ContainerTurretPlatform extends Container
{
	private TileEntityTurretPlatform tileEntity;

	public ContainerTurretPlatform(InventoryPlayer player, TileEntityTurretPlatform tileEntity)
	{
		this.tileEntity = tileEntity;

		int row;

		// Turret Ammunition Slots
		for (row = 0; row < 3; row++)
		{
			for (int column = 0; column < 4; column++)
			{
				this.addSlotToContainer(new SlotSpecific(tileEntity, column + row * 4, 8 + column * 18, 27 + row * 18, ICBMSentry.conventionalBullet.copy()));
			}
		}

		// Turret Upgrade Slots
		for (int i = 0; i < 4; i++)
		{
			this.addSlotToContainer(new Slot(tileEntity, i + TileEntityTurretPlatform.UPGRADE_START_INDEX, 89 + i * 18, 64));
		}

		// Player Inventory
		for (row = 0; row < 3; ++row)
		{
			for (int slot = 0; slot < 9; ++slot)
			{
				this.addSlotToContainer(new Slot(player, slot + row * 9 + 9, 8 + slot * 18, 84 + row * 18));
			}
		}
		for (row = 0; row < 9; ++row)
		{
			this.addSlotToContainer(new Slot(player, row, 8 + row * 18, 142));
		}

		this.tileEntity.openChest();
	}

	@Override
	public void onCraftGuiClosed(EntityPlayer par1EntityPlayer)
	{
		this.tileEntity.closeChest();
		super.onCraftGuiClosed(par1EntityPlayer);
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

			if (par1 > this.tileEntity.containingItems.length)
			{
				if (var4.getItem() instanceof ItemAmmo)
				{
					if (!this.mergeItemStack(var4, 0, TileEntityTurretPlatform.UPGRADE_START_INDEX, false))
					{
						return null;
					}
				}
				else if (var4.getItem() instanceof IModifier)
				{
					if (!this.mergeItemStack(var4, TileEntityTurretPlatform.UPGRADE_START_INDEX, this.tileEntity.containingItems.length, false))
					{
						return null;
					}
				}
				else if (par1 < this.tileEntity.containingItems.length + 27)
				{
					if (!this.mergeItemStack(var4, this.tileEntity.containingItems.length + 27, this.tileEntity.containingItems.length + 36, false))
					{
						return null;
					}
				}
				else if (par1 >= this.tileEntity.containingItems.length + 27 && par1 < this.tileEntity.containingItems.length + 36 && !this.mergeItemStack(var4, this.tileEntity.containingItems.length, this.tileEntity.containingItems.length + 27, false))
				{
					return null;
				}
			}
			else if (!this.mergeItemStack(var4, this.tileEntity.containingItems.length, 36 + this.tileEntity.containingItems.length, false))
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

	@Override
	public boolean canInteractWith(EntityPlayer par1EntityPlayer)
	{
		return this.tileEntity.isUseableByPlayer(par1EntityPlayer);
	}
}
