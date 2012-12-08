package icbm.dianqi;

import icbm.ZhuYao;
import icbm.daodan.EDaoDan;
import icbm.zhapin.ZhaPin;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
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
	private static final int YONG_DIAN_LIANG = 8000;

	public ItFaSheQi(int par1, int par2)
	{
		super(par1);
		this.iconIndex = par2;
		this.setItemName("launcher");
		this.setCreativeTab(ZhuYao.TAB);
		this.setTextureFile(ZhuYao.ITEM_TEXTURE_FILE);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player)
	{
		if (!world.isRemote)
		{
			if (this.getJoules(itemStack) > YONG_DIAN_LIANG)
			{
				// Check the player's inventory and look for missiles.
				for (int i = 0; i < player.inventory.getSizeInventory(); i++)
				{
					ItemStack inventoryStack = player.inventory.getStackInSlot(i);

					if (inventoryStack != null)
					{
						if (inventoryStack.itemID == ZhuYao.itDaoDan.shiftedIndex)
						{
							int daoDanHaoMa = inventoryStack.getItemDamage();

							// Limit the missile to tier two.
							if (daoDanHaoMa < ZhaPin.E_ER_ID)
							{
								ZhaPin zhaPin = ZhaPin.list[daoDanHaoMa];

								if (zhaPin != null)
								{
									double dist = 5000;
									Vector3 diDian = Vector3.add(Vector3.get(player), new Vector3(0, 0.5, 0));
									Vector3 kan = Vector3.get(player.getLook(1));
									Vector3 kaiShiDiDian = Vector3.add(diDian, Vector3.multiply(kan, 2));
									Vector3 muBiao = Vector3.add(diDian, Vector3.multiply(kan, 1000));
									EDaoDan eDaoDan = new EDaoDan(world, zhaPin.getID(), kaiShiDiDian, player.rotationYaw, player.rotationPitch);
									world.spawnEntityInWorld(eDaoDan);
									eDaoDan.faShe(muBiao);
									player.inventory.setInventorySlotContents(i, null);
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
	public double getVoltage()
	{
		return 20;
	}

	@Override
	public double getMaxJoules(Object... data)
	{
		return 80000;
	}
}
