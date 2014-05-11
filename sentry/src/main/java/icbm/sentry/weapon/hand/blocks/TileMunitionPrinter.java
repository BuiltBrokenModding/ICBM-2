package icbm.sentry.weapon.hand.blocks;

import icbm.core.prefab.TileICBM;
import icbm.sentry.ICBMSentry;
import icbm.sentry.weapon.hand.items.IItemAmmunition;
import icbm.sentry.weapon.hand.items.ItemWeapon;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import universalelectricity.api.energy.EnergyStorageHandler;
import calclavia.lib.prefab.tile.IRotatable;

public class TileMunitionPrinter extends TileICBM implements IRotatable, IInventory {

	ItemStack[] inventory = new ItemStack[2];
	
	private Item chosenMunition = ICBMSentry.itemMagazine;
	
	public TileMunitionPrinter() {
		setEnergyHandler(new EnergyStorageHandler(50000, 15000));
	}

	@Override
	public int getSizeInventory() {
		return inventory.length;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return inventory[i];
	}

	public boolean canPrint() {
		if(inventory[0] != null) {
			return false;
		}
		if(inventory[1] != null) {
			return false;
		}
		if(getEnergy(null) < 500) {
			return false;
		}
		return true;
	}

	public void updateEntity() {
		print();
	}
	
	public void print() {
		if (canPrint()) {
			ItemStack newStack = new ItemStack(chosenMunition);
			
			if(chosenMunition instanceof IItemAmmunition) {
				inventory[0] = newStack.copy();
			} else if(chosenMunition instanceof ItemWeapon) {
				inventory[1] = newStack.copy();
			}
		}
		setEnergy(null, getEnergy(null) - 500);
	}
	

	@Override
	public ItemStack decrStackSize(int i, int j) {
		if (inventory[i] != null) {
			ItemStack stack;

			if (inventory[i].stackSize <= j) {
				stack = inventory[i];
				inventory[i] = null;
				return stack;
			} else {
				stack = inventory[i].splitStack(j);

				if (inventory[i].stackSize == 0) {
					inventory[i] = null;
				}

				return stack;
			}
		} else {
			return null;
		}
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		if (inventory[i] != null) {
			ItemStack var2 = inventory[i];
			inventory[i] = null;
			return var2;
		} else {
			return null;
		}
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack stack) {
		inventory[i] = stack;

		if (stack != null && stack.stackSize > this.getInventoryStackLimit()) {
			stack.stackSize = this.getInventoryStackLimit();
		}
	}

	@Override
	public String getInvName() {
		return "Munition Printer";
	}

	@Override
	public boolean isInvNameLocalized() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return false;
	}

	@Override
	public void openChest() {

	}

	@Override
	public void closeChest() {

	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		switch(i) {
			case 0:
				return itemstack.getItem() instanceof IItemAmmunition;
			case 1:
				return itemstack.getItem() instanceof ItemWeapon;
		}
		return true;
	}
}
