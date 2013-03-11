package icbm.zhapin.zhapin;

import icbm.api.ICBMTab;
import icbm.core.ZhuYao;
import icbm.zhapin.ZhuYaoZhaPin;
import icbm.zhapin.zhapin.ZhaPin.ZhaPinType;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;

public class ItShouLiuDan extends Item
{
	public ItShouLiuDan(int par1, int par2)
	{
		super(par1);
		this.setIconIndex(par2);
		this.setMaxStackSize(64);
		this.setCreativeTab(ICBMTab.INSTANCE);
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
		this.setTextureFile(ZhuYao.ITEM_PATH);
	}

	/**
	 * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack,
	 * world, entityPlayer
	 */
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World world, EntityPlayer entityPlayer)
	{
		if (!world.isRemote)
		{
			int haoMa = ZhaPin.list[par1ItemStack.getItemDamage()].getID();
			if (!ZhuYaoZhaPin.shiBaoHu(world, new Vector3(entityPlayer), ZhaPinType.SHOU_LIU_DAN, haoMa))
			{
				if (!entityPlayer.capabilities.isCreativeMode)
				{
					--par1ItemStack.stackSize;
				}

				world.playSoundAtEntity(entityPlayer, "random.fuse", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
				world.spawnEntityInWorld(new EShouLiuDan(world, entityPlayer, haoMa));
			}
			else
			{
				entityPlayer.sendChatToPlayer("Grenades are banned in this region.");
			}
		}

		return par1ItemStack;
	}

	@Override
	public int getMetadata(int damage)
	{
		return damage;
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack)
	{
		return this.getUnlocalizedName() + "." + ZhaPin.list[itemstack.getItemDamage()].getName();
	}

	@Override
	public String getUnlocalizedName()
	{
		return "icbm.grenade";
	}

	@Override
	public Icon getIconFromDamage(int i)
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
