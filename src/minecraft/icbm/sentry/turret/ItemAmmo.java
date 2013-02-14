package icbm.sentry.turret;

import icbm.api.ICBMTab;
import icbm.sentry.ICBMSentry;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemAmmo extends Item
{
	public ItemAmmo(int id, int texture)
	{
		super(id);
		this.setIconIndex(texture);
		this.setItemName("ammo");
		this.setCreativeTab(ICBMTab.INSTANCE);
		this.setTextureFile(ICBMSentry.ITEM_TEXTURE_PATH);
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
		for (int i = 0; i < 4; i++)
		{
			par3List.add(new ItemStack(this, 1, i));
		}
	}
}