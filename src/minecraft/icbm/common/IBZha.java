package icbm.common;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class IBZha extends ItemBlock
{
	public IBZha(int par1)
	{
		super(par1);
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
	}

	@Override
	public int getMetadata(int par1)
	{
		return par1;
	}

	@Override
	public String getItemNameIS(ItemStack itemstack)
	{
		return this.getItemName() + "." + itemstack.getItemDamage();
	}

	@Override
	public String getItemName()
	{
		return "item.spike";
	}
}
