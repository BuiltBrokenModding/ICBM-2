package icbm.core.base;

import icbm.Reference;
import icbm.core.CreativeTabICBM;
import icbm.core.ICBMConfiguration;
import universalelectricity.api.item.ItemElectric;

public abstract class ItemICBMElectricBase extends ItemElectric
{
    public ItemICBMElectricBase(int id, String name)
    {
        super(ICBMConfiguration.CONFIGURATION.getItem("name", id).getInt(id));
        this.setUnlocalizedName(Reference.PREFIX + name);
        this.setCreativeTab(CreativeTabICBM.INSTANCE);
        this.setTextureName(Reference.PREFIX + name);
    }

}
