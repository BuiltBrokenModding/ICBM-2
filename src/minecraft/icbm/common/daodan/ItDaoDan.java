package icbm.common.daodan;

import icbm.common.ICBMItem;
import icbm.common.zhapin.ZhaPin;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class ItDaoDan extends ICBMItem
{
	public ItDaoDan(String name, int id, int texture)
	{
		super(name, id, texture);
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
		this.maxStackSize = 1;
	}

	@Override
	public int getMetadata(int damage)
	{
		return damage;
	}

	@Override
	public String getItemNameIS(ItemStack itemstack)
	{
		return ZhaPin.list[itemstack.getItemDamage()].getDaoDanMing();
	}

	@Override
	public int getIconFromDamage(int i)
	{
		return this.iconIndex;
	}

	@Override
	public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
	{
		for (int i = 0; i < ZhaPin.E_SI_ID; i++)
		{
			par3List.add(new ItemStack(this, 1, i));
		}
	}
}
