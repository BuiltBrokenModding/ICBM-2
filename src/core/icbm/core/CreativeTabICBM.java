package icbm.core;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class CreativeTabICBM extends CreativeTabs
{
	public static final CreativeTabICBM INSTANCE = new CreativeTabICBM();

	public static ItemStack itemStack;

	public CreativeTabICBM()
	{
		super(CreativeTabs.getNextID(), "ICBM");
	}

	@Override
	public ItemStack getIconItemStack()
	{
		if (itemStack == null)
		{
			itemStack = new ItemStack(Block.tnt);
		}

		return itemStack;
	}

}
