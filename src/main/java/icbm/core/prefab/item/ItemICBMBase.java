package icbm.core.prefab.item;

import icbm.Reference;
import icbm.core.CreativeTabICBM;
import icbm.core.Settings;
import net.minecraft.item.Item;

public class ItemICBMBase extends Item
{
    public ItemICBMBase(int id, String name)
    {
        super(Settings.CONFIGURATION.getItem(name, id).getInt(id));
        this.setUnlocalizedName(Reference.PREFIX + name);
        this.setCreativeTab(CreativeTabICBM.INSTANCE);
        this.setTextureName(Reference.PREFIX + name);
    }

}
