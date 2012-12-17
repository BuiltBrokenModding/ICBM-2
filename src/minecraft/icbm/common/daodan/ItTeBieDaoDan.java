package icbm.common.daodan;

import icbm.common.ZhuYao;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class ItTeBieDaoDan extends ItDaoDan
{
	public ItTeBieDaoDan(int id, int texture)
	{
		super(id, texture);
		this.setCreativeTab(ZhuYao.TAB);
	}

	@Override
	public String getItemNameIS(ItemStack itemstack)
	{
		return this.getItemName() + "." + DaoDan.list[itemstack.getItemDamage()-1].getTranslatedMing();
	}
	
	@Override
	public String getItemName()
	{
		return "icbm.missile";
	}

	@Override
	public int getIconFromDamage(int i)
	{
		return this.iconIndex;
	}

	@Override
	public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
	{
		for (int i = 0; i < DaoDan.list.length; i++)
		{
			par3List.add(new ItemStack(this, 1, i));
		}
	}	
}
