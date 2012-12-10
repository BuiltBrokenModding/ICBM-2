package icbm.daodan;

import icbm.ICBMItem;
import icbm.ZhuYao;
import icbm.zhapin.ZhaPin;

import java.util.List;

import net.minecraft.src.CreativeTabs;
import net.minecraft.src.ItemStack;

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
