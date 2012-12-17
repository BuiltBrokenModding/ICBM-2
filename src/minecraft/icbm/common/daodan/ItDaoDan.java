package icbm.common.daodan;

import icbm.common.ZhuYao;
import icbm.common.zhapin.ZhaPin;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItDaoDan extends Item
{
	public ItDaoDan(int id, int texture)
	{
		super(id);
		this.setIconIndex(texture);
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
		this.setMaxStackSize(1);
		this.setCreativeTab(ZhuYao.TAB);
	}

	@Override
	public int getMetadata(int damage)
	{
		return damage;
	}

	@Override
	public String getItemNameIS(ItemStack itemstack)
	{
		return this.getItemName() + "." + ZhaPin.list[itemstack.getItemDamage()].getMingZi();
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
		for (int i = 0; i < ZhaPin.E_SI_ID; i++)
		{
			par3List.add(new ItemStack(this, 1, i));
		}
	}
}
