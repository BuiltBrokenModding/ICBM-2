package universalelectricity.prefab;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class UETab extends CreativeTabs
{
	public static final UETab INSTANCE = new UETab("UniversalElectricity");
	private static ItemStack itemStack;

	public UETab(String par2Str)
	{
		super(CreativeTabs.getNextID(), par2Str);
		LanguageRegistry.instance().addStringLocalization("itemGroup.UniversalElectricity", "en_US", "Universal Electricity");
	}

	public static void setItemStack(ItemStack newItemStack)
	{
		if (itemStack == null)
		{
			itemStack = newItemStack;
		}
	}

	@Override
	public ItemStack getIconItemStack()
	{
		if (itemStack == null)
		{
			return new ItemStack(Block.blocksList[this.getTabIconItemIndex()]);
		}

		return itemStack;
	}
}
