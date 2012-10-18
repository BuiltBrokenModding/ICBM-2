package icbm;

import icbm.api.ICBM;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.Item;

public class ICBMItem extends Item
{
    public ICBMItem(String name, int id, int index, CreativeTabs tab)
    {
        super(id);
        this.setMaxStackSize(64);
        this.setIconIndex(index);
        this.setItemName(name);
        this.setCreativeTab(tab);
    }

    @Override
   	public String getTextureFile()
	{
       return ICBM.ITEM_TEXTURE_FILE;
	}
}