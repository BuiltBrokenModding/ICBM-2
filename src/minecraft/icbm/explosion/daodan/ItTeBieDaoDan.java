package icbm.explosion.daodan;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class ItTeBieDaoDan extends ItDaoDan
{
	public ItTeBieDaoDan(int id, int texture)
	{
		super(id, texture);
	}

	@Override
	public String getItemNameIS(ItemStack itemstack)
	{
		if (itemstack.getItemDamage() == 0)
		{
			return this.getItemName() + ".missileModule";
		}

		return this.getItemName() + "." + DaoDan.list[itemstack.getItemDamage() + 100].getMingZing();
	}

	@Override
	public String getItemName()
	{
		return "icbm.missile";
	}

	@Override
	public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
	{
		for (int i = 0; i < DaoDan.MAX_DAO_DAN + 1; i++)
		{
			par3List.add(new ItemStack(this, 1, i));
		}
	}
}
