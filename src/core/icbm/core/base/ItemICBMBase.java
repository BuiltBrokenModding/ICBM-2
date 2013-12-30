package icbm.core.base;

import icbm.core.CreativeTabICBM;
import icbm.core.ICBMConfiguration;
import icbm.core.ICBMCore;

public class ItemICBMBase extends ItemBase
{
    public ItemICBMBase(int id, String name)
    {
        super(id, name, ICBMConfiguration.CONFIGURATION, ICBMCore.PREFIX, CreativeTabICBM.INSTANCE);
    }

}
