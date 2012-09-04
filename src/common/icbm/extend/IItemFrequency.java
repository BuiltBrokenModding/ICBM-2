package icbm.extend;

import net.minecraft.src.ItemStack;

public interface IItemFrequency
{
	public short getFrequency(ItemStack par1ItemStack);
	
	public void setFrequency(ItemStack par1ItemStack, short frequency);
}
