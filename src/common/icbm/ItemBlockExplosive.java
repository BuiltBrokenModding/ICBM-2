package icbm;

import icbm.explosives.Explosive;
import net.minecraft.src.ItemBlock;
import net.minecraft.src.ItemStack;

public class ItemBlockExplosive extends ItemBlock
{
    public ItemBlockExplosive(int id)
    {
        super(id);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    @Override
	public int getMetadata(int damage)
    {
        return damage;
    }

    @Override
	public String getItemNameIS(ItemStack itemstack)
    {
        return (new StringBuilder())
                .append(super.getItemName())
                .append(".")
                .append(Explosive.list[itemstack.getItemDamage()]+" Explosives")
                .toString();
    }
}
