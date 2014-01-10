package icbm.explosion.items;

import icbm.core.ICBMCore;
import icbm.core.prefab.item.ItemICBMElectrical;
import icbm.explosion.ICBMExplosion;
import icbm.explosion.missile.Explosive;
import icbm.explosion.missile.TileExplosive;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import universalelectricity.api.vector.Vector3;
import calclavia.lib.network.IPacketReceiver;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.common.network.PacketDispatcher;

public class ItemRemoteDetonator extends ItemICBMElectrical implements IPacketReceiver
{
	public static final int BAN_JING = 100;
	public static final int ENERGY = 1500;

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

	/** Lock the remote to an explosive if it exists. */
	@Override
	public boolean onItemUse(ItemStack itemStack, EntityPlayer entityPlayer, World world, int x, int y, int z, int par7, float par8, float par9, float par10)
	{
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

		if (entityPlayer.isSneaking() && tileEntity != null)
		{
			if (this.nengZha(tileEntity))
			{
				// Check for electricity
				if (this.getEnergy(itemStack) > ENERGY)
				{
					this.setSavedCoords(itemStack, new Vector3(x, y, z));
					this.discharge(itemStack, ENERGY, true);
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
	public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player)
	{
		if (world.isRemote)
		{
			if (!player.isSneaking())
			{
				MovingObjectPosition objectMouseOver = player.rayTrace(BAN_JING, 1);

				if (objectMouseOver != null && objectMouseOver.typeOfHit == EnumMovingObjectType.TILE)
				{

					TileEntity tileEntity = world.getBlockTileEntity(objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ);
					int blockID = world.getBlockId(objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ);
					TileEntity tile = world.getBlockTileEntity(objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ);

					if (tile != null)
					{
						if (tile instanceof TileExplosive)
						{
							if (blockID == ICBMExplosion.blockMachine.blockID)
							{
								return itemStack;
							}
							else if (this.nengZha(tileEntity))
							{
								// Check for electricity
								if (this.getEnergy(itemStack) > ENERGY)
								{
									PacketDispatcher.sendPacketToServer(ICBMCore.PACKET_TILE.getPacket(tileEntity, (byte) 2));
									return itemStack;
								}
								else
								{
									player.addChatMessage("Remote out of electricity!");
								}
							}
						}
					}
				}
			}
			else
			{
				if (this.getEnergy(itemStack) > ENERGY)
				{
					TileEntity tileEntity = this.getSavedCoord(itemStack).getTileEntity(world);

					if (this.nengZha(tileEntity))
					{
						// Blow it up
						PacketDispatcher.sendPacketToServer(ICBMCore.PACKET_TILE.getPacket(tileEntity, (byte) 2));
						// Use Energy
						PacketDispatcher.sendPacketToServer(ICBMCore.PACKET_ITEM.getPacket(player));
					}
				}
				else
				{
					player.addChatMessage("Remote out of electricity!");
				}
			}
		}

		return itemStack;
	}

	public boolean nengZha(TileEntity tileEntity)
	{
		if (tileEntity != null)
		{
			if (tileEntity instanceof TileExplosive)
			{
				return ((TileExplosive) tileEntity).haoMa == Explosive.condensed.getID() || ((TileExplosive) tileEntity).haoMa == Explosive.breaching.getID() || ((TileExplosive) tileEntity).haoMa == Explosive.sMine.getID();
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
	public long getEnergyCapacity(ItemStack theItem)
	{
		return 50000000;
	}

	@Override
	public long getVoltage(ItemStack itemStack)
	{
		return 80;
	}

	@Override
	public void onReceivePacket(ByteArrayDataInput data, EntityPlayer player, Object... extra)
	{
		ItemStack itemStack = (ItemStack) extra[0];
		this.discharge(itemStack, ENERGY, true);
	}
}
