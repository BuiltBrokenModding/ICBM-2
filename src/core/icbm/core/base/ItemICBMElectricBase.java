package icbm.core.base;

import universalelectricity.api.item.ItemElectric;
import icbm.core.CreativeTabICBM;
import icbm.core.ICBMConfiguration;
import icbm.core.ICBMCore;

public abstract class ItemICBMElectricBase extends ItemElectric
{
    public ItemICBMElectricBase(int id, String name)
    {
        super(ICBMConfiguration.CONFIGURATION.getItem("name", id).getInt(id));
        this.setUnlocalizedName(ICBMCore.PREFIX + name);
        this.setCreativeTab(CreativeTabICBM.INSTANCE);
        this.setTextureName(ICBMCore.PREFIX + name);
    }

}
