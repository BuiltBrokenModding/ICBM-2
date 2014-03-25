package icbm.explosion.items;

import icbm.Settings;
import icbm.core.prefab.item.ItemICBMElectrical;
import icbm.explosion.entities.EntityMissile;
import icbm.explosion.explosive.ExplosiveRegistry;
import icbm.explosion.missile.types.Missile;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import universalelectricity.api.vector.Vector3;
import calclavia.api.icbm.explosion.ExplosiveType;
import calclavia.api.icbm.explosion.ExplosionEvent.ExplosivePreDetonationEvent;
import calclavia.lib.utility.LanguageUtility;

/**
 * Rocket Launcher
 * 
 * @author Calclavia
 */

public class ItemRocketLauncher extends ItemICBMElectrical
{
	private static final int ENERGY = 1000000;

	public ItemRocketLauncher(int par1)
	{
		super(par1, "rocketLauncher");
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
			if (this.getEnergy(itemStack) >= ENERGY)
			{
				// Check the player's inventory and look for missiles.
				for (int i = 0; i < player.inventory.getSizeInventory(); i++)
				{
					ItemStack inventoryStack = player.inventory.getStackInSlot(i);

					if (inventoryStack != null)
					{
						if (inventoryStack.getItem() instanceof ItemMissile)
						{
							int haoMa = inventoryStack.getItemDamage();

							if (ExplosiveRegistry.get(haoMa) instanceof Missile)
							{
								Missile daoDan = (Missile) ExplosiveRegistry.get(haoMa);

								ExplosivePreDetonationEvent evt = new ExplosivePreDetonationEvent(world, player.posX, player.posY, player.posZ, ExplosiveType.AIR, ExplosiveRegistry.get(haoMa));
								MinecraftForge.EVENT_BUS.post(evt);

								if (daoDan != null && !evt.isCanceled())
								{
									// Limit the missile to tier two.
									if (daoDan.getTier() <= Settings.MAX_ROCKET_LAUCNHER_TIER && daoDan.isCruise())
									{
										double dist = 5000;
										Vector3 diDian = Vector3.translate(new Vector3(player), new Vector3(0, 0.5, 0));
										Vector3 kan = new Vector3(player.getLook(1));
										Vector3 start = Vector3.translate(diDian, Vector3.scale(kan, 1.1));
										Vector3 muBiao = Vector3.translate(diDian, Vector3.scale(kan, 100));

										//TOD: Fix this rotation when we use the proper model loader.
										EntityMissile entityMissile = new EntityMissile(world, start, daoDan.getID(), -player.rotationYaw, -player.rotationPitch);
										world.spawnEntityInWorld(entityMissile);

										if (player.isSneaking())
										{
											player.mountEntity(entityMissile);
											player.setSneaking(false);
										}

										entityMissile.ignore(player);
										entityMissile.launch(muBiao);

										if (!player.capabilities.isCreativeMode)
										{
											player.inventory.setInventorySlotContents(i, null);
										}

										this.discharge(itemStack, ENERGY, true);

										return itemStack;
									}
								}
								else
								{
									player.sendChatToPlayer(ChatMessageComponent.createFromText(LanguageUtility.getLocal("message.launcher.protected")));
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
	public long getVoltage(ItemStack itemStack)
	{
		return 1000;
	}

	@Override
	public long getEnergyCapacity(ItemStack theItem)
	{
		return ENERGY * 16;
	}

	@Override
	public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List list, boolean par4)
	{
		String str = LanguageUtility.getLocal("info.rocketlauncher.tooltip").replaceAll("%s", String.valueOf(Settings.MAX_ROCKET_LAUCNHER_TIER));
		list.add(str);
	}
}
