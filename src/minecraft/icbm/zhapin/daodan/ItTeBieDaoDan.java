package icbm.zhapin.daodan;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class ItTeBieDaoDan extends ItDaoDan
{
	public ItTeBieDaoDan(int id)
	{
		super(id);
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack)
	{
		if (itemstack.getItemDamage() == 0)
		{
			return this.getUnlocalizedName() + ".missileModule";
		}

		return this.getUnlocalizedName() + "." + DaoDan.list[itemstack.getItemDamage() + 100].getMingZing();
	}

	@Override
	public String getUnlocalizedName()
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
