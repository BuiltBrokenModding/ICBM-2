package icbm.explosion.explosive;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import resonant.content.prefab.itemblock.ItemBlockTooltip;

public class ItemBlockExplosive extends ItemBlockTooltip
{
    public ItemBlockExplosive(Block block)
    {
        super(block);
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
        return this.getUnlocalizedName() + "." + ExplosiveRegistry.get(itemstack.getItemDamage()).getUnlocalizedName();
    }

    @Override
    public String getUnlocalizedName()
    {
        return "icbm.explosive";
    }
}
