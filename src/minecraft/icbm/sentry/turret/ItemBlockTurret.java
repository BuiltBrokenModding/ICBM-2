package icbm.sentry.turret;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockTurret extends ItemBlock
{
	public ItemBlockTurret(int par1)
	{
		super(par1);
	}

	@Override
	public int getMetadata(int par1)
	{
		return par1;
	}

	@Override
	public String getItemNameIS(ItemStack par1ItemStack)
	{
		return this.getItemName() + "." + par1ItemStack.getItemDamage();
	}
}
