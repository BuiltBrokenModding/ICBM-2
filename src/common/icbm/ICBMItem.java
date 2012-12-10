package icbm;

import net.minecraft.src.CreativeTabs;
import net.minecraft.src.Item;

public class ICBMItem extends Item
{
	public ICBMItem(String name, int id, int index)
	{
		super(id);
		this.setMaxStackSize(64);
		this.setIconIndex(index);
		this.setItemName(name);
		this.setCreativeTab(ZhuYao.TAB);
	}

	@Override
	public String getTextureFile()
	{
		return ZhuYao.ITEM_TEXTURE_FILE;
	}
}