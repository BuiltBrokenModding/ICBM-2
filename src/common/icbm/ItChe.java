package icbm;

import icbm.cart.EChe;
import icbm.zhapin.ZhaPin;

import java.util.List;

import net.minecraft.src.BlockRail;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

public class ItChe extends Item
{
	public ItChe(int id, int texture)
	{
		super(id);
		this.setMaxStackSize(1);
		this.setIconIndex(texture);
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
		this.setItemName("Explosive Cart");
		this.setCreativeTab(ZhuYao.TAB);
	}

	/**
	 * Callback for item usage. If the item does
	 * something special on right clicking, he
	 * will have one of those. Return True if
	 * something happen and false if it don't.
	 * This is for ITEMS, not BLOCKS
	 */
	@Override
	public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int x, int y, int z, int par7, float par8, float par9, float par10)
	{
		int var11 = par3World.getBlockId(x, y, z);

		if (BlockRail.isRailBlock(var11))
		{
			if (!par3World.isRemote)
			{
				par3World.spawnEntityInWorld(new EChe(par3World, (double) ((float) x + 0.5F), (double) ((float) y + 0.5F), (double) ((float) z + 0.5F), par1ItemStack.getItemDamage()));
			}

			--par1ItemStack.stackSize;
			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	public int getMetadata(int damage)
	{
		return damage;
	}

	@Override
	public String getItemNameIS(ItemStack itemstack)
	{
		return ZhaPin.list[itemstack.getItemDamage()].getCheMing();
	}

	@Override
	public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
	{
		for (int metadata = 0; metadata < ZhaPin.MAX_TIER_TWO; metadata++)
		{
			par3List.add(new ItemStack(par1, 1, metadata));
		}
	}
}
