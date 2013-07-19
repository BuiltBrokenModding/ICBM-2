package icbm.zhapin.dianqi;

import icbm.core.di.ItICBMElectricBase;
import icbm.zhapin.ZhaPinPacketGuanLi.ZhaPinPacketType;
import icbm.zhapin.ZhuYaoZhaPin;
import icbm.zhapin.jiqi.TFaSheQi;
import icbm.zhapin.jiqi.TFaSheShiMuo;
import icbm.zhapin.jiqi.TXiaoFaSheQi;

import java.util.List;

import mffs.api.card.ICoordLink;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.network.PacketManager;
import cpw.mods.fml.common.network.PacketDispatcher;

public class ItLeiDaQiang extends ItICBMElectricBase implements ICoordLink
{
	public static final int YONG_DIAN_LIANG = 1000;
	public static final int JU_LI = 1000;

	public ItLeiDaQiang(int id)
	{
		super(id, "radarGun");
	}

	/**
	 * Allows items to add custom lines of information to the mouseover description
	 */
	@Override
	public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List par3List, boolean par4)
	{
		super.addInformation(itemStack, entityPlayer, par3List, par4);
		Vector3 coord = getLink(itemStack);
		par3List.add("\uaa74Saved Coordinates:");
		par3List.add("X: " + (int) coord.x + ", Y: " + (int) coord.y + ", Z: " + (int) coord.z);
		par3List.add((int) new Vector3(entityPlayer).distanceTo(coord) + " Meters (" + (int) (new Vector3(entityPlayer).x - coord.x) + ", " + (int) (new Vector3(entityPlayer).y - coord.y) + ", " + (int) (new Vector3(entityPlayer).z - coord.z) + ")");
	}

	/**
	 * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack,
	 * world, entityPlayer
	 */
	@Override
	public ItemStack onItemRightClick(ItemStack itemStack, World par2World, EntityPlayer entityPlayer)
	{
		if (par2World.isRemote)
		{
			MovingObjectPosition objectMouseOver = entityPlayer.rayTrace(JU_LI, 1);

			if (objectMouseOver != null)
			{
				TileEntity tileEntity = par2World.getBlockTileEntity(objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ);

				// Do not scan if the target is a
				// missile launcher
				if (!(tileEntity instanceof TFaSheQi))
				{
					// Check for electricity
					if (this.getElectricityStored(itemStack) > YONG_DIAN_LIANG)
					{
						PacketDispatcher.sendPacketToServer(PacketManager.getPacketWithID(ZhuYaoZhaPin.CHANNEL, ZhaPinPacketType.RADAR_GUN.ordinal(), objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ));
						this.discharge(itemStack, YONG_DIAN_LIANG, true);
						entityPlayer.addChatMessage("Scanned Coordinates: X:" + objectMouseOver.blockX + ", Y:" + objectMouseOver.blockY + ", Z:" + objectMouseOver.blockZ + " - Distance: " + (int) Math.round(new Vector3(entityPlayer).distanceTo(new Vector3(objectMouseOver))));
					}
					else
					{
						entityPlayer.addChatMessage("Radar gun out of electricity!");
					}
				}
			}
		}

		return itemStack;
	}

	/**
	 * Callback for item usage. If the item does something special on right clicking, he will have
	 * one of those. Return True if something happen and false if it don't. This is for ITEMS, not
	 * BLOCKS !
	 */
	@Override
	public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int x, int y, int z, int par7, float par8, float par9, float par10)
	{
		int blockId = par3World.getBlockId(x, y, z);
		int blockMetadata = par3World.getBlockMetadata(x, y, z);

		if (blockId == ZhuYaoZhaPin.bJiQi.blockID)
		{
			TileEntity tileEntity = par3World.getBlockTileEntity(x, y, z);

			if (tileEntity != null)
			{
				if (tileEntity instanceof TFaSheShiMuo)
				{
					TFaSheShiMuo missileLauncher = (TFaSheShiMuo) tileEntity;

					Vector3 savedCords = this.getLink(par1ItemStack);

					// If the vector is NOT 0
					if (!savedCords.equals(new Vector3()))
					{
						if (missileLauncher.getTarget() == null)
						{
							missileLauncher.setTarget(new Vector3());
						}

						missileLauncher.getTarget().x = (int) savedCords.x;
						missileLauncher.getTarget().z = (int) savedCords.z;

						if (par3World.isRemote)
						{
							PacketDispatcher.sendPacketToServer(PacketManager.getPacket(ZhuYaoZhaPin.CHANNEL, missileLauncher, 2, savedCords.x, missileLauncher.getTarget().y, savedCords.z));
							par2EntityPlayer.addChatMessage("Coordinate information transfered!");
						}
					}
					else
					{
						if (par3World.isRemote)
							par2EntityPlayer.addChatMessage("You must scan a coordinate!");
					}
				}
				else if (tileEntity instanceof TXiaoFaSheQi)
				{
					TXiaoFaSheQi missileLauncher = (TXiaoFaSheQi) tileEntity;

					Vector3 savedCords = this.getLink(par1ItemStack);

					if (!savedCords.equals(new Vector3()))
					{
						if (missileLauncher.getTarget() == null)
						{
							missileLauncher.setTarget(new Vector3());
						}

						missileLauncher.setTarget(new Vector3(savedCords.x, savedCords.y, savedCords.z));

						if (par3World.isRemote)
						{
							PacketDispatcher.sendPacketToServer(PacketManager.getPacket(ZhuYaoZhaPin.CHANNEL, missileLauncher, 2, savedCords.x, savedCords.y, savedCords.z));
							par2EntityPlayer.addChatMessage("Coordinate information transfered!");
						}
					}
					else
					{
						if (par3World.isRemote)
						{
							par2EntityPlayer.addChatMessage("You must scan a coordinate!");
						}
					}
				}
			}
		}

		return false;
	}

	@Override
	public void setLink(ItemStack itemStack, Vector3 position)
	{
		// Saves the frequency in the ItemStack
		if (itemStack.getTagCompound() == null)
		{
			itemStack.setTagCompound(new NBTTagCompound());
		}

		position.writeToNBT(itemStack.getTagCompound());
	}

	@Override
	public Vector3 getLink(ItemStack itemStack)
	{
		if (itemStack.getTagCompound() == null)
		{
			itemStack.setTagCompound(new NBTTagCompound());
		}

		return Vector3.readFromNBT(itemStack.getTagCompound());
	}

	@Override
	public float getVoltage(ItemStack itemStack)
	{
		return 20;
	}

	@Override
	public float getMaxElectricityStored(ItemStack itemStack)
	{
		return 80000;
	}

}
