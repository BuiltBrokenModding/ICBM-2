package icbm.explosion.machines;

import icbm.api.IBlockFrequency;
import icbm.api.IItemFrequency;
import icbm.api.IRadarDetectable;
import icbm.api.RadarRegistry;
import icbm.core.base.TileEnityBase;
import icbm.core.implement.IChunkLoadHandler;
import icbm.core.implement.IRedstoneProvider;
import icbm.explosion.ICBMExplosion;
import icbm.explosion.missile.missile.EntityMissile;

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
import universalelectricity.api.vector.Vector2;
import universalelectricity.api.vector.Vector3;

import com.builtbroken.minecraft.prefab.BlockAdvanced;
import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import dan200.computer.api.IComputerAccess;
import dan200.computer.api.ILuaContext;
import dan200.computer.api.IPeripheral;

public class TileEntityRadarStation extends TileEnityBase implements IChunkLoadHandler, IPacketReceiver, IRedstoneProvider, IPeripheral, IBlockFrequency, IBlockActivate, IRotatable
{
    public final static int MAX_DETECTION_RANGE = 500;

    public static final float WATTS = 1.5f;

    public float rotation = 0;

    public int alarmRange = 100;

    public int safetyRange = 50;

    /** List of all incoming missiles, in order of distance. */
    private List<EntityMissile> incomingMissiles = new ArrayList<EntityMissile>();

    public List<Entity> detectedEntities = new ArrayList<Entity>();

    public List<TileEntity> detectedTiles = new ArrayList<TileEntity>();

    private final Set<EntityPlayer> playersUsing = new HashSet<EntityPlayer>();

    public boolean emitAll = true;

    private byte fangXiang = 3;

    private Ticket ticket;

    public TileEntityRadarStation()
    {
        super();
        RadarRegistry.register(this);
    }

    @Override
    public void initiate()
    {
        super.initiate();
        this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.worldObj.getBlockId(this.xCoord, this.yCoord, this.zCoord));
        this.chunkLoaderInit(ForgeChunkManager.requestTicket(ICBMExplosion.instance, this.worldObj, Type.NORMAL));
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
                for (EntityPlayer player : this.playersUsing)
                {
                    PacketDispatcher.sendPacketToPlayer(this.getDescriptionPacket2(), (Player) player);
                }
            }
        }

        if (this.provideElectricity(WATTS, false).getWatts() >= this.getRequest(null))
        {
            this.rotation += 0.08f;

            if (this.rotation > 360)
            {
                this.rotation = 0;
            }

            if (!this.worldObj.isRemote)
            {
                this.provideElectricity(WATTS, true);
            }

            int prevDetectedEntities = this.detectedEntities.size();

            // Do a radar scan
            this.doScan();

            if (prevDetectedEntities != this.detectedEntities.size())
            {
                this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.getBlockType().blockID);
            }

            if (this.ticks % 20 == 0 && this.incomingMissiles.size() > 0)
            {
                for (TileEntityLauncherPrefab faSheQi : MissileLauncherRegistry.getFaSheQi())
                {
                    if (new Vector3(this).distance(new Vector3(faSheQi)) < this.alarmRange && faSheQi.getFrequency() == this.getFrequency())
                    {
                        if (faSheQi instanceof TileEntityLauncherScreen)
                        {
                            double height = faSheQi.getTarget() != null ? faSheQi.getTarget().y : 0;
                            faSheQi.setTarget(new Vector3(this.incomingMissiles.get(0).posX, height, this.incomingMissiles.get(0).posZ));
                        }
                        else
                        {
                            faSheQi.setTarget(new Vector3(this.incomingMissiles.get(0)));
                        }
                    }
                }
            }
        }
        else
        {
            if (this.detectedEntities.size() > 0)
            {
                this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.getBlockType().blockID);
            }

            this.detectedEntities.clear();
            this.detectedTiles.clear();
        }

        if (this.ticks % 40 == 0)
        {
            this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.getBlockType().blockID);
        }
    }

    private void doScan()
    {
        this.incomingMissiles.clear();
        this.detectedEntities.clear();
        this.detectedTiles.clear();

        List<Entity> entities = RadarRegistry.getEntitiesWithinRadius(new Vector3(this).toVector2(), MAX_DETECTION_RANGE);

        for (Entity entity : entities)
        {
            if (entity instanceof EntityMissile)
            {
                if (((EntityMissile) entity).feiXingTick > -1)
                {
                    if (!this.detectedEntities.contains(entity))
                    {
                        this.detectedEntities.add(entity);
                    }

                    if (this.isWeiXianDaoDan((EntityMissile) entity))
                    {
                        if (this.incomingMissiles.size() > 0)
                        {
                            /** Sort in order of distance */
                            double dist = new Vector3(this).distance(new Vector3(entity));

                            for (int i = 0; i < this.incomingMissiles.size(); i++)
                            {
                                EntityMissile daoDan = this.incomingMissiles.get(i);

                                if (dist < new Vector3(this).distance(new Vector3(daoDan)))
                                {
                                    this.incomingMissiles.add(i, (EntityMissile) entity);
                                    break;
                                }
                                else if (i == this.incomingMissiles.size() - 1)
                                {
                                    this.incomingMissiles.add((EntityMissile) entity);
                                    break;
                                }
                            }
                        }
                        else
                        {
                            this.incomingMissiles.add((EntityMissile) entity);
                        }
                    }
                }
            }
            else
            {
                this.detectedEntities.add(entity);
            }
        }

        List<EntityPlayer> players = this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getBoundingBox(this.xCoord - MAX_DETECTION_RANGE, this.yCoord - MAX_DETECTION_RANGE, this.zCoord - MAX_DETECTION_RANGE, this.xCoord + MAX_DETECTION_RANGE, this.yCoord + MAX_DETECTION_RANGE, this.zCoord + MAX_DETECTION_RANGE));

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
                    this.detectedEntities.add(player);
                }
            }
        }

        for (TileEntity jiQi : RadarRegistry.getTileEntitiesInArea(new Vector2(this.xCoord - TileEntityRadarStation.MAX_DETECTION_RANGE, this.zCoord - TileEntityRadarStation.MAX_DETECTION_RANGE), new Vector2(this.xCoord + TileEntityRadarStation.MAX_DETECTION_RANGE, this.zCoord + TileEntityRadarStation.MAX_DETECTION_RANGE)))
        {
            if (jiQi instanceof TileEntityRadarStation)
            {
                if (((TileEntityRadarStation) jiQi).getEnergyStored() > 0)
                {
                    this.detectedTiles.add(jiQi);
                }
            }
            else
            {
                if (this.detectedTiles instanceof IRadarDetectable)
                {
                    if (((IRadarDetectable) this.detectedTiles).canDetect(this))
                    {
                        this.detectedTiles.add(jiQi);
                    }
                }
                else
                {
                    this.detectedTiles.add(jiQi);
                }
            }
        }
    }

    public boolean isWeiXianDaoDan(EntityMissile daoDan)
    {
        if (daoDan == null)
        {
            return false;
        }
        if (daoDan.targetVector == null)
        {
            return false;
        }

        return (Vector2.distance(new Vector3(daoDan).toVector2(), new Vector2(this.xCoord, this.zCoord)) < this.alarmRange && Vector2.distance(daoDan.targetVector.toVector2(), new Vector2(this.xCoord, this.zCoord)) < this.safetyRange);
    }

    private Packet getDescriptionPacket2()
    {
        return PacketManager.getPacket(ICBMExplosion.CHANNEL, this, 1, this.alarmRange, this.safetyRange, this.getFrequency());
    }

    @Override
    public Packet getDescriptionPacket()
    {
        return PacketManager.getPacket(ICBMExplosion.CHANNEL, this, 4, this.fangXiang, this.getEnergyStored());
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
                    this.playersUsing.add(player);
                }
                else
                {
                    this.playersUsing.remove(player);
                }
            }
            else if (this.worldObj.isRemote)
            {
                if (ID == 1)
                {
                    this.alarmRange = dataStream.readInt();
                    this.safetyRange = dataStream.readInt();
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
                    this.safetyRange = dataStream.readInt();
                }
                else if (ID == 3)
                {
                    this.alarmRange = dataStream.readInt();
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
            if (this.incomingMissiles.size() > 0)
            {
                if (this.emitAll)
                    return true;

                for (EntityMissile daoDan : this.incomingMissiles)
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

    /** Reads a tile entity from NBT. */
    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        this.safetyRange = nbt.getInteger("safetyBanJing");
        this.alarmRange = nbt.getInteger("alarmBanJing");
        this.emitAll = nbt.getBoolean("emitAll");
        this.fangXiang = nbt.getByte("fangXiang");
    }

    /** Writes a tile entity to NBT. */
    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setInteger("safetyBanJing", this.safetyRange);
        nbt.setInteger("alarmBanJing", this.alarmRange);
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

        entityPlayer.openGui(ICBMExplosion.instance, 0, this.worldObj, this.xCoord, this.yCoord, this.zCoord);
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
                List<Entity> entities = RadarRegistry.getEntitiesWithinRadius(new Vector3(this).toVector2(), this.alarmRange);

                for (Entity entity : entities)
                {
                    returnArray.put("x_" + count, entity.posX);
                    returnArray.put("y_" + count, entity.posY);
                    returnArray.put("z_" + count, entity.posZ);
                    count++;
                }

                return new Object[] { returnArray };
            case 1:
                for (TileEntity jiQi : RadarRegistry.getTileEntitiesInArea(new Vector2(this.xCoord - TileEntityRadarStation.MAX_DETECTION_RANGE, this.zCoord - TileEntityRadarStation.MAX_DETECTION_RANGE), new Vector2(this.xCoord + TileEntityRadarStation.MAX_DETECTION_RANGE, this.zCoord + TileEntityRadarStation.MAX_DETECTION_RANGE)))
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
        return WATTS * 3;
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
