package icbm.zhapin;

import net.minecraft.src.ItemBlock;
import net.minecraft.src.ItemStack;

public class IBZhaDan extends ItemBlock
{
    public IBZhaDan(int id)
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
                .append(ZhaPin.list[itemstack.getItemDamage()]+" Explosives")
                .toString();
    }
}
