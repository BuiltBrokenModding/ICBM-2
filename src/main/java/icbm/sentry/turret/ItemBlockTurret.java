package icbm.sentry.turret;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockTurret extends ItemBlock
{
    public ItemBlockTurret(int par1)
    {
        super(par1);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
        this.setMaxStackSize(1);
    }

    @Override
    public int getMetadata(int par1)
    {
        return 0;
    }

    @Override
    public String getUnlocalizedName(ItemStack par1ItemStack)
    {
        return this.getUnlocalizedName() + "." + par1ItemStack.getItemDamage();
    }
}
