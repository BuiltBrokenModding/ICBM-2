package icbm.jiqi;

import icbm.ICBMCommon;
import icbm.ZhuYao;
import icbm.daodan.DaoDanGuanLi;
import icbm.daodan.EDaoDan;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.src.ChunkCoordIntPair;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.INetworkManager;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.Packet;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.TileEntity;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeChunkManager.Type;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.UEConfig;
import universalelectricity.core.Vector2;
import universalelectricity.core.Vector3;
import universalelectricity.electricity.ElectricInfo;
import universalelectricity.implement.IRedstoneProvider;
import universalelectricity.prefab.TileEntityElectricityReceiver;
import universalelectricity.prefab.multiblock.IMultiBlock;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.network.PacketManager;

import com.google.common.io.ByteArrayDataInput;

import dan200.computer.api.IComputerAccess;
import dan200.computer.api.IPeripheral;

public class TLeiDaTai extends TileEntityElectricityReceiver implements IPacketReceiver, IRedstoneProvider, IMultiBlock, IPeripheral
{
	// Watts Per Tick
	public final static int YAO_DIAN = 15;

	public final static int MAX_BIAN_JING = 500;

	private static final boolean YIN_XIANG = UEConfig.getConfigData(ZhuYao.CONFIGURATION, "Radar Emit Sound", true);

	// The electricity stored
	public double dian, prevDian = 0;

	public float xuanZhuan = 0;

	public int alarmBanJing = 100;

	public int safetyBanJing = 20;

	public List<EDaoDan> detectedMissiles = new ArrayList<EDaoDan>();

	public List<TLeiDaTai> detectedRadarStations = new ArrayList<TLeiDaTai>();

	private boolean missileAlert = false;

	private int yongZhe = 0;

	private Ticket ticket;

	public TLeiDaTai()
	{
		super();
		LeiDaGuanLi.addRadarStation(this);
	}

	/**
	 * Called every tick. Super this!
	 */
	@Override
	public void onReceive(TileEntity sender, double amps, double voltage, ForgeDirection side)
	{
		if (!this.isDisabled())
		{
			this.dian += ElectricInfo.getWatts(amps, voltage);
		}
	}

	@Override
	public void initiate()
	{
		if (this.worldObj != null)
		{
			this.worldObj.notifyBlocksOfNeighborChange((int) this.xCoord, (int) this.yCoord, (int) this.zCoord, this.getBlockType().blockID);
		}

		if (this.ticket == null)
		{
			this.ticket = ForgeChunkManager.requestTicket(ZhuYao.instance, this.worldObj, Type.NORMAL);
			this.ticket.getModData();
		}

		ForgeChunkManager.forceChunk(this.ticket, new ChunkCoordIntPair(this.xCoord >> 4, this.zCoord >> 4));
	}

	public void updateEntity()
	{
		super.updateEntity();

		this.prevDian = this.dian;

		if (!this.worldObj.isRemote)
		{
			if (this.ticks % 40 == 0)
			{
				PacketManager.sendPacketToClients(this.getDescriptionPacket(), this.worldObj, Vector3.get(this), 35);
			}
			else if (this.ticks % 3 == 0 && this.yongZhe > 0)
			{
				PacketManager.sendPacketToClients(this.getDescriptionPacket2(), this.worldObj, Vector3.get(this), 12);
			}
		}

		if (!this.isDisabled())
		{
			if (this.dian >= this.YAO_DIAN)
			{
				this.xuanZhuan += 0.05F;

				if (this.xuanZhuan > 360)
					this.xuanZhuan = 0;

				if (!this.worldObj.isRemote)
				{
					this.dian -= this.YAO_DIAN;
				}

				// Do a radar scan
				boolean previousMissileDetection = this.detectedMissiles.size() > 0;
				this.missileAlert = false;
				this.detectedMissiles.clear();
				this.detectedRadarStations.clear();

				List<EDaoDan> missilesNearby = DaoDanGuanLi.getMissileInArea(new Vector2(this.xCoord - MAX_BIAN_JING, this.zCoord - MAX_BIAN_JING), new Vector2(this.xCoord + MAX_BIAN_JING, this.zCoord + MAX_BIAN_JING));

				for (EDaoDan missile : missilesNearby)
				{
					if (missile.ticksInAir > -1)
					{
						if (!this.detectedMissiles.contains(missile))
						{
							this.detectedMissiles.add(missile);
						}

						if (Vector2.distance(missile.muBiao.toVector2(), new Vector2(this.xCoord, this.zCoord)) < this.safetyBanJing)
						{
							this.missileAlert = true;
						}
					}
				}

				for (TLeiDaTai radarStation : LeiDaGuanLi.getRadarStationsInArea(new Vector2(this.xCoord - this.MAX_BIAN_JING, this.zCoord - this.MAX_BIAN_JING), new Vector2(this.xCoord + this.MAX_BIAN_JING, this.zCoord + this.MAX_BIAN_JING)))
				{
					if (!radarStation.isDisabled() && radarStation.prevDian > 0)
					{
						this.detectedRadarStations.add(radarStation);
					}
				}

				if (previousMissileDetection != this.detectedMissiles.size() > 0)
				{
					this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.getBlockType().blockID);
				}

				if (this.ticks % 20 == 0)
				{
					if (this.missileAlert && YIN_XIANG)
					{
						this.worldObj.playSoundEffect(this.xCoord, this.yCoord, this.zCoord, "icbm.alarm", 4F, 1F);
					}
				}
			}
			else
			{
				if (this.detectedMissiles.size() > 0)
				{
					this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.getBlockType().blockID);
				}

				this.detectedMissiles.clear();
				this.detectedRadarStations.clear();

				this.dian = 0;
			}
		}

		if (this.ticks % 40 == 0)
		{
			this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.getBlockType().blockID);
		}
	}

	private Packet getDescriptionPacket2()
	{
		return PacketManager.getPacket(ZhuYao.CHANNEL, this, (int) 1, this.alarmBanJing, this.safetyBanJing);
	}

	@Override
	public Packet getDescriptionPacket()
	{
		double sendDian = this.dian;

		if (sendDian > 0)
		{
			sendDian = this.YAO_DIAN;
		}

		return PacketManager.getPacket(ZhuYao.CHANNEL, this, (int) 4, sendDian, this.disabledTicks);
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
					PacketManager.sendPacketToClients(this.getDescriptionPacket2(), this.worldObj, Vector3.get(this), 15);
					this.yongZhe++;
				}
				else
				{
					this.yongZhe--;
				}
			}
			else if (this.worldObj.isRemote)
			{
				if (ID == 1)
				{
					this.alarmBanJing = dataStream.readInt();
					this.safetyBanJing = dataStream.readInt();
				}
				else if (ID == 4)
				{
					this.dian = dataStream.readDouble();
					this.disabledTicks = dataStream.readInt();
				}
			}
			else if (!this.worldObj.isRemote)
			{
				if (ID == 2)
				{
					this.safetyBanJing = dataStream.readInt();
				}
				else if (ID == 3)
				{
					this.alarmBanJing = dataStream.readInt();
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public double wattRequest()
	{
		if (!this.isDisabled()) { return this.YAO_DIAN; }

		return 0;
	}

	@Override
	public boolean canReceiveFromSide(ForgeDirection side)
	{
		return true;
	}

	@Override
	public double getVoltage()
	{
		return 120;
	}

	@Override
	public boolean isPoweringTo(byte side)
	{
		return this.detectedMissiles.size() > 0 && this.missileAlert;
	}

	@Override
	public boolean isIndirectlyPoweringTo(byte side)
	{
		return this.detectedMissiles.size() > 0 && this.missileAlert;
	}

	/**
	 * Reads a tile entity from NBT.
	 */
	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.readFromNBT(par1NBTTagCompound);

		this.safetyBanJing = par1NBTTagCompound.getInteger("safetyRadius");
		this.alarmBanJing = par1NBTTagCompound.getInteger("alarmRadius");
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.writeToNBT(par1NBTTagCompound);

		par1NBTTagCompound.setInteger("safetyRadius", this.safetyBanJing);
		par1NBTTagCompound.setInteger("alarmRadius", this.alarmBanJing);
	}

	@Override
	public void onDestroy(TileEntity callingBlock)
	{
		this.worldObj.setBlockWithNotify(this.xCoord, this.yCoord, this.zCoord, 0);

		// Top 3x3
		this.worldObj.setBlockWithNotify(this.xCoord, this.yCoord + 1, this.zCoord, 0);

		this.worldObj.setBlockWithNotify(this.xCoord + 1, this.yCoord + 1, this.zCoord, 0);
		this.worldObj.setBlockWithNotify(this.xCoord - 1, this.yCoord + 1, this.zCoord, 0);

		this.worldObj.setBlockWithNotify(this.xCoord, this.yCoord + 1, this.zCoord + 1, 0);
		this.worldObj.setBlockWithNotify(this.xCoord, this.yCoord + 1, this.zCoord - 1, 0);

		this.worldObj.setBlockWithNotify(this.xCoord + 1, this.yCoord + 1, this.zCoord + 1, 0);
		this.worldObj.setBlockWithNotify(this.xCoord - 1, this.yCoord + 1, this.zCoord - 1, 0);
		this.worldObj.setBlockWithNotify(this.xCoord + 1, this.yCoord + 1, this.zCoord - 1, 0);
		this.worldObj.setBlockWithNotify(this.xCoord - 1, this.yCoord + 1, this.zCoord + 1, 0);
	}

	@Override
	public boolean onActivated(EntityPlayer entityPlayer)
	{
		entityPlayer.openGui(ZhuYao.instance, ICBMCommon.GUI_RADAR_STATION, this.worldObj, this.xCoord, this.yCoord, this.zCoord);
		return true;
	}

	@Override
	public void onCreate(Vector3 position)
	{
		ZhuYao.bJia.makeFakeBlock(worldObj, Vector3.add(new Vector3(0, 1, 0), position), Vector3.get(this));

		ZhuYao.bJia.makeFakeBlock(worldObj, Vector3.add(new Vector3(1, 1, 0), position), Vector3.get(this));
		ZhuYao.bJia.makeFakeBlock(worldObj, Vector3.add(new Vector3(-1, 1, 0), position), Vector3.get(this));

		ZhuYao.bJia.makeFakeBlock(worldObj, Vector3.add(new Vector3(0, 1, 1), position), Vector3.get(this));
		ZhuYao.bJia.makeFakeBlock(worldObj, Vector3.add(new Vector3(0, 1, -1), position), Vector3.get(this));

		ZhuYao.bJia.makeFakeBlock(worldObj, Vector3.add(new Vector3(1, 1, -1), position), Vector3.get(this));
		ZhuYao.bJia.makeFakeBlock(worldObj, Vector3.add(new Vector3(-1, 1, 1), position), Vector3.get(this));

		ZhuYao.bJia.makeFakeBlock(worldObj, Vector3.add(new Vector3(1, 1, 1), position), Vector3.get(this));
		ZhuYao.bJia.makeFakeBlock(worldObj, Vector3.add(new Vector3(-1, 1, -1), position), Vector3.get(this));

	}

	@Override
	public String getType()
	{
		return "ICBMRadar";
	}

	@Override
	public String[] getMethodNames()
	{
		return new String[]
		{ "getMissiles", "getRadars" };
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, int method, Object[] arguments) throws Exception
	{
		if (this.prevDian < this.YAO_DIAN) { throw new Exception("Radar has insufficient electricity!"); }

		List<Double> returnArray;

		switch (method)
		{
			case 0:
				List<EDaoDan> daoDans = DaoDanGuanLi.getMissileInArea(new Vector2(this.xCoord - MAX_BIAN_JING, this.zCoord - MAX_BIAN_JING), new Vector2(this.xCoord + MAX_BIAN_JING, this.zCoord + MAX_BIAN_JING));
				returnArray = new ArrayList<Double>();

				for (EDaoDan daoDan : daoDans)
				{
					returnArray.add(daoDan.posX);
					returnArray.add(daoDan.posY);
					returnArray.add(daoDan.posZ);
				}

				return returnArray.toArray();
			case 1:
				returnArray = new ArrayList<Double>();

				for (TLeiDaTai radarStation : LeiDaGuanLi.getRadarStationsInArea(new Vector2(this.xCoord - this.MAX_BIAN_JING, this.zCoord - this.MAX_BIAN_JING), new Vector2(this.xCoord + this.MAX_BIAN_JING, this.zCoord + this.MAX_BIAN_JING)))
				{
					if (!radarStation.isDisabled() && radarStation.prevDian > 0)
					{
						returnArray.add((double) radarStation.xCoord);
						returnArray.add((double) radarStation.yCoord);
						returnArray.add((double) radarStation.zCoord);
					}
				}

				return returnArray.toArray();
		}

		throw new Exception("Invalid ICBM Radar Function.");
	}

	@Override
	public void invalidate()
	{
		ForgeChunkManager.releaseTicket(ticket);
		super.invalidate();
	}

	@Override
	public boolean canAttachToSide(int side)
	{
		return true;
	}

	@Override
	public void attach(IComputerAccess computer, String computerSide)
	{
	}

	@Override
	public void detach(IComputerAccess computer)
	{
	}
}
