package icbm.core.items;

import icbm.Reference;
import net.minecraft.item.ItemStack;
import resonant.content.prefab.itemblock.ItemTooltip;

public class ItemBlockSpikes extends ItemTooltip
{
    public ItemBlockSpikes(int par1)
    {
        super();
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
