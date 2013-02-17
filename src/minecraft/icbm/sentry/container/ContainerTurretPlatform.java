package icbm.sentry.container;

import icbm.sentry.SlotAmmunition;
import icbm.sentry.api.ITerminal;
import icbm.sentry.platform.TileEntityTurretPlatform;
import icbm.sentry.terminal.AccessLevel;
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
				this.addSlotToContainer(new SlotAmmunition(tileEntity, column + row * 4, 8 + column * 18, 40 + row * 18));
			}
		}

		// Turret Upgrade Slots
		for (int i = 0; i < 4; i++)
		{
			this.addSlotToContainer(new Slot(tileEntity, i + TileEntityTurretPlatform.UPGRADE_START_INDEX, 89 + i * 18, 77));
		}

		// Player Inventory
		for (row = 0; row < 3; ++row)
		{
			for (int slot = 0; slot < 9; ++slot)
			{
				this.addSlotToContainer(new Slot(player, slot + row * 9 + 9, 8 + slot * 18, 97 + row * 18));
			}
		}
		for (row = 0; row < 9; ++row)
		{
			this.addSlotToContainer(new Slot(player, row, 8 + row * 18, 155));
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
	public ItemStack transferStackInSlot(EntityPlayer entityPlayer, int slotID)
	{
		ItemStack var2 = null;
		Slot slot = (Slot) this.inventorySlots.get(slotID);

		if (slot != null && slot.getHasStack())
		{
			ItemStack itemStack = slot.getStack();
			var2 = itemStack.copy();

			if (slotID > this.tileEntity.containingItems.length - 1)
			{
				if (this.tileEntity.getUserAccess(entityPlayer.username).ordinal() > AccessLevel.NONE.ordinal())
				{
					if (itemStack.getItem() instanceof ItemAmmo)
					{
						if (!this.mergeItemStack(itemStack, 0, TileEntityTurretPlatform.UPGRADE_START_INDEX, false))
						{
							return null;
						}
					}
					else if (itemStack.getItem() instanceof IModifier)
					{
						if (!this.mergeItemStack(itemStack, TileEntityTurretPlatform.UPGRADE_START_INDEX, this.tileEntity.containingItems.length, false))
						{
							return null;
						}
					}
				}
				else if (slotID < this.tileEntity.containingItems.length + 27)
				{
					if (!this.mergeItemStack(itemStack, this.tileEntity.containingItems.length + 27, this.tileEntity.containingItems.length + 36, false))
					{
						return null;
					}
				}
				else if (!this.mergeItemStack(itemStack, this.tileEntity.containingItems.length, this.tileEntity.containingItems.length + 27, false))
				{
					return null;
				}
			}
			else if (!this.mergeItemStack(itemStack, this.tileEntity.containingItems.length, 36 + this.tileEntity.containingItems.length, false))
			{
				return null;
			}

			if (itemStack.stackSize == 0)
			{
				slot.putStack((ItemStack) null);
			}
			else
			{
				slot.onSlotChanged();
			}

			if (itemStack.stackSize == var2.stackSize)
			{
				return null;
			}

			slot.onPickupFromSlot(entityPlayer, itemStack);
		}

		return var2;
	}

	@Override
	public boolean canInteractWith(EntityPlayer par1EntityPlayer)
	{
		return this.tileEntity.isUseableByPlayer(par1EntityPlayer);
	}
}
