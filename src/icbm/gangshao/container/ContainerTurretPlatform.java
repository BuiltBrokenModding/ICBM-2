package icbm.gangshao.container;

import icbm.gangshao.IAmmunition;
import icbm.gangshao.ITurretUpgrade;
import icbm.gangshao.SlotTurret;
import icbm.gangshao.access.AccessLevel;
import icbm.gangshao.platform.TPaoTaiZhan;
import icbm.gangshao.turret.ItemAmmo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import universalelectricity.core.item.IItemElectric;

public class ContainerTurretPlatform extends ContainerTerminal
{
	private TPaoTaiZhan tileEntity;

	public ContainerTurretPlatform(InventoryPlayer inventoryPlayer, TPaoTaiZhan tileEntity)
	{
		super(inventoryPlayer, tileEntity);
		this.tileEntity = tileEntity;
		int row;

		// Turret Ammunition Slots
		for (row = 0; row < 3; row++)
		{
			for (int column = 0; column < 4; column++)
			{
				this.addSlotToContainer(new SlotTurret(tileEntity, column + row * 4, 8 + column * 18, 40 + row * 18, IAmmunition.class, IItemElectric.class));
			}
		}

		// Turret Upgrade Slots
		for (int i = 0; i < 4; i++)
		{
			this.addSlotToContainer(new SlotTurret(tileEntity, i + TPaoTaiZhan.UPGRADE_START_INDEX, 89 + i * 18, 77, ITurretUpgrade.class));
		}

		// Player Inventory
		for (row = 0; row < 3; ++row)
		{
			for (int slot = 0; slot < 9; ++slot)
			{
				this.addSlotToContainer(new Slot(inventoryPlayer, slot + row * 9 + 9, 8 + slot * 18, 97 + row * 18));
			}
		}
		for (row = 0; row < 9; ++row)
		{
			this.addSlotToContainer(new Slot(inventoryPlayer, row, 8 + row * 18, 155));
		}
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
					if (itemStack.getItem() instanceof ItemAmmo || itemStack.getItem() instanceof IItemElectric)
					{
						if (!this.mergeItemStack(itemStack, 0, TPaoTaiZhan.UPGRADE_START_INDEX, false))
						{
							return null;
						}
					}
					else if (itemStack.getItem() instanceof ITurretUpgrade)
					{
						if (!this.mergeItemStack(itemStack, TPaoTaiZhan.UPGRADE_START_INDEX, this.tileEntity.containingItems.length, false))
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
}
