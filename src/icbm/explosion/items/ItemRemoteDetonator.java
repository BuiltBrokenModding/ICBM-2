package icbm.explosion.items;

import icbm.core.base.ItemICBMElectricBase;
import icbm.explosion.ICBMExplosion;
import icbm.explosion.ICBMPacketHandler.ZhaPinPacketType;
import icbm.explosion.missile.Explosive;
import icbm.explosion.missile.TileEntityExplosive;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.network.PacketManager;
import cpw.mods.fml.common.network.PacketDispatcher;

public class ItemRemoteDetonator extends ItemICBMElectricBase
{
	public static final int BAN_JING = 100;
	public static final int YONG_DIAN_LIANG = 1500;

	public ItemRemoteDetonator(int id)
	{
		super(id, "remoteDetonator");
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
				if (this.getElectricityStored(itemStack) > YONG_DIAN_LIANG)
				{
					this.setSavedCoords(itemStack, new Vector3(x, y, z));
					this.discharge(itemStack, YONG_DIAN_LIANG, true);
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
						if (tile instanceof TileEntityExplosive)
						{
							if (blockID == ICBMExplosion.blockMachine.blockID)
							{
								return itemStack;
							}
							else if (this.nengZha(tileEntity))
							{
								// Check for electricity
								if (this.getElectricityStored(itemStack) > YONG_DIAN_LIANG)
								{
									PacketDispatcher.sendPacketToServer(PacketManager.getPacket(ICBMExplosion.CHANNEL, tileEntity, (byte) 2));
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
				if (this.getElectricityStored(itemStack) > YONG_DIAN_LIANG)
				{
					TileEntity tileEntity = this.getSavedCoord(itemStack).getTileEntity(world);

					if (this.nengZha(tileEntity))
					{
						// Blow it up
						PacketDispatcher.sendPacketToServer(PacketManager.getPacket(ICBMExplosion.CHANNEL, tileEntity, (byte) 2));
						// Use Energy
						PacketDispatcher.sendPacketToServer(PacketManager.getPacketWithID(ICBMExplosion.CHANNEL, ZhaPinPacketType.REMOTE.ordinal()));
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
			if (tileEntity instanceof TileEntityExplosive)
			{
				return ((TileEntityExplosive) tileEntity).haoMa == Explosive.condensed.getID() || ((TileEntityExplosive) tileEntity).haoMa == Explosive.breaching.getID() || ((TileEntityExplosive) tileEntity).haoMa == Explosive.sMine.getID();
			}
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
		if (par1ItemStack.stackTagCompound == null)
		{
			return new Vector3();
		}

		return new Vector3(par1ItemStack.stackTagCompound.getInteger("x"), par1ItemStack.stackTagCompound.getInteger("y"), par1ItemStack.stackTagCompound.getInteger("z"));
	}

	@Override
	public float getVoltage(ItemStack itemStack)
	{
		return 20;
	}

	@Override
	public float getMaxElectricityStored(ItemStack itemStack)
	{
		return 50000;
	}
}
