package icbm.core.base;

import icbm.core.CreativeTabICBM;
import icbm.core.ICBMConfiguration;
import icbm.core.ICBMCore;
import calclavia.lib.base.ItemElectricBase;

public abstract class ItemICBMElectricBase extends ItemElectricBase
{
    public ItemICBMElectricBase(int id, String name)
    {
        super(id, name, ICBMConfiguration.CONFIGURATION, ICBMCore.PREFIX, CreativeTabICBM.INSTANCE);
    }

}
