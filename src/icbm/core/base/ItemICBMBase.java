package icbm.core.base;

import icbm.core.CreativeTabICBM;
import icbm.core.ICBMConfiguration;
import icbm.core.ICBMCore;
import calclavia.lib.base.ItemBase;

public class ItemICBMBase extends ItemBase
{
	public ItemICBMBase(int id, String name)
	{
		super(id, name, ICBMConfiguration.CONFIGURATION, ICBMCore.PREFIX, CreativeTabICBM.INSTANCE);
	}

}
