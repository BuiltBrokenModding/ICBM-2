package icbm.zhapin.zhapin.daodan;

import icbm.core.di.ItICBMBase;
import icbm.zhapin.zhapin.ZhaPin;
import icbm.zhapin.zhapin.ZhaPinRegistry;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class ItDaoDan extends ItICBMBase
{
	public ItDaoDan(int id, String name)
	{
		super(id, name);
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
		this.setMaxStackSize(1);
	}

	@Override
	public int getMetadata(int damage)
	{
		return damage;
	}

	@Override
	public String getUnlocalizedName(ItemStack itemStack)
	{
		return this.getUnlocalizedName() + "." + ZhaPinRegistry.get(itemStack.getItemDamage()).getUnlocalizedName();
	}

	@Override
	public String getUnlocalizedName()
	{
		return "icbm.missile";
	}

	@Override
	public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
	{
		for (ZhaPin zhaPin : ZhaPinRegistry.getAllDaoDan())
		{
			if (zhaPin.hasMissileForm())
			{
				par3List.add(new ItemStack(par1, 1, zhaPin.getID()));
			}
		}
	}
}
