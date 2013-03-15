package icbm.zhapin.zhapin;

import icbm.core.ZhuYao;
import icbm.core.di.ItICBM;
import icbm.zhapin.ZhuYaoZhaPin;
import icbm.zhapin.zhapin.ZhaPin.ZhaPinType;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
import universalelectricity.core.vector.Vector3;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItShouLiuDan extends ItICBM
{
	public static final List<Icon> ICONS = new ArrayList<Icon>();

	public ItShouLiuDan(int id)
	{
		super(id, "grenade");
		this.setMaxStackSize(16);
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
	}

	@Override
	public ItemStack onEaten(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
	{
		return par1ItemStack;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack par1ItemStack)
	{
		return EnumAction.bow;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack par1ItemStack)
	{
		return 3 * 20;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityPlayer)
	{
		if (itemStack != null)
		{
			int haoMa = ZhaPin.list[itemStack.getItemDamage()].getID();

			if (!ZhuYaoZhaPin.shiBaoHu(world, new Vector3(entityPlayer), ZhaPinType.SHOU_LIU_DAN, haoMa))
			{
				entityPlayer.setItemInUse(itemStack, this.getMaxItemUseDuration(itemStack));
			}
			else
			{
				entityPlayer.sendChatToPlayer("Grenades are banned in this region.");
			}
		}

		return itemStack;
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack itemStack, World world, EntityPlayer entityPlayer, int nengLiang)
	{
		if (!world.isRemote)
		{
			int haoMa = ZhaPin.list[itemStack.getItemDamage()].getID();

			if (!ZhuYaoZhaPin.shiBaoHu(world, new Vector3(entityPlayer), ZhaPinType.SHOU_LIU_DAN, haoMa))
			{
				if (!entityPlayer.capabilities.isCreativeMode)
				{
					itemStack.stackSize--;
					
					if (itemStack.stackSize <= 0)
					{
						entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, null);
					}
				}

				world.playSoundAtEntity(entityPlayer, "random.fuse", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
				world.spawnEntityInWorld(new EShouLiuDan(world, entityPlayer, haoMa, (float) (this.getMaxItemUseDuration(itemStack) - nengLiang) / (float) this.getMaxItemUseDuration(itemStack)));
			}
			else
			{
				entityPlayer.sendChatToPlayer("Grenades are banned in this region.");
			}
		}
	}

	@Override
	public int getMetadata(int damage)
	{
		return damage;
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack)
	{
		return this.getUnlocalizedName() + "." + ZhaPin.list[itemstack.getItemDamage()].getUnlocalizedName();
	}

	@Override
	public String getUnlocalizedName()
	{
		return "icbm.grenade";
	}

	@SideOnly(Side.CLIENT)
	public void func_94581_a(IconRegister par1IconRegister)
	{
		for (int i = 0; i < ZhaPin.E_YI_ID; i++)
		{
			ICONS.add(par1IconRegister.func_94245_a(ZhuYao.PREFIX + "grenade_" + ZhaPin.list[i].getUnlocalizedName()));
		}
	}

	@Override
	public Icon getIconFromDamage(int i)
	{
		return ICONS.get(i);
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
