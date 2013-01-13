package icbm.common.dianqi;

import icbm.common.ICBMPacketManager.ICBMPacketType;
import icbm.common.ZhuYao;
import icbm.common.zhapin.TZhaDan;
import icbm.common.zhapin.ZhaPin;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.ItemElectric;
import universalelectricity.prefab.network.PacketManager;
import cpw.mods.fml.common.network.PacketDispatcher;

public class ItYaoKong extends ItemElectric
{
	public static final int BAN_JING = 100;
	public static final int YONG_DIAN_LIANG = 2000;

	public ItYaoKong(int par1, int par2)
	{
		super(par1);
		this.iconIndex = par2;
		this.setItemName("remoteDetonator");
		this.setCreativeTab(ZhuYao.TAB);
	}

	@Override
	public void addInformation(ItemStack itemStack, EntityPlayer player, List par3List, boolean par4)
	{
		super.addInformation(itemStack, player, par3List, par4);
		Vector3 coord = getSavedCoord(itemStack);

		if (this.nengZha(coord.getTileEntity(player.worldObj)))
		{
			par3List.add("\uaa74Linked Explosive:");
			par3List.add("X: " + (int) coord.x + ", Y: " + (int) coord.y + ", Z: " + (int) coord.z);
		}
		else
		{
			par3List.add("\u00a74No Linked Explosive");
		}
	}

	@Override
	public String getTextureFile()
	{
		return ZhuYao.ITEM_TEXTURE_FILE;
	}

	/**
	 * Lock the remote to an explosive if it exists.
	 */
	@Override
	public boolean onItemUse(ItemStack itemStack, EntityPlayer entityPlayer, World world, int x, int y, int z, int par7, float par8, float par9, float par10)
	{
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

		if (entityPlayer.isSneaking() && tileEntity != null)
		{
			if (this.nengZha(tileEntity))
			{
				// Check for electricity
				if (this.getJoules(itemStack) > YONG_DIAN_LIANG)
				{

					this.setSavedCoords(itemStack, new Vector3(x, y, z));
					this.onUse(YONG_DIAN_LIANG, itemStack);
					if (world.isRemote)
					{
						entityPlayer.addChatMessage("Remote Locked to: X:" + x + ", Y:" + y + ", Z:" + z);
					}
				}
				else if (world.isRemote)
				{
					entityPlayer.addChatMessage("Remote out of electricity!");
				}

				return true;
			}
		}

		return false;
	}

	/**
	 * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack,
	 * world, entityPlayer
	 */
	@Override
	public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityPlayer)
	{
		if (world.isRemote)
		{
			if (!entityPlayer.isSneaking())
			{
				MovingObjectPosition objectMouseOver = entityPlayer.rayTrace(BAN_JING, 1);

				if (objectMouseOver != null && objectMouseOver.typeOfHit == EnumMovingObjectType.TILE)
				{

					TileEntity tileEntity = world.getBlockTileEntity(objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ);
					int blockID = world.getBlockId(objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ);
					TileEntity tile = world.getBlockTileEntity(objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ);

					if (tile != null)
					{
						if (tile instanceof TZhaDan)
						{
							int explosiveID = ((TZhaDan) tile).haoMa;

							if (blockID == ZhuYao.bJiQi.blockID)
							{
								return itemStack;
							}
							else if (this.nengZha(tileEntity))
							{
								// Check for electricity
								if (this.getJoules(itemStack) > YONG_DIAN_LIANG)
								{
									PacketDispatcher.sendPacketToServer(PacketManager.getPacket(ZhuYao.CHANNEL, tileEntity, (byte) 2));
									return itemStack;
								}
								else
								{
									entityPlayer.addChatMessage("Remote out of electricity!");
								}
							}
						}
					}
				}
			}
			else
			{
				if (this.getJoules(itemStack) > YONG_DIAN_LIANG)
				{
					TileEntity tileEntity = this.getSavedCoord(itemStack).getTileEntity(world);

					if (this.nengZha(tileEntity))
					{
						// Blow it up
						PacketDispatcher.sendPacketToServer(PacketManager.getPacket(ZhuYao.CHANNEL, tileEntity, (byte) 2));
						// Use Energy
						PacketDispatcher.sendPacketToServer(PacketManager.getPacketWithID(ZhuYao.CHANNEL, (int) ICBMPacketType.REMOTE.ordinal()));
					}
				}
				else
				{
					entityPlayer.addChatMessage("Remote out of electricity!");
				}
			}
		}

		return itemStack;
	}

	public boolean nengZha(TileEntity tileEntity)
	{
		if (tileEntity != null)
		{
			if (tileEntity instanceof TZhaDan) { return ((TZhaDan) tileEntity).haoMa == ZhaPin.yaSuo.getID() || ((TZhaDan) tileEntity).haoMa == ZhaPin.tuPuo.getID(); }
		}

		return false;
	}

	public void setSavedCoords(ItemStack itemStack, Vector3 position)
	{
		if (itemStack.stackTagCompound == null)
		{
			itemStack.setTagCompound(new NBTTagCompound());
		}

		itemStack.stackTagCompound.setInteger("x", position.intX());
		itemStack.stackTagCompound.setInteger("y", position.intY());
		itemStack.stackTagCompound.setInteger("z", position.intZ());
	}

	public Vector3 getSavedCoord(ItemStack par1ItemStack)
	{
		if (par1ItemStack.stackTagCompound == null) { return new Vector3(); }

		return new Vector3(par1ItemStack.stackTagCompound.getInteger("x"), par1ItemStack.stackTagCompound.getInteger("y"), par1ItemStack.stackTagCompound.getInteger("z"));
	}

	@Override
	public double getVoltage(Object... data)
	{
		return 20;
	}

	@Override
	public double getMaxJoules(Object... data)
	{
		return 40000;
	}
}
