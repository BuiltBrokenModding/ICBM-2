package icbm.core.di;

import icbm.core.ICBMTab;
import icbm.core.SheDing;
import icbm.core.ZhuYaoICBM;
import calclavia.lib.base.ItemElectricBase;

public abstract class ItICBMElectricBase extends ItemElectricBase
{
	public ItICBMElectricBase(int id, String name)
	{
		super(id, name, SheDing.CONFIGURATION, ZhuYaoICBM.PREFIX, ICBMTab.INSTANCE);
	}

}
