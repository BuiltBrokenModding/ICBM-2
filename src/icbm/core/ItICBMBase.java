package icbm.core;

import calclavia.lib.base.ItemBase;

public class ItICBMBase extends ItemBase
{
	public ItICBMBase(int id, String name)
	{
		super(id, name, SheDing.CONFIGURATION, ZhuYaoICBM.PREFIX, ICBMTab.INSTANCE);
	}

}
