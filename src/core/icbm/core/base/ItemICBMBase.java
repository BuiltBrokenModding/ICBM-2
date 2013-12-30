package icbm.core.base;

import net.minecraft.item.Item;
import icbm.core.CreativeTabICBM;
import icbm.core.ICBMConfiguration;
import icbm.core.ICBMCore;

public class ItemICBMBase extends Item
{
    public ItemICBMBase(int id, String name)
    {
        super(ICBMConfiguration.CONFIGURATION.getItem("name", id).getInt(id));
        this.setUnlocalizedName(ICBMCore.PREFIX + name);
        this.setCreativeTab(CreativeTabICBM.INSTANCE);
        this.setTextureName(ICBMCore.PREFIX + name);
    }

}
