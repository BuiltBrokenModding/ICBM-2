package icbm.contraption.block;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockConcreate extends ItemBlock
{
	public ItemBlockConcreate(int id)
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
	public String getUnlocalizedName(ItemStack itemstack)
	{
		switch (itemstack.getItemDamage())
		{
			case 1:
				return this.getUnlocalizedName() + "Compact";
			case 2:
				return this.getUnlocalizedName() + "Reinforced";
		}

		return this.getUnlocalizedName();
	}
}
