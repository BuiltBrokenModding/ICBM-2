package icbm.explosion.zhapin;

import icbm.api.explosion.IExplosive;
import icbm.api.explosion.IExplosiveContainer;
import icbm.explosion.ICBMExplosion;
import icbm.explosion.items.ItemRemoteDetonator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.network.PacketManager;
import universalelectricity.prefab.tile.IRotatable;

import com.google.common.io.ByteArrayDataInput;

public class TileEntityExplosive extends TileEntity implements IExplosiveContainer, IPacketReceiver, IRotatable
{
	public boolean exploding = false;
	public int haoMa = 0;
	public NBTTagCompound nbtData = new NBTTagCompound();

	@Override
	public boolean canUpdate()
	{
		return false;
	}

	/**
	 * Reads a tile entity from NBT.
	 */
	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.readFromNBT(par1NBTTagCompound);
		this.haoMa = par1NBTTagCompound.getInteger("explosiveID");
		this.nbtData = par1NBTTagCompound.getCompoundTag("data");
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.writeToNBT(par1NBTTagCompound);
		par1NBTTagCompound.setInteger("explosiveID", this.haoMa);
		par1NBTTagCompound.setTag("data", this.nbtData);
	}

	@Override
	public void handlePacketData(INetworkManager network, int packetType, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput dataStream)
	{
		try
		{
			final byte ID = dataStream.readByte();

			if (ID == 1)
			{
				this.haoMa = dataStream.readInt();
			}
			else if (ID == 2 && !this.worldObj.isRemote)
			{
				// Packet explode command
				if (player.inventory.getCurrentItem().getItem() instanceof ItemRemoteDetonator)
				{
					ItemStack itemStack = player.inventory.getCurrentItem();
					BlockExplosive.yinZha(this.worldObj, this.xCoord, this.yCoord, this.zCoord, this.haoMa, 0);
					((ItemRemoteDetonator) ICBMExplosion.itYaoKong).discharge(itemStack, ItemRemoteDetonator.YONG_DIAN_LIANG, true);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public Packet getDescriptionPacket()
	{
		return PacketManager.getPacket(ICBMExplosion.CHANNEL, this, (byte) 1, this.haoMa);
	}

	@Override
	public ForgeDirection getDirection()
	{
		return ForgeDirection.getOrientation(this.getBlockMetadata());
	}

	@Override
	public void setDirection(ForgeDirection facingDirection)
	{
		this.worldObj.setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, facingDirection.ordinal(), 2);
	}

	@Override
	public IExplosive getExplosiveType()
	{
		return ExplosiveRegistry.get(this.haoMa);
	}

	@Override
	public NBTTagCompound getTagCompound()
	{
		return this.nbtData;
	}
}
