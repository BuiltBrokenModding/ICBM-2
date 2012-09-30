package icbm.jiqi;

import icbm.BYinXing;
import icbm.ICBM;
import icbm.ICBMCommonProxy;
import icbm.daodan.DaoDanGuanLi;
import icbm.daodan.EDaoDan;
import icbm.extend.IMB;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NetworkManager;
import net.minecraft.src.Packet;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.Ticker;
import universalelectricity.UEConfig;
import universalelectricity.basiccomponents.UELoader;
import universalelectricity.electricity.ElectricInfo;
import universalelectricity.implement.IRedstoneProvider;
import universalelectricity.network.IPacketReceiver;
import universalelectricity.network.PacketManager;
import universalelectricity.prefab.TileEntityElectricityReceiver;
import universalelectricity.prefab.Vector2;
import universalelectricity.prefab.Vector3;

import com.google.common.io.ByteArrayDataInput;

public class TLeiDaTai extends TileEntityElectricityReceiver implements IPacketReceiver, IRedstoneProvider, IMB
{
	//Watts Per Tick
	public final static int YAO_WA = 4;
    
	public final static int MAX_BIAN_JING = 500;
	
	private static final boolean PLAY_SOUND = UEConfig.getConfigData(ICBM.CONFIGURATION, "Radar Emit Sound", true);
	
	//The electricity stored
	public double wattsReceived, prevWattsReceived = 0;
	
	public float radarRotationYaw = 0;
		
	public int alarmRadius = 100;
	
	public int safetyRadius = 20;
	
	public List<EDaoDan> detectedMissiles = new ArrayList<EDaoDan>();
	
	public List<TLeiDaTai> detectedRadarStations = new ArrayList<TLeiDaTai>();

	private boolean missileAlert = false;
	
	private int playersUsing = 0;
		
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
		if(!this.isDisabled())
		{
			this.wattsReceived += ElectricInfo.getWatts(amps, voltage);
			this.prevWattsReceived = this.wattsReceived;
		}
	}
	
	@Override
    public void initiate()
    {
		if(this.worldObj != null)
		{
			this.worldObj.notifyBlocksOfNeighborChange((int)this.xCoord, (int)this.yCoord, (int)this.zCoord, this.getBlockType().blockID);
		}
    }
	
	public void updateEntity()
	{
		super.updateEntity();
		
		if(!this.worldObj.isRemote)
		{
			if(Ticker.inGameTicks % 20 == 0)
			{
				PacketManager.sendPacketToClients(this.getDescriptionPacket(), this.worldObj, Vector3.get(this), 100);
				
				if(this.playersUsing > 0)
				{
					PacketManager.sendPacketToClients(PacketManager.getPacket(ICBM.CHANNEL, this.alarmRadius, this.safetyRadius), this.worldObj, Vector3.get(this), 15);
				}
			}
		}
		
		if(!this.isDisabled())
		{
			if(this.wattsReceived >= this.YAO_WA)
			{				
				this.radarRotationYaw += 0.05F;
				
				if(this.radarRotationYaw > 360) this.radarRotationYaw = 0;

				if(!this.worldObj.isRemote)
				{
					this.wattsReceived = 0;
				}
				
				//Do a radar scan
				boolean previousMissileDetection = this.detectedMissiles.size() > 0;
				this.missileAlert = false;
				this.detectedMissiles.clear();
				this.detectedRadarStations.clear();
				
				List<EDaoDan> entitiesNearby = DaoDanGuanLi.getMissileInArea(new Vector2(this.xCoord - MAX_BIAN_JING, this.zCoord - MAX_BIAN_JING), new Vector2(this.xCoord + MAX_BIAN_JING, this.zCoord + MAX_BIAN_JING));
				
		        for(EDaoDan missile : entitiesNearby)
		        {
		        	if(missile.ticksInAir > -1)
		        	{
		        		if(!this.detectedMissiles.contains(missile))
		        		{
		        			this.detectedMissiles.add(missile);
		        		}
		        		
		        		if(Vector2.distance(missile.targetPosition.toVector2(), new Vector2(this.xCoord, this.zCoord)) < this.safetyRadius)
		        		{
		        			this.missileAlert  = true;
		        		}
		        	}
		        }
		        
		        for(TLeiDaTai radarStation : LeiDaGuanLi.getRadarStationsInArea(new Vector2(this.xCoord-this.MAX_BIAN_JING, this.zCoord-this.MAX_BIAN_JING), new Vector2(this.xCoord+this.MAX_BIAN_JING, this.zCoord+this.MAX_BIAN_JING)))
		        {
		        	if(!radarStation.isDisabled() && radarStation.prevWattsReceived > 0)
		        	{
		        		this.detectedRadarStations.add(radarStation);
		        	}
		        }
		        
		        if(previousMissileDetection != this.detectedMissiles.size() > 0)
		        {
			        this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.getBlockType().blockID);
		        }
		        
		        if(Ticker.inGameTicks % 25 == 0)
				{
			        if(this.missileAlert && PLAY_SOUND)
			        {
						this.worldObj.playSoundEffect(this.xCoord, this.yCoord, this.zCoord, "icbm.alarm", 4F, 1F);
			        }
				}
			}
			else
			{
				if(this.detectedMissiles.size() > 0)
				{
					this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.getBlockType().blockID);
				}
				
				this.detectedMissiles.clear();
				this.detectedRadarStations.clear();
			}
		}
		
		if(Ticker.inGameTicks % 40 == 0)
		{
			this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.getBlockType().blockID);
		}
	}
	
	@Override
    public Packet getDescriptionPacket()
    {
        return PacketManager.getPacket(UELoader.CHANNEL, this, (int)4, this.wattsReceived, this.disabledTicks);
    }

	@Override
	public void handlePacketData(NetworkManager network, int packetType, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput dataStream)
	{
		try
	    {
	        final int ID = dataStream.readInt();
			
	        if(ID == -1)
	        {
	        	if(dataStream.readBoolean())
				{
					this.playersUsing ++;
				}
				else
				{
					this.playersUsing --;
				}
	        }
	        else if(this.worldObj.isRemote)
	        {
	        	if(ID == 1)
		        {
			        this.alarmRadius = dataStream.readInt();
			        this.safetyRadius = dataStream.readInt();
		        }
	        	else if(ID == 4)
		        {
		        	this.wattsReceived = dataStream.readDouble();
			        this.disabledTicks = dataStream.readInt();
		        }
	        }
	        else if(!this.worldObj.isRemote)
	        {
	        	if(ID == 2)
	        	{
	        		this.safetyRadius = dataStream.readInt();
	        	}
	        	else if(ID == 3)
	        	{
	        		this.alarmRadius = dataStream.readInt();
	        	}
	        } 
	    }
	    catch(Exception e)
	    {
	        e.printStackTrace();
	    }
	}
	
	@Override
    public double wattRequest()
    {
        if (!this.isDisabled())
        {
            return Math.ceil(this.YAO_WA-this.wattsReceived);
        }

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
    	
    	this.safetyRadius = par1NBTTagCompound.getInteger("safetyRadius");
    	this.alarmRadius = par1NBTTagCompound.getInteger("alarmRadius");
    	this.wattsReceived = par1NBTTagCompound.getDouble("electricityStored");
    }

    /**
     * Writes a tile entity to NBT.
     */
    @Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
    	super.writeToNBT(par1NBTTagCompound);
    	
    	par1NBTTagCompound.setInteger("safetyRadius", this.safetyRadius);
    	par1NBTTagCompound.setInteger("alarmRadius", this.alarmRadius);
    	par1NBTTagCompound.setDouble("electricityStored", this.wattsReceived);
    }

	@Override
	public void onDestroy(TileEntity callingBlock)
	{
		this.worldObj.setBlockWithNotify(this.xCoord, this.yCoord, this.zCoord, 0);
		
		//Top 3x3
		this.worldObj.setBlockWithNotify(this.xCoord, this.yCoord+1, this.zCoord, 0);
		
		this.worldObj.setBlockWithNotify(this.xCoord+1, this.yCoord+1, this.zCoord, 0);
		this.worldObj.setBlockWithNotify(this.xCoord-1, this.yCoord+1, this.zCoord, 0);
		
		this.worldObj.setBlockWithNotify(this.xCoord, this.yCoord+1, this.zCoord+1, 0);
		this.worldObj.setBlockWithNotify(this.xCoord, this.yCoord+1, this.zCoord-1, 0);
		
		this.worldObj.setBlockWithNotify(this.xCoord+1, this.yCoord+1, this.zCoord+1, 0);
		this.worldObj.setBlockWithNotify(this.xCoord-1, this.yCoord+1, this.zCoord-1, 0);
		this.worldObj.setBlockWithNotify(this.xCoord+1, this.yCoord+1, this.zCoord-1, 0);
		this.worldObj.setBlockWithNotify(this.xCoord-1, this.yCoord+1, this.zCoord+1, 0);
	}

	@Override
	public boolean onActivated(EntityPlayer entityPlayer)
	{
		entityPlayer.openGui(ICBM.instance, ICBMCommonProxy.GUI_RADAR_STATION, this.worldObj, this.xCoord, this.yCoord, this.zCoord);
		return true;
	}

	@Override
	public void onCreate(Vector3 position)
	{
		BYinXing.makeInvisibleBlock(worldObj, Vector3.add(new Vector3(0, 1, 0), position), Vector3.get(this));
		
		BYinXing.makeInvisibleBlock(worldObj, Vector3.add(new Vector3(1, 1, 0), position), Vector3.get(this));
		BYinXing.makeInvisibleBlock(worldObj, Vector3.add(new Vector3(-1, 1, 0), position), Vector3.get(this));
		
		BYinXing.makeInvisibleBlock(worldObj, Vector3.add(new Vector3(0, 1, 1), position), Vector3.get(this));
		BYinXing.makeInvisibleBlock(worldObj, Vector3.add(new Vector3(0, 1, -1), position), Vector3.get(this));
		
		BYinXing.makeInvisibleBlock(worldObj, Vector3.add(new Vector3(1, 1, -1), position), Vector3.get(this));
		BYinXing.makeInvisibleBlock(worldObj, Vector3.add(new Vector3(-1, 1, 1), position), Vector3.get(this));
		
		BYinXing.makeInvisibleBlock(worldObj, Vector3.add(new Vector3(1, 1, 1), position), Vector3.get(this));
		BYinXing.makeInvisibleBlock(worldObj, Vector3.add(new Vector3(-1, 1, -1), position), Vector3.get(this));

	}
}
