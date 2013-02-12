package icbm.api;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotSpecific extends Slot
{
	private ItemStack[] itemStacks;

	public SlotSpecific(IInventory par2IInventory, int par3, int par4, int par5, ItemStack... itemStacks)
	{
		super(par2IInventory, par3, par4, par5);
		this.itemStacks = itemStacks;
	}

	/**
	 * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
	 */
	@Override
	public boolean isItemValid(ItemStack compareStack)
	{
		for (ItemStack itemStack : itemStacks)
		{
			if (compareStack.isItemEqual(itemStack))
			{
				return true;
			}
		}
		return false;
	}
}
