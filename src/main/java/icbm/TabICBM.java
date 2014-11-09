package icbm;

import icbm.explosion.ICBMExplosion;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class TabICBM extends CreativeTabs
{
    public static final TabICBM INSTANCE = new TabICBM();

    public static ItemStack itemStack;

    public TabICBM()
    {
        super(CreativeTabs.getNextID(), "ICBM");
    }

    @Override
    public ItemStack getIconItemStack()
    {
        if (itemStack == null)
        {
            itemStack = new ItemStack(Blocks.tnt);
        }

        return itemStack;
    }

	@Override
	public Item getTabIconItem() {
		// TODO Auto-generated method stub
		return ICBMExplosion.itemRocketLauncher;
	}

}
