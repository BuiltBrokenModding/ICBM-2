package icbm.zhapin;

import icbm.ZhuYao;
import icbm.dianqi.ItYaoKong;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.INetworkManager;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.Packet;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.TileEntity;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.network.PacketManager;

import com.google.common.io.ByteArrayDataInput;

public class TZhaDan extends TileEntity implements IPacketReceiver
{
	public int explosiveID = 0;

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

		this.explosiveID = par1NBTTagCompound.getInteger("explosiveID");
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.writeToNBT(par1NBTTagCompound);

		par1NBTTagCompound.setInteger("explosiveID", this.explosiveID);
	}

	@Override
	public void handlePacketData(INetworkManager network, int packetType, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput dataStream)
	{
		try
		{
			final int ID = dataStream.readByte();

			if (ID == 1)
			{
				this.explosiveID = dataStream.readInt();
			}
			else if (ID == 2 && !this.worldObj.isRemote)
			{
				// Packet explode command
				if (player.inventory.getCurrentItem().getItem() instanceof ItYaoKong)
				{
					ItemStack itemStack = player.inventory.getCurrentItem();
					BZhaDan.detonateTNT(this.worldObj, this.xCoord, this.yCoord, this.zCoord, this.explosiveID, 0);
					((ItYaoKong) ZhuYao.itYaoKong).onUse(ItYaoKong.YONG_DIAN_LIANG, itemStack);
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
		return PacketManager.getPacket(ZhuYao.CHANNEL, this, (byte) 1, this.explosiveID);
	}
}
