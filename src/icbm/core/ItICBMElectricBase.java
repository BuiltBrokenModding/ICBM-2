package icbm.core;

import calclavia.lib.base.ItemElectricBase;

public abstract class ItICBMElectricBase extends ItemElectricBase
{
	public ItICBMElectricBase(int id, String name)
	{
		super(id, name, SheDing.CONFIGURATION, ZhuYaoICBM.PREFIX, ICBMTab.INSTANCE);
	}

}
