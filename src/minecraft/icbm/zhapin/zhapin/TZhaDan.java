package icbm.zhapin.zhapin;

import icbm.api.explosion.IExplosive;
import icbm.api.explosion.IExplosiveContainer;
import icbm.zhapin.ZhuYaoZhaPin;
import icbm.zhapin.dianqi.ItYaoKong;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.electricity.ElectricityPack;
import universalelectricity.core.item.ItemElectric;
import universalelectricity.prefab.implement.IRotatable;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.network.PacketManager;

import com.google.common.io.ByteArrayDataInput;

public class TZhaDan extends TileEntity implements IExplosiveContainer, IPacketReceiver, IRotatable
{
	public boolean exploding = false;
	public int haoMa = 0;

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
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.writeToNBT(par1NBTTagCompound);

		par1NBTTagCompound.setInteger("explosiveID", this.haoMa);
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
				if (player.inventory.getCurrentItem().getItem() instanceof ItYaoKong)
				{
					ItemStack itemStack = player.inventory.getCurrentItem();
					BZhaDan.yinZha(this.worldObj, this.xCoord, this.yCoord, this.zCoord, this.haoMa, 0);
					((ItYaoKong) ZhuYaoZhaPin.itYaoKong).onProvide(ElectricityPack.getFromWatts(ItYaoKong.YONG_DIAN_LIANG, ((ItemElectric) ZhuYaoZhaPin.itYaoKong).getVoltage(itemStack)), itemStack);
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
		return PacketManager.getPacket(ZhuYaoZhaPin.CHANNEL, this, (byte) 1, this.haoMa);
	}

	@Override
	public ForgeDirection getDirection(IBlockAccess world, int x, int y, int z)
	{
		return ForgeDirection.getOrientation(this.getBlockMetadata());
	}

	@Override
	public void setDirection(World world, int x, int y, int z, ForgeDirection facingDirection)
	{
		this.worldObj.setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, facingDirection.ordinal(), 2);
	}

	@Override
	public IExplosive getExplosiveType()
	{
		return ZhaPin.list[this.haoMa];
	}
}
