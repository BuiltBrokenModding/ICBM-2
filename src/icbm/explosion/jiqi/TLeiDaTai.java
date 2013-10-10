package icbm.explosion.jiqi;

import icbm.api.IBlockFrequency;
import icbm.api.IItemFrequency;
import icbm.api.IRadarDetectable;
import icbm.api.RadarRegistry;
import icbm.core.base.TShengBuo;
import icbm.core.implement.IChunkLoadHandler;
import icbm.core.implement.IRedstoneProvider;
import icbm.explosion.ZhuYaoZhaPin;
import icbm.explosion.zhapin.daodan.EDaoDan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeChunkManager.Type;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.vector.Vector2;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.block.BlockAdvanced;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.network.PacketManager;
import universalelectricity.prefab.tile.IRotatable;
import calclavia.lib.multiblock.IBlockActivate;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import dan200.computer.api.IComputerAccess;
import dan200.computer.api.ILuaContext;
import dan200.computer.api.IPeripheral;

public class TLeiDaTai extends TShengBuo implements IChunkLoadHandler, IPacketReceiver, IRedstoneProvider, IPeripheral, IBlockFrequency, IBlockActivate, IRotatable
{
	public final static int MAX_BIAN_JING = 500;

	public static final float DIAN = 1.5f;

	public float xuanZhuan = 0;

	public int alarmBanJing = 100;

	public int safetyBanJing = 50;

	/**
	 * List of all incoming missiles, in order of distance.
	 */
	private List<EDaoDan> weiXianDaoDan = new ArrayList<EDaoDan>();

	public List<Entity> xunZhaoEntity = new ArrayList<Entity>();

	public List<TileEntity> xunZhaoJiQi = new ArrayList<TileEntity>();

	private final Set<EntityPlayer> yongZhe = new HashSet<EntityPlayer>();

	public boolean emitAll = true;

	private byte fangXiang = 3;

	private Ticket ticket;

	public TLeiDaTai()
	{
		super();
		RadarRegistry.register(this);
	}

	@Override
	public void initiate()
	{
		super.initiate();
		this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.worldObj.getBlockId(this.xCoord, this.yCoord, this.zCoord));
		this.chunkLoaderInit(ForgeChunkManager.requestTicket(ZhuYaoZhaPin.instance, this.worldObj, Type.NORMAL));
	}

	@Override
	public void chunkLoaderInit(Ticket ticket)
	{
		if (!this.worldObj.isRemote)
		{
			if (this.ticket == null && ticket != null)
			{
				this.ticket = ticket;
				new Vector3(this).writeToNBT(this.ticket.getModData());
				ForgeChunkManager.forceChunk(this.ticket, new ChunkCoordIntPair(this.xCoord >> 4, this.zCoord >> 4));
			}
		}
	}

	@Override
	public void updateEntity()
	{
		super.updateEntity();

		if (!this.worldObj.isRemote)
		{
			if (this.ticks % 40 == 0)
			{
				PacketManager.sendPacketToClients(this.getDescriptionPacket(), this.worldObj, new Vector3(this), 35);
			}
			else if (this.ticks % 3 == 0)
			{
				for (EntityPlayer wanJia : this.yongZhe)
				{
					PacketDispatcher.sendPacketToPlayer(this.getDescriptionPacket2(), (Player) wanJia);
				}
			}
		}

		if (this.provideElectricity(DIAN, false).getWatts() >= this.getRequest(null))
		{
			this.xuanZhuan += 0.08f;

			if (this.xuanZhuan > 360)
				this.xuanZhuan = 0;

			if (!this.worldObj.isRemote)
			{
				this.provideElectricity(DIAN, true);
			}

			int prevShuMu = this.xunZhaoEntity.size();

			// Do a radar scan
			this.doScan();

			if (prevShuMu != this.xunZhaoEntity.size())
			{
				this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.getBlockType().blockID);
			}

			if (this.ticks % 20 == 0 && this.weiXianDaoDan.size() > 0)
			{
				for (TFaSheQi faSheQi : FaSheQiGuanLi.getFaSheQi())
				{
					if (new Vector3(this).distanceTo(new Vector3(faSheQi)) < this.alarmBanJing && faSheQi.getFrequency() == this.getFrequency())
					{
						if (faSheQi instanceof TFaSheShiMuo)
						{
							double height = faSheQi.getTarget() != null ? faSheQi.getTarget().y : 0;
							faSheQi.setTarget(new Vector3(this.weiXianDaoDan.get(0).posX, height, this.weiXianDaoDan.get(0).posZ));
						}
						else
						{
							faSheQi.setTarget(new Vector3(this.weiXianDaoDan.get(0)));
						}
					}
				}
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

		if (this.ticks % 40 == 0)
		{
			this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.getBlockType().blockID);
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
						if (this.weiXianDaoDan.size() > 0)
						{
							/**
							 * Sort in order of distance
							 */
							double dist = new Vector3(this).distanceTo(new Vector3(entity));

							for (int i = 0; i < this.weiXianDaoDan.size(); i++)
							{
								EDaoDan daoDan = this.weiXianDaoDan.get(i);

								if (dist < new Vector3(this).distanceTo(new Vector3(daoDan)))
								{
									this.weiXianDaoDan.add(i, (EDaoDan) entity);
									break;
								}
								else if (i == this.weiXianDaoDan.size() - 1)
								{
									this.weiXianDaoDan.add((EDaoDan) entity);
									break;
								}
							}
						}
						else
						{
							this.weiXianDaoDan.add((EDaoDan) entity);
						}
					}
				}
			}
			else
			{
				this.xunZhaoEntity.add(entity);
			}
		}

		List<EntityPlayer> players = this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getBoundingBox(this.xCoord - MAX_BIAN_JING, this.yCoord - MAX_BIAN_JING, this.zCoord - MAX_BIAN_JING, this.xCoord + MAX_BIAN_JING, this.yCoord + MAX_BIAN_JING, this.zCoord + MAX_BIAN_JING));

		for (EntityPlayer player : players)
		{
			if (player != null)
			{
				boolean youHuoLuan = false;

				for (int i = 0; i < player.inventory.getSizeInventory(); i++)
				{
					ItemStack itemStack = player.inventory.getStackInSlot(i);

					if (itemStack != null)
					{
						if (itemStack.getItem() instanceof IItemFrequency)
						{
							youHuoLuan = true;
							break;
						}
					}
				}

				if (!youHuoLuan)
				{
					this.xunZhaoEntity.add(player);
				}
			}
		}

		for (TileEntity jiQi : RadarRegistry.getTileEntitiesInArea(new Vector2(this.xCoord - TLeiDaTai.MAX_BIAN_JING, this.zCoord - TLeiDaTai.MAX_BIAN_JING), new Vector2(this.xCoord + TLeiDaTai.MAX_BIAN_JING, this.zCoord + TLeiDaTai.MAX_BIAN_JING)))
		{
			if (jiQi instanceof TLeiDaTai)
			{
				if (((TLeiDaTai) jiQi).getEnergyStored() > 0)
				{
					this.xunZhaoJiQi.add(jiQi);
				}
			}
			else
			{
				if (this.xunZhaoJiQi instanceof IRadarDetectable)
				{
					if (((IRadarDetectable) this.xunZhaoJiQi).canDetect(this))
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
	}

	public boolean isWeiXianDaoDan(EDaoDan daoDan)
	{
		if (daoDan == null)
		{
			return false;
		}
		if (daoDan.muBiao == null)
		{
			return false;
		}

		return (Vector2.distance(new Vector3(daoDan).toVector2(), new Vector2(this.xCoord, this.zCoord)) < this.alarmBanJing && Vector2.distance(daoDan.muBiao.toVector2(), new Vector2(this.xCoord, this.zCoord)) < this.safetyBanJing);
	}

	private Packet getDescriptionPacket2()
	{
		return PacketManager.getPacket(ZhuYaoZhaPin.CHANNEL, this, 1, this.alarmBanJing, this.safetyBanJing, this.getFrequency());
	}

	@Override
	public Packet getDescriptionPacket()
	{
		return PacketManager.getPacket(ZhuYaoZhaPin.CHANNEL, this, 4, this.fangXiang, this.getEnergyStored());
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
					this.yongZhe.add(player);
				}
				else
				{
					this.yongZhe.remove(player);
				}
			}
			else if (this.worldObj.isRemote)
			{
				if (ID == 1)
				{
					this.alarmBanJing = dataStream.readInt();
					this.safetyBanJing = dataStream.readInt();
					this.setFrequency(dataStream.readInt());
				}
				else if (ID == 4)
				{
					this.fangXiang = dataStream.readByte();
					this.setEnergyStored(dataStream.readFloat());
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
				else if (ID == 4)
				{
					this.setFrequency(dataStream.readInt());
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
		if (this.getEnergyStored() > 0)
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
		this.fangXiang = nbt.getByte("fangXiang");
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		nbt.setInteger("safetyBanJing", this.safetyBanJing);
		nbt.setInteger("alarmBanJing", this.alarmBanJing);
		nbt.setBoolean("emitAll", this.emitAll);
		nbt.setByte("fangXiang", this.fangXiang);
	}

	@Override
	public boolean onActivated(EntityPlayer entityPlayer)
	{
		if (entityPlayer.inventory.getCurrentItem() != null)
		{
			if (((BlockAdvanced) this.getBlockType()).isUsableWrench(entityPlayer, entityPlayer.inventory.getCurrentItem(), this.xCoord, this.yCoord, this.zCoord))
			{
				if (!this.worldObj.isRemote)
				{
					this.emitAll = !this.emitAll;
					entityPlayer.addChatMessage("Radar redstone all side emission: " + this.emitAll);
				}

				return true;
			}
		}

		entityPlayer.openGui(ZhuYaoZhaPin.instance, 0, this.worldObj, this.xCoord, this.yCoord, this.zCoord);
		return true;
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
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws Exception
	{
		if (this.getEnergyStored() < this.getRequest(null))
		{
			throw new Exception("Radar has insufficient electricity!");
		}

		HashMap<String, Double> returnArray = new HashMap<String, Double>();
		int count = 0;

		switch (method)
		{
			case 0:
				List<Entity> entities = RadarRegistry.getEntitiesWithinRadius(new Vector3(this).toVector2(), this.alarmBanJing);

				for (Entity entity : entities)
				{
					returnArray.put("x_" + count, entity.posX);
					returnArray.put("y_" + count, entity.posY);
					returnArray.put("z_" + count, entity.posZ);
					count++;
				}

				return new Object[] { returnArray };
			case 1:
				for (TileEntity jiQi : RadarRegistry.getTileEntitiesInArea(new Vector2(this.xCoord - TLeiDaTai.MAX_BIAN_JING, this.zCoord - TLeiDaTai.MAX_BIAN_JING), new Vector2(this.xCoord + TLeiDaTai.MAX_BIAN_JING, this.zCoord + TLeiDaTai.MAX_BIAN_JING)))
				{
					returnArray.put("x_" + count, (double) jiQi.xCoord);
					returnArray.put("y_" + count, (double) jiQi.yCoord);
					returnArray.put("z_" + count, (double) jiQi.zCoord);
					count++;
				}
				return new Object[] { returnArray };
		}

		throw new Exception("Invalid ICBM Radar Function.");
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
	public void invalidate()
	{
		ForgeChunkManager.releaseTicket(this.ticket);
		RadarRegistry.unregister(this);
		super.invalidate();
	}

	@Override
	public float getRequest(ForgeDirection direction)
	{
		return (float) Math.ceil(this.getMaxEnergyStored() - this.getEnergyStored());
	}

	@Override
	public float getMaxEnergyStored()
	{
		return DIAN * 3;
	}

	@Override
	public float getProvide(ForgeDirection direction)
	{
		return 0;
	}

	@Override
	public ForgeDirection getDirection()
	{
		return ForgeDirection.getOrientation(this.fangXiang);
	}

	@Override
	public void setDirection(ForgeDirection facingDirection)
	{
		this.fangXiang = (byte) facingDirection.ordinal();
	}

}
