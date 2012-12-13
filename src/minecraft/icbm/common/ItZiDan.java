package icbm.common;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class ItZiDan extends ICBMItem
{
	public ItZiDan(String name, int par1, int par2)
	{
		super(name, par1, par2);
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
		this.maxStackSize = 16;
	}

	@Override
	public int getMetadata(int damage)
	{
		return damage;
	}

	@Override
	public String getItemNameIS(ItemStack itemstack)
	{
		return "item.bullet." + itemstack.getItemDamage();
	}

	@Override
	public int getIconFromDamage(int i)
	{
		return this.iconIndex + i;
	}

	@Override
	public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
	{
		for (int i = 0; i < 2; i++)
		{
			par3List.add(new ItemStack(this, 1, i));
		}
	}
}
