package icbm.zhapin.dianqi;

import icbm.api.ICBMTab;
import icbm.core.ItIC2ElectricItem;
import icbm.core.ZhuYao;
import icbm.zhapin.ZhuYaoZhaPin;
import icbm.zhapin.daodan.EDaoDan;
import icbm.zhapin.zhapin.ZhaPin;
import icbm.zhapin.zhapin.ZhaPin.ZhaPinType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;

/**
 * Rocket Launcher
 * 
 * @author Calclavia
 * 
 */

public class ItFaSheQi extends ItIC2ElectricItem
{
	private static final int YONG_DIAN_LIANG = 5000;

	public ItFaSheQi(int par1)
	{
		super(par1);
		this.setItemName("launcher");
		this.setCreativeTab(ICBMTab.INSTANCE);
		this.setTextureFile(ZhuYao.ITEM_TEXTURE_FILE);
	}

	@Override
	public EnumAction getItemUseAction(ItemStack par1ItemStack)
	{
		return EnumAction.bow;
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
						if (inventoryStack.itemID == ZhuYaoZhaPin.itDaoDan.itemID)
						{
							int haoMa = inventoryStack.getItemDamage();

							if (!ZhuYaoZhaPin.shiBaoHu(world, new Vector3(player), ZhaPinType.DAO_DAN, haoMa))
							{
								// Limit the missile to tier two.
								if (haoMa < ZhaPin.E_ER_ID)
								{
									ZhaPin zhaPin = ZhaPin.list[haoMa];

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
							else
							{
								player.sendChatToPlayer("Region being is protected.");
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
