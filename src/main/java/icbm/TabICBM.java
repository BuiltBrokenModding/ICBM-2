package icbm;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
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
            itemStack = new ItemStack(Block.tnt);
        }

        return itemStack;
    }

}
