package icbm.core.prefab.item;

import calclavia.lib.prefab.item.ItemTooltip;
import icbm.Reference;
import icbm.TabICBM;
import net.minecraft.item.Item;

/** Prefab for ICBM items that sets the creative tab, texture name, and translation name
 * 
 * @author DarkGuardsman */
public class ItemICBMBase extends ItemTooltip
{
    public ItemICBMBase(int id, String name)
    {
        super(id);
        this.setUnlocalizedName(Reference.PREFIX + name);
        this.setCreativeTab(TabICBM.INSTANCE);
        this.setTextureName(Reference.PREFIX + name);
    }

}
