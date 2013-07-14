package icbm.zhapin.zhapin;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class IBZhaDan extends ItemBlock {
	public IBZhaDan(int id) {
		super(id);
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
	}

	@Override
	public int getMetadata(int damage) {
		return damage;
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack) {
		return this.getUnlocalizedName()
				+ "."
				+ ZhaPinRegistry.get(itemstack.getItemDamage())
						.getUnlocalizedName();
	}

	@Override
	public String getUnlocalizedName() {
		return "icbm.explosive";
	}
}
