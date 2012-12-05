package icbm;

import com.google.common.io.ByteArrayDataInput;

import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.INetworkManager;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagList;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.network.PacketManager;

public class TYinXin extends TileEntity implements IPacketReceiver
{
	// The block Id this block is trying to mimik
	private int fakeBlockID = 1;

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
			this.setFakeBlock(dataStream.readInt());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public int getFakeBlock()
	{
		return this.fakeBlockID;
	}

	public void setFakeBlock(int blockID)
	{
		fakeBlockID = Math.max(blockID, 1);

		if (!this.worldObj.isRemote)
		{
			PacketManager.sendPacketToClients(PacketManager.getPacket(ZhuYao.CHANNEL, this, this.fakeBlockID));
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.readFromNBT(par1NBTTagCompound);

		this.fakeBlockID = par1NBTTagCompound.getInteger("fakeBlockID");
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.writeToNBT(par1NBTTagCompound);

		par1NBTTagCompound.setInteger("fakeBlockID", this.fakeBlockID);
	}
}
