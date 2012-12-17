package icbm.common.zhapin;

import icbm.common.BaoHu;
import icbm.common.ZhuYao;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;

public class ItShouLiuDan extends Item
{
	public ItShouLiuDan(int par1, int par2)
	{
		super(par1);
		this.setIconIndex(par2);
		this.setMaxStackSize(64);
		this.setCreativeTab(ZhuYao.TAB);
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
	}

	/**
	 * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack,
	 * world, entityPlayer
	 */
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
	{
		if (BaoHu.nengShouLiuDanBaoHu(par2World, new Vector3(par3EntityPlayer).toVector2()))
		{
			if (!par3EntityPlayer.capabilities.isCreativeMode)
			{
				--par1ItemStack.stackSize;
			}

			par2World.playSoundAtEntity(par3EntityPlayer, "random.fuse", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

			if (!par2World.isRemote)
			{
				par2World.spawnEntityInWorld(new EShouLiuDan(par2World, par3EntityPlayer, ZhaPin.list[par1ItemStack.getItemDamage()].getID()));
			}
		}
		else
		{
			par3EntityPlayer.sendChatToPlayer("Grenades are banned in this region.");
		}

		return par1ItemStack;
	}

	@Override
	public int getMetadata(int damage)
	{
		return damage;
	}

	@Override
	public String getItemNameIS(ItemStack itemstack)
	{
		return this.getItemName() + "." + ZhaPin.list[itemstack.getItemDamage()].getMingZi();
	}

	@Override
	public String getItemName()
	{
		return "icbm.grenade";
	}
	
	@Override
	public int getIconFromDamage(int i)
	{
		return this.iconIndex + i;
	}

	@Override
	public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
	{
		for (int i = 0; i < ZhaPin.E_YI_ID; i++)
		{
			par3List.add(new ItemStack(this, 1, i));
		}
	}
}
