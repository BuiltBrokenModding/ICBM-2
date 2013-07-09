package icbm.zhapin.zhapin.daodan;

import icbm.core.di.ItICBM;
import icbm.zhapin.zhapin.ZhaPin;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class ItDaoDan extends ItICBM
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
		if (itemStack.getItemDamage() < ZhaPin.list.length)
		{
			return this.getUnlocalizedName() + "." + ZhaPin.list[itemStack.getItemDamage()].getUnlocalizedName();
		}

		return "";
	}

	@Override
	public String getUnlocalizedName()
	{
		return "icbm.missile";
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
