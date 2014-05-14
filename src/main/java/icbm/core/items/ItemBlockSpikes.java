package icbm.core.items;

import icbm.Reference;
import net.minecraft.item.ItemStack;
import resonant.lib.prefab.item.ItemBlockTooltip;

public class ItemBlockSpikes extends ItemBlockTooltip
{
    public ItemBlockSpikes(int par1)
    {
        super(par1);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int damage)
    {
        return damage;
    }

    @Override
    public String getUnlocalizedName(ItemStack itemstack)
    {
        return this.getUnlocalizedName() + "." + itemstack.getItemDamage();
    }

    @Override
    public String getUnlocalizedName()
    {
        return "tile." + Reference.PREFIX + "spikes";
    }
}
