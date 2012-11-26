package icbm.slots;

import icbm.ItZiDan;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Slot;

/**
 * This slot should be used by any container that needs the slot for an electric item only
 * 
 * @author Calclavia
 * 
 */
public class SZiDan extends Slot
{
	public SZiDan(IInventory par2IInventory, int par3, int par4, int par5)
	{
		super(par2IInventory, par3, par4, par5);
	}

	/**
	 * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
	 */
	@Override
	public boolean isItemValid(ItemStack par1ItemStack)
	{
		return par1ItemStack.getItem() instanceof ItZiDan;
	}
}
