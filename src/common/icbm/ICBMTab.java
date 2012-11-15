package icbm;

import net.minecraft.src.CreativeTabs;
import net.minecraft.src.ItemStack;

public class ICBMTab extends CreativeTabs
{
	public ICBMTab()
	{
		super(CreativeTabs.getNextID(), "ICBM");
	}

	@Override
	public ItemStack getIconItemStack()
	{
		return new ItemStack(ZhuYao.bZha4Dan4);
	}

}
