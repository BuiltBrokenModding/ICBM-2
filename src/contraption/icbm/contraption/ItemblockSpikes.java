package icbm.contraption;

import icbm.core.ICBMCore;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemblockSpikes extends ItemBlock
{
	public ItemblockSpikes(int par1)
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
		return "tile." + ICBMCore.PREFIX + "spikes";
	}
}
