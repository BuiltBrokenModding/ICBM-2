package icbm;

import com.google.common.io.ByteArrayDataInput;

import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.INetworkManager;
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
}
