package icbm.common.jiqi;

import icbm.api.RadarRegistry;
import icbm.common.CommonProxy;
import icbm.common.ZhuYao;
import icbm.common.daodan.EDaoDan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeChunkManager.Type;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.electricity.ElectricityPack;
import universalelectricity.core.vector.Vector2;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.implement.IRedstoneProvider;
import universalelectricity.prefab.implement.IToolConfigurator;
import universalelectricity.prefab.multiblock.IMultiBlock;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.network.PacketManager;
import universalelectricity.prefab.tile.TileEntityElectricityRunnable;

import com.google.common.io.ByteArrayDataInput;

import dan200.computer.api.IComputerAccess;
import dan200.computer.api.IPeripheral;

public class TLeiDaTai extends TileEntityElectricityRunnable implements IPacketReceiver, IRedstoneProvider, IMultiBlock, IPeripheral
{
	public final static int MAX_BIAN_JING = 500;

	public float xuanZhuan = 0;

	public int alarmBanJing = 100;

	public int safetyBanJing = 50;

	private List<EDaoDan> weiXianDaoDan = new ArrayList<EDaoDan>();

	public List<Entity> xunZhaoEntity = new ArrayList<Entity>();

	public List<TileEntity> xunZhaoJiQi = new ArrayList<TileEntity>();

	private int yongZhe = 0;

	public boolean emitAll = false;

	private Ticket ticket;

	public TLeiDaTai()
	{
		super();
		RadarRegistry.register(this);
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

			if (this.ticket != null)
			{
				this.ticket.getModData();
			}
		}

		ForgeChunkManager.forceChunk(this.ticket, new ChunkCoordIntPair(this.xCoord >> 4, this.zCoord >> 4));
	}

	@Override
	public void updateEntity()
	{
		super.updateEntity();

		try
		{
			if (!this.worldObj.isRemote)
			{
				if (this.ticks % 40 == 0)
				{
					PacketManager.sendPacketToClients(this.getDescriptionPacket(), this.worldObj, new Vector3(this), 35);
				}
				else if (this.ticks % 3 == 0 && this.yongZhe > 0)
				{
					PacketManager.sendPacketToClients(this.getDescriptionPacket2(), this.worldObj, new Vector3(this), 12);
				}
			}

			if (!this.isDisabled())
			{
				if (this.wattsReceived >= this.getRequest().getWatts())
				{
					this.xuanZhuan += 0.05F;

					if (this.xuanZhuan > 360)
						this.xuanZhuan = 0;

					if (!this.worldObj.isRemote)
					{
						this.wattsReceived -= this.getRequest().getWatts() / 2;
					}

					int prevShuMu = this.xunZhaoEntity.size();

					// Do a radar scan
					this.doScan();

					if (prevShuMu != this.xunZhaoEntity.size())
					{
						this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.getBlockType().blockID);
					}
				}
				else
				{
					if (this.xunZhaoEntity.size() > 0)
					{
						this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.getBlockType().blockID);
					}

					this.xunZhaoEntity.clear();
					this.xunZhaoJiQi.clear();
				}
			}

			if (this.ticks % 40 == 0)
			{
				this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.getBlockType().blockID);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void doScan()
	{
		this.weiXianDaoDan.clear();
		this.xunZhaoEntity.clear();
		this.xunZhaoJiQi.clear();

		List<Entity> entities = RadarRegistry.getEntitiesWithinRadius(new Vector3(this).toVector2(), MAX_BIAN_JING);

		for (Entity entity : entities)
		{
			if (entity instanceof EDaoDan)
			{
				if (((EDaoDan) entity).feiXingTick > -1)
				{
					if (!this.xunZhaoEntity.contains(entity))
					{
						this.xunZhaoEntity.add(entity);
					}

					if (this.isWeiXianDaoDan((EDaoDan) entity))
					{
						weiXianDaoDan.add((EDaoDan) entity);
					}
				}
			}
			else
			{
				this.xunZhaoEntity.add(entity);
			}
		}

		for (TileEntity jiQi : RadarRegistry.getTileEntitiesInArea(new Vector2(this.xCoord - this.MAX_BIAN_JING, this.zCoord - this.MAX_BIAN_JING), new Vector2(this.xCoord + this.MAX_BIAN_JING, this.zCoord + this.MAX_BIAN_JING)))
		{
			if (jiQi instanceof TLeiDaTai)
			{
				if (!((TLeiDaTai) jiQi).isDisabled() && ((TLeiDaTai) jiQi).prevWatts > 0)
				{
					this.xunZhaoJiQi.add(jiQi);
				}
			}
			else
			{
				this.xunZhaoJiQi.add(jiQi);
			}
		}
	}

	public boolean isWeiXianDaoDan(EDaoDan daoDan)
	{
		if (daoDan == null) { return false; }
		if (daoDan.muBiao == null) { return false; }

		return (Vector2.distance(new Vector3(daoDan).toVector2(), new Vector2(this.xCoord, this.zCoord)) < this.alarmBanJing && Vector2.distance(daoDan.muBiao.toVector2(), new Vector2(this.xCoord, this.zCoord)) < this.safetyBanJing);
	}

	private Packet getDescriptionPacket2()
	{
		return PacketManager.getPacket(ZhuYao.CHANNEL, this, (int) 1, this.alarmBanJing, this.safetyBanJing);
	}

	@Override
	public Packet getDescriptionPacket()
	{
		double sendDian = this.wattsReceived;

		if (sendDian > 0)
		{
			sendDian = this.getRequest().getWatts();
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
					PacketManager.sendPacketToClients(this.getDescriptionPacket2(), this.worldObj, new Vector3(this), 15);
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
					this.wattsReceived = dataStream.readDouble();
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
	public boolean isPoweringTo(ForgeDirection side)
	{
		if (this.prevWatts > 0 || this.wattsReceived > 0)
		{
			if (this.weiXianDaoDan.size() > 0)
			{
				if (this.emitAll)
					return true;

				for (EDaoDan daoDan : this.weiXianDaoDan)
				{
					Vector2 position = new Vector3(daoDan).toVector2();
					ForgeDirection daoDanFangXiang = ForgeDirection.UNKNOWN;
					double closest = -1;

					for (int i = 2; i < 6; i++)
					{
						double dist = Vector2.distance(position, new Vector2(this.xCoord + ForgeDirection.getOrientation(i).offsetX, this.zCoord + ForgeDirection.getOrientation(i).offsetZ));

						if (dist < closest || closest < 0)
						{
							daoDanFangXiang = ForgeDirection.getOrientation(i);
							closest = dist;
						}
					}

					if (daoDanFangXiang.getOpposite() == side)
						return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean isIndirectlyPoweringTo(ForgeDirection side)
	{
		return this.isPoweringTo(side);
	}

	/**
	 * Reads a tile entity from NBT.
	 */
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);

		this.safetyBanJing = nbt.getInteger("safetyBanJing");
		this.alarmBanJing = nbt.getInteger("alarmBanJing");
		this.emitAll = nbt.getBoolean("emitAll");
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.writeToNBT(par1NBTTagCompound);

		par1NBTTagCompound.setInteger("safetyBanJing", this.safetyBanJing);
		par1NBTTagCompound.setInteger("alarmBanJing", this.alarmBanJing);
		par1NBTTagCompound.setBoolean("emitAll", this.emitAll);
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
		if (entityPlayer.inventory.getCurrentItem() != null)
		{
			if (entityPlayer.inventory.getCurrentItem().getItem() instanceof IToolConfigurator)
			{
				if (!this.worldObj.isRemote)
				{
					this.emitAll = !this.emitAll;
					entityPlayer.addChatMessage("Radar redstone all side emission: " + this.emitAll);
				}
				return true;
			}
		}

		entityPlayer.openGui(ZhuYao.instance, CommonProxy.GUI_RADAR_STATION, this.worldObj, this.xCoord, this.yCoord, this.zCoord);
		return true;
	}

	@Override
	public void onCreate(Vector3 position)
	{
		ZhuYao.bJia.makeFakeBlock(worldObj, Vector3.add(new Vector3(0, 1, 0), position), new Vector3(this));

		ZhuYao.bJia.makeFakeBlock(worldObj, Vector3.add(new Vector3(1, 1, 0), position), new Vector3(this));
		ZhuYao.bJia.makeFakeBlock(worldObj, Vector3.add(new Vector3(-1, 1, 0), position), new Vector3(this));

		ZhuYao.bJia.makeFakeBlock(worldObj, Vector3.add(new Vector3(0, 1, 1), position), new Vector3(this));
		ZhuYao.bJia.makeFakeBlock(worldObj, Vector3.add(new Vector3(0, 1, -1), position), new Vector3(this));

		ZhuYao.bJia.makeFakeBlock(worldObj, Vector3.add(new Vector3(1, 1, -1), position), new Vector3(this));
		ZhuYao.bJia.makeFakeBlock(worldObj, Vector3.add(new Vector3(-1, 1, 1), position), new Vector3(this));

		ZhuYao.bJia.makeFakeBlock(worldObj, Vector3.add(new Vector3(1, 1, 1), position), new Vector3(this));
		ZhuYao.bJia.makeFakeBlock(worldObj, Vector3.add(new Vector3(-1, 1, -1), position), new Vector3(this));

	}

	@Override
	public String getType()
	{
		return "ICBMRadar";
	}

	@Override
	public String[] getMethodNames()
	{
		return new String[] { "getEntities", "getBlocks" };
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, int method, Object[] arguments) throws Exception
	{
		if (this.prevWatts < this.getRequest().getWatts()) { throw new Exception("Radar has insufficient electricity!"); }

		HashMap<String, Double> returnArray = new HashMap();

		switch (method)
		{
			case 0:

				List<Entity> entities = RadarRegistry.getEntitiesWithinRadius(new Vector3(this).toVector2(), this.alarmBanJing);

				for (Entity entity : entities)
				{
					returnArray.put("x", entity.posX);
					returnArray.put("y", entity.posY);
					returnArray.put("z", entity.posZ);
				}

				return new Object[] { returnArray };
			case 1:
				for (TileEntity jiQi : RadarRegistry.getTileEntitiesInArea(new Vector2(this.xCoord - this.MAX_BIAN_JING, this.zCoord - this.MAX_BIAN_JING), new Vector2(this.xCoord + this.MAX_BIAN_JING, this.zCoord + this.MAX_BIAN_JING)))
				{
					returnArray.put("x", (double) jiQi.xCoord);
					returnArray.put("y", (double) jiQi.yCoord);
					returnArray.put("z", (double) jiQi.zCoord);
				}
				return new Object[] { returnArray };
		}

		throw new Exception("Invalid ICBM Radar Function.");
	}

	@Override
	public void invalidate()
	{
		ForgeChunkManager.releaseTicket(ticket);
		RadarRegistry.unregister(this);
		super.invalidate();
	}

	@Override
	public boolean canAttachToSide(int side)
	{
		return true;
	}

	@Override
	public void attach(IComputerAccess computer)
	{

	}

	@Override
	public void detach(IComputerAccess computer)
	{

	}

	@Override
	public ElectricityPack getRequest()
	{
		return new ElectricityPack(15 / this.getVoltage(), this.getVoltage());
	}
}
