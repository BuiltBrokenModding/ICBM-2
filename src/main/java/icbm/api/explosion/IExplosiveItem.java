package icbm.api.explosion;

import net.minecraft.item.ItemStack;

/**
 * Created by robert on 12/2/2014.
 */
public interface IExplosiveItem
{
    public IExplosive getExplosive(ItemStack stack);
}
