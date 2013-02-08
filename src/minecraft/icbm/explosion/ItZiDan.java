package icbm.explosion;

import icbm.api.ICBMTab;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItZiDan extends Item
{
	public ItZiDan(int par1, int par2)
	{
		super(par1);
		this.setMaxStackSize(16);
		this.setIconIndex(par2);
		this.setItemName("bullet");
		this.setCreativeTab(ICBMTab.INSTANCE);
		this.setTextureFile(ZhuYaoExplosion.ITEM_TEXTURE_FILE);
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
		return this.getItemName() + "." + itemstack.getItemDamage();
	}

	@Override
	public int getIconFromDamage(int i)
	{
		return this.iconIndex + i;
	}

	@Override
	public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
	{
		for (int i = 0; i < 2; i++)
		{
			par3List.add(new ItemStack(this, 1, i));
		}
	}
}
