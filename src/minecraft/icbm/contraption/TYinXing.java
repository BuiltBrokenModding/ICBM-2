package icbm.contraption;

import icbm.explosion.ZhuYao;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.network.PacketManager;

import com.google.common.io.ByteArrayDataInput;

public class TYinXing extends TileEntity implements IPacketReceiver
{
	// The block Id this block is trying to mimik
	private int jiaHaoMa = 0;
	private int jiaMetadata = 0;
	private boolean isYing = true;
	private final boolean[] qingBian = new boolean[] { false, false, false, false, false, false };

	@Override
	public boolean canUpdate()
	{
		return false;
	}

	@Override
	public void handlePacketData(INetworkManager network, int packetType, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput dataStream)
	{
		try
		{
			this.setFangGe(dataStream.readInt(), dataStream.readInt());
			this.qingBian[0] = dataStream.readBoolean();
			this.qingBian[1] = dataStream.readBoolean();
			this.qingBian[2] = dataStream.readBoolean();
			this.qingBian[3] = dataStream.readBoolean();
			this.qingBian[4] = dataStream.readBoolean();
			this.qingBian[5] = dataStream.readBoolean();
			this.isYing = dataStream.readBoolean();

			this.worldObj.markBlockForRenderUpdate(this.xCoord, this.yCoord, this.zCoord);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public Packet getDescriptionPacket()
	{
		return PacketManager.getPacket(ZhuYao.CHANNEL, this, this.jiaHaoMa, this.jiaMetadata, this.qingBian[0], this.qingBian[1], this.qingBian[2], this.qingBian[3], this.qingBian[4], this.qingBian[5], this.isYing);
	}

	public boolean getYing()
	{
		return this.isYing;
	}

	public void setYing(boolean isYing)
	{
		this.isYing = isYing;

		if (!this.worldObj.isRemote)
		{
			PacketManager.sendPacketToClients(this.getDescriptionPacket());
		}
	}

	public void setYing()
	{
		this.setYing(!this.isYing);
	}

	public int getJiaHaoMa()
	{
		return this.jiaHaoMa;
	}

	public int getJiaMetadata()
	{
		return this.jiaMetadata;
	}

	public void setFangGe(int blockID, int metadata)
	{
		if (this.jiaHaoMa != blockID || this.jiaMetadata != metadata)
		{
			this.jiaHaoMa = Math.max(blockID, 0);
			this.jiaMetadata = Math.max(metadata, 0);

			if (!this.worldObj.isRemote)
			{
				PacketManager.sendPacketToClients(this.getDescriptionPacket());
			}
		}
	}

	public boolean getQing(ForgeDirection direction)
	{
		if (direction.ordinal() < qingBian.length) { return qingBian[direction.ordinal()]; }

		return false;
	}

	public void setQing(ForgeDirection direction, boolean isQing)
	{
		if (direction.ordinal() < qingBian.length)
		{
			qingBian[direction.ordinal()] = isQing;

			if (!this.worldObj.isRemote)
			{
				PacketManager.sendPacketToClients(this.getDescriptionPacket());
			}
		}
	}

	public void setQing(ForgeDirection direction)
	{
		this.setQing(direction, !getQing(direction));
	}

	public void setQing(boolean isQing)
	{
		for (int i = 0; i < this.qingBian.length; i++)
		{
			this.setQing(ForgeDirection.getOrientation(i), isQing);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.readFromNBT(par1NBTTagCompound);

		this.jiaHaoMa = par1NBTTagCompound.getInteger("jiaHaoMa");
		this.jiaMetadata = par1NBTTagCompound.getInteger("jiaMetadata");

		for (int i = 0; i < qingBian.length; i++)
		{
			this.qingBian[i] = par1NBTTagCompound.getBoolean("qingBian" + i);
		}

		this.isYing = par1NBTTagCompound.getBoolean("isYing");
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.writeToNBT(par1NBTTagCompound);

		par1NBTTagCompound.setInteger("jiaHaoMa", this.jiaHaoMa);
		par1NBTTagCompound.setInteger("jiaMetadata", this.jiaMetadata);

		for (int i = 0; i < qingBian.length; i++)
		{
			par1NBTTagCompound.setBoolean("qingBian" + i, this.qingBian[i]);
		}

		par1NBTTagCompound.setBoolean("isYing", this.isYing);
	}
}
