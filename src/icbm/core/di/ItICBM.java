package icbm.core.di;

import icbm.core.ICBMTab;
import icbm.core.ZhuYaoBase;
import net.minecraft.item.Item;

public class ItICBM extends Item
{
	public ItICBM(int id, String name)
	{
		super(ZhuYaoBase.CONFIGURATION.getItem(name, id).getInt());
		this.setUnlocalizedName(ZhuYaoBase.PREFIX + name);
		this.setCreativeTab(ICBMTab.INSTANCE);
	}
}
