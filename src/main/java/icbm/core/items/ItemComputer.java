package icbm.core.items;

import icbm.Reference;
import icbm.core.prefab.item.ItemICBMBase;
import net.minecraft.client.renderer.texture.IIconRegister;

public class ItemComputer extends ItemICBMBase
{
    public ItemComputer()
    {
        super("hackingComputer");
    }

    @Override
    public void registerIcons(IIconRegister iconRegister)
    {
        super.registerIcons(iconRegister);
        
        // Icon for base item.
        this.itemIcon = iconRegister.registerIcon(Reference.PREFIX + "hackingComputer");
    }
}
