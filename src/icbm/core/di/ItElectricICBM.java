package icbm.core.di;

import icbm.core.ICBMTab;
import icbm.core.ZhuYaoBase;
import calclavia.lib.ItemUniversalElectric;

public abstract class ItElectricICBM extends ItemUniversalElectric
{
	public ItElectricICBM(int itemID, String name)
	{
		super(ZhuYaoBase.CONFIGURATION.getItem(name, itemID).getInt());
		this.setUnlocalizedName(ZhuYaoBase.PREFIX + name);
		this.setCreativeTab(ICBMTab.INSTANCE);
	}
}
