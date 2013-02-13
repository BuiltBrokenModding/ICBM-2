package icbm.api;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotSpecific extends Slot
{
	public ItemStack[] itemStacks;
	public boolean isMetadataSensitive = false;

	public SlotSpecific(IInventory par2IInventory, int par3, int par4, int par5, ItemStack... itemStacks)
	{
		super(par2IInventory, par3, par4, par5);
		this.itemStacks = itemStacks;
	}

	public SlotSpecific setMetadataSensitive()
	{
		this.isMetadataSensitive = true;
		return this;
	}

	/**
	 * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
	 */
	@Override
	public boolean isItemValid(ItemStack compareStack)
	{
		for (ItemStack itemStack : itemStacks)
		{
			if (compareStack.isItemEqual(itemStack) || (!this.isMetadataSensitive && compareStack.itemID == itemStack.itemID))
			{
				return true;
			}
		}
		return false;
	}
}
