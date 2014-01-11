package icbm.explosion.missile;

import icbm.Reference;
import icbm.api.explosion.ExplosionEvent.ExplosivePreDetonationEvent;
import icbm.api.explosion.ExplosiveType;
import icbm.core.prefab.item.ItemICBMBase;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemGrenade extends ItemICBMBase
{
	public static final Icon[] ICONS = new Icon[256];

	public ItemGrenade(int id)
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
			Explosive zhaPin = ExplosiveRegistry.get(itemStack.getItemDamage());
			ExplosivePreDetonationEvent evt = new ExplosivePreDetonationEvent(world, entityPlayer, ExplosiveType.ITEM, zhaPin);
			MinecraftForge.EVENT_BUS.post(evt);

			if (!evt.isCanceled())
			{
				entityPlayer.setItemInUse(itemStack, this.getMaxItemUseDuration(itemStack));
			}
			else
			{
				entityPlayer.sendChatToPlayer(ChatMessageComponent.createFromText("Grenades are banned in this region."));
			}
		}

		return itemStack;
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack itemStack, World world, EntityPlayer entityPlayer, int nengLiang)
	{
		if (!world.isRemote)
		{
			Explosive zhaPin = ExplosiveRegistry.get(itemStack.getItemDamage());
			ExplosivePreDetonationEvent evt = new ExplosivePreDetonationEvent(world, entityPlayer, ExplosiveType.ITEM, zhaPin);
			MinecraftForge.EVENT_BUS.post(evt);

			if (!evt.isCanceled())
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
				world.spawnEntityInWorld(new EntityGrenade(world, entityPlayer, zhaPin.getID(), (float) (this.getMaxItemUseDuration(itemStack) - nengLiang) / (float) this.getMaxItemUseDuration(itemStack)));
			}
			else
			{
				entityPlayer.sendChatToPlayer(ChatMessageComponent.createFromText("Grenades are banned in this region."));
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
		return this.getUnlocalizedName() + "." + ExplosiveRegistry.get(itemstack.getItemDamage()).getUnlocalizedName();
	}

	@Override
	public String getUnlocalizedName()
	{
		return "icbm.grenade";
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IconRegister iconRegister)
	{
		for (int i = 0; i < ExplosiveRegistry.getAllZhaPin().size(); i++)
		{
			ICONS[i] = iconRegister.registerIcon(Reference.PREFIX + "grenade_" + ExplosiveRegistry.get(i).getUnlocalizedName());
		}
	}

	@Override
	public Icon getIconFromDamage(int i)
	{
		return ICONS[i];
	}

	@Override
	public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
	{
		for (Explosive zhaPin : ExplosiveRegistry.getAllZhaPin())
		{
			if (zhaPin.hasGrenadeForm())
			{
				par3List.add(new ItemStack(par1, 1, zhaPin.getID()));

			}
		}
	}
}
