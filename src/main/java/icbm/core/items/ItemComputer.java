package icbm.core.items;

import net.minecraft.client.renderer.texture.IconRegister;
import icbm.Reference;
import icbm.core.prefab.item.ItemICBMBase;

public class ItemComputer extends ItemICBMBase
{
    public ItemComputer(int id)
    {
        super(id, "hackingComputer");
    }

    @Override
    public void registerIcons(IconRegister iconRegister)
    {
        super.registerIcons(iconRegister);
        
        // Icon for base item.
        this.itemIcon = iconRegister.registerIcon(Reference.PREFIX + "hackingComputer");
    }
}
