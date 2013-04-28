package icbm.core.di;

import icbm.core.ICBMTab;
import icbm.core.ZhuYao;
import calclavia.lib.ItemUniversalElectric;

public abstract class ItElectricICBM extends ItemUniversalElectric
{
	public ItElectricICBM(int itemID, String name)
	{
		super(ZhuYao.CONFIGURATION.getItem(name, itemID).getInt());
		this.setUnlocalizedName(ZhuYao.PREFIX + name);
		this.setCreativeTab(ICBMTab.INSTANCE);
	}
}
