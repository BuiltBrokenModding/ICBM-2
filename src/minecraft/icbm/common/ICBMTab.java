package icbm.common;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class ICBMTab extends CreativeTabs
{
	public ICBMTab()
	{
		super(CreativeTabs.getNextID(), "ICBM");
	}

	@Override
	public ItemStack getIconItemStack()
	{
		return new ItemStack(ZhuYao.bZhaDan);
	}

}
