package icbm.core.di;

import icbm.core.ICBMTab;
import icbm.core.SheDing;
import icbm.core.ZhuYaoICBM;
import calclavia.lib.base.ItemBase;

public class ItICBMBase extends ItemBase
{
	public ItICBMBase(int id, String name)
	{
		super(id, name, SheDing.CONFIGURATION, ZhuYaoICBM.PREFIX, ICBMTab.INSTANCE);
	}

}
