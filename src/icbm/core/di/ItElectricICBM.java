package icbm.core.di;

import icbm.core.ICBMTab;
import icbm.core.SheDing;
import icbm.core.ZhuYaoICBM;
import calclavia.lib.ItemUniversalElectric;

public abstract class ItElectricICBM extends ItemUniversalElectric {
	public ItElectricICBM(int itemID, String name) {
		super(SheDing.CONFIGURATION.getItem(name, itemID).getInt());
		this.setUnlocalizedName(ZhuYaoICBM.PREFIX + name);
		this.setCreativeTab(ICBMTab.INSTANCE);
		this.func_111206_d(ZhuYaoICBM.PREFIX + name);

	}
}
