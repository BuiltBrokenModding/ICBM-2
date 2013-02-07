package icbm.common.dianqi;

import icbm.common.ZhuYao;
import icbm.common.daodan.EDaoDan;
import icbm.common.zhapin.ZhaPin;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.ItemElectric;

/**
 * Rocket Launcher
 * 
 * @author Calclavia
 * 
 */

public class ItFaSheQi extends ItemElectric
{
	private static final int YONG_DIAN_LIANG = 5000;

	public ItFaSheQi(int par1)
	{
		super(par1);
		this.setItemName("launcher");
		this.setCreativeTab(ZhuYao.TAB);
		this.setTextureFile(ZhuYao.ITEM_TEXTURE_FILE);
	}

	@Override
	public EnumAction getItemUseAction(ItemStack par1ItemStack)
	{
		return EnumAction.bow;
	}

	@Override
	public void onUpdate(ItemStack par1ItemStack, World par2World, Entity par3Entity, int par4, boolean par5)
	{
		super.onUpdate(par1ItemStack, par2World, par3Entity, par4, par5);
		/*
		 * if (par3Entity instanceof EntityPlayer) { if (((EntityPlayer)
		 * par3Entity).getCurrentEquippedItem() == par1ItemStack) { ((EntityPlayer)
		 * par3Entity).clearItemInUse(); ((EntityPlayer) par3Entity).setItemInUse(par1ItemStack, 5);
		 * } }
		 */
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player)
	{
		if (!world.isRemote)
		{
			if (this.getJoules(itemStack) >= YONG_DIAN_LIANG)
			{
				// Check the player's inventory and look for missiles.
				for (int i = 0; i < player.inventory.getSizeInventory(); i++)
				{
					ItemStack inventoryStack = player.inventory.getStackInSlot(i);

					if (inventoryStack != null)
					{
						if (inventoryStack.itemID == ZhuYao.itDaoDan.itemID)
						{
							int daoDanHaoMa = inventoryStack.getItemDamage();

							// Limit the missile to tier two.
							if (daoDanHaoMa < ZhaPin.E_ER_ID)
							{
								ZhaPin zhaPin = ZhaPin.list[daoDanHaoMa];

								if (zhaPin != null)
								{
									double dist = 5000;
									Vector3 diDian = Vector3.add(new Vector3(player), new Vector3(0, 0.5, 0));
									Vector3 kan = new Vector3(player.getLook(1));
									Vector3 kaiShiDiDian = Vector3.add(diDian, Vector3.multiply(kan, 2));
									Vector3 muBiao = Vector3.add(diDian, Vector3.multiply(kan, 100));

									EDaoDan eDaoDan = new EDaoDan(world, zhaPin.getID(), kaiShiDiDian, player.rotationYaw, player.rotationPitch);
									world.spawnEntityInWorld(eDaoDan);
									eDaoDan.faShe(muBiao);

									if (!player.capabilities.isCreativeMode)
									{
										player.inventory.setInventorySlotContents(i, null);
									}

									this.onUse(YONG_DIAN_LIANG, itemStack);

									return itemStack;
								}
							}
						}
					}
				}
			}
		}

		return itemStack;
	}

	@Override
	public double getVoltage(Object... data)
	{
		return 25;
	}

	@Override
	public double getMaxJoules(Object... data)
	{
		return 100000;
	}
}
