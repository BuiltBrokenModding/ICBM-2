package icbm.zhapin.jiqi;

import icbm.api.RadarRegistry;
import icbm.core.ZhuYaoBase;
import icbm.zhapin.ZhuYaoZhaPin;
import icbm.zhapin.zhapin.ZhaPin;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.implement.IRedstoneReceptor;
import universalelectricity.prefab.multiblock.IMultiBlock;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.network.PacketManager;
import calclavia.lib.TileEntityUniversalStorable;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class TDianCiQi extends TileEntityUniversalStorable implements IPacketReceiver, IMultiBlock, IRedstoneReceptor
{
	// The maximum possible radius for the EMP to strike
	public static final int MAX_RADIUS = 150;

	public float xuanZhuan = 0;
	private float xuanZhuanLu, prevXuanZhuanLu = 0;

	// The EMP mode. 0 = All, 1 = Missiles Only, 2 = Electricity Only
	public byte muoShi = 0;

	// The EMP explosion radius
	public int banJing = 60;

	private final Set<EntityPlayer> yongZhe = new HashSet<EntityPlayer>();

	public TDianCiQi()
	{
		super();
		RadarRegistry.register(this);
	}

	@Override
	public void invalidate()
	{
		RadarRegistry.unregister(this);
		super.invalidate();
	}

	@Override
	public void updateEntity()
	{
		super.updateEntity();

		if (!this.isDisabled())
		{
			if (this.ticks % 20 == 0 && this.getJoules() > 0)
			{
				this.worldObj.playSoundEffect(this.xCoord, this.yCoord, this.zCoord, "icbm.machinehum", 0.5F, (float) (0.85F * this.getJoules() / this.getMaxJoules()));
			}

			this.xuanZhuanLu = (float) (Math.pow(this.getJoules() / this.getMaxJoules(), 2) * 0.5);
			this.xuanZhuan += xuanZhuanLu;
			if (this.xuanZhuan > 360)
				this.xuanZhuan = 0;
		}

		if (!this.worldObj.isRemote)
		{
			if (this.ticks % 3 == 0)
			{
				for (EntityPlayer wanJia : this.yongZhe)
				{
					PacketDispatcher.sendPacketToPlayer(this.getDescriptionPacket(), (Player) wanJia);
				}
			}

			if (this.ticks % 60 == 0 && this.prevXuanZhuanLu != this.xuanZhuanLu)
			{
				PacketManager.sendPacketToClients(this.getDescriptionPacket(), this.worldObj, new Vector3(this), 35);
			}
		}

		this.prevXuanZhuanLu = this.xuanZhuanLu;
	}

	@Override
	public void handlePacketData(INetworkManager network, int packetType, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput dataStream)
	{
		try
		{
			final int ID = dataStream.readInt();

			if (ID == -1)
			{
				if (dataStream.readBoolean())
				{
					PacketManager.sendPacketToClients(this.getDescriptionPacket(), this.worldObj, new Vector3(this), 15);
					this.yongZhe.add(player);
				}
				else
				{
					this.yongZhe.remove(player);
				}
			}
			else if (ID == 1)
			{
				this.setJoules(dataStream.readDouble());
				this.disabledTicks = dataStream.readInt();
				this.banJing = dataStream.readInt();
				this.muoShi = dataStream.readByte();
			}
			else if (ID == 2)
			{
				this.banJing = dataStream.readInt();
			}
			else if (ID == 3)
			{
				this.muoShi = dataStream.readByte();
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
		return PacketManager.getPacket(ZhuYaoZhaPin.CHANNEL, this, 1, this.getJoules(), this.disabledTicks, this.banJing, this.muoShi);
	}

	@Override
	public double getVoltage()
	{
		return 240;
	}

	/**
	 * Reads a tile entity from NBT.
	 */
	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.readFromNBT(par1NBTTagCompound);

		this.banJing = par1NBTTagCompound.getInteger("banJing");
		this.muoShi = par1NBTTagCompound.getByte("muoShi");
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.writeToNBT(par1NBTTagCompound);

		par1NBTTagCompound.setInteger("banJing", this.banJing);
		par1NBTTagCompound.setByte("muoShi", this.muoShi);
	}

	@Override
	public void onPowerOn()
	{
		if (this.getJoules() >= this.getMaxJoules())
		{
			if (this.muoShi == 0 || this.muoShi == 1)
			{
				ZhaPin.dianCiSignal.doBaoZha(worldObj, new Vector3(this.xCoord, this.yCoord, this.zCoord), null, this.banJing, -1);
			}

			if (this.muoShi == 0 || this.muoShi == 2)
			{
				ZhaPin.dianCiWave.doBaoZha(worldObj, new Vector3(this.xCoord, this.yCoord, this.zCoord), null, this.banJing, -1);
			}

			this.setJoules(0);
		}
	}

	@Override
	public void onPowerOff()
	{
	}

	@Override
	public void onDestroy(TileEntity callingBlock)
	{
		this.worldObj.setBlock(this.xCoord, this.yCoord, this.zCoord, 0, 0, 2);
		this.worldObj.setBlock(this.xCoord, this.yCoord + 1, this.zCoord, 0, 0, 2);
	}

	@Override
	public boolean onActivated(EntityPlayer entityPlayer)
	{
		entityPlayer.openGui(ZhuYaoZhaPin.instance, ZhuYaoBase.GUI_DIAN_CI_QI, this.worldObj, this.xCoord, this.yCoord, this.zCoord);
		return true;
	}

	@Override
	public void onCreate(Vector3 position)
	{
		ZhuYaoZhaPin.bJia.makeFakeBlock(this.worldObj, Vector3.add(position, new Vector3(0, 1, 0)), new Vector3(this));
	}

	@Override
	public double getMaxJoules()
	{
		return Math.max(2000000 * ((float) this.banJing / (float) MAX_RADIUS), 1000000);
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox()
	{
		return INFINITE_EXTENT_AABB;
	}
}
