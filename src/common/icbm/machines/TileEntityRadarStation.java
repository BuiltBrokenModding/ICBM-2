package icbm.machines;

import icbm.BlockInvisible;
import icbm.EntityMissile;
import icbm.ICBM;
import icbm.ICBMCommonProxy;
import icbm.RadarStationManager;
import icbm.extend.IMultiBlock;
import icbm.missiles.MissileManager;

import java.util.ArrayList;
import java.util.List;

import com.google.common.io.ByteArrayDataInput;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NetworkManager;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.Vector2;
import universalelectricity.Vector3;
import universalelectricity.electricity.ElectricityManager;
import universalelectricity.electricity.TileEntityElectricUnit;
import universalelectricity.extend.IRedstoneProvider;
import universalelectricity.network.IPacketReceiver;
import universalelectricity.network.PacketManager;

public class TileEntityRadarStation extends TileEntityElectricUnit implements IPacketReceiver, IRedstoneProvider, IMultiBlock
{
	public final int ELECTRICITY_REQUIRED = 4;
    
	public final int MAX_RADIUS = 500;
	
	private static final boolean PLAY_SOUND = ICBM.getBooleanConfig("Radar Emit Sound", true);
	
	//The electricity stored
	public float electricityStored, prevElectricityStored = 0;
	
	public float radarRotationYaw = 0;
	
	private int secondTicks = 0;
	
	public int alarmRadius = 100;
	
	public int safetyRadius = 20;
	
	public List<EntityMissile> detectedMissiles = new ArrayList<EntityMissile>();
	
	public List<TileEntityRadarStation> detectedRadarStations = new ArrayList<TileEntityRadarStation>();

	private boolean soundAlarm = false;
		
	public TileEntityRadarStation()
	{
		super();
		RadarStationManager.addRadarStation(this);
	}
	
  	/**
	 * Called every tick. Super this!
	 */
	@Override
	public void onUpdate(float watts, float voltage, ForgeDirection side)
	{
		super.onUpdate(watts, voltage, side);
		
		if(!this.isDisabled())
		{
			this.prevElectricityStored = this.electricityStored += watts;
									
			if(this.electricityStored >= this.ELECTRICITY_REQUIRED)
			{
				this.radarRotationYaw += 0.05F;
				
				if(this.radarRotationYaw > 360) this.radarRotationYaw = 0;
				
				if(!this.worldObj.isRemote)
				{
					PacketManager.sendTileEntityPacket(this, "ICBM", (int)1, this.radarRotationYaw, this.alarmRadius, this.safetyRadius, this.disabledTicks, this.electricityStored, this.prevElectricityStored);
				}
				
				this.electricityStored = 0;
				
				//Do a radar scan
				boolean previousMissileDetection = this.detectedMissiles.size() > 0;
				this.soundAlarm = false;
				this.detectedMissiles.clear();
				this.detectedRadarStations.clear();
				
				List<EntityMissile> entitiesNearby = MissileManager.getMissileInArea(new Vector2(this.xCoord - MAX_RADIUS, this.zCoord - MAX_RADIUS), new Vector2(this.xCoord + MAX_RADIUS, this.zCoord + MAX_RADIUS));
				
		        for(EntityMissile missile : entitiesNearby)
		        {
		        	if(missile.ticksInAir > -1)
		        	{
		        		if(!this.detectedMissiles.contains(missile))
		        		{
		        			this.detectedMissiles.add(missile);
		        		}
		        		
		        		if(Vector2.distance(missile.targetPosition.toVector2(), new Vector2(this.xCoord, this.zCoord)) < this.safetyRadius)
		        		{
		        			this.soundAlarm  = true;
		        		}
		        	}
		        }
		        
		        for(TileEntityRadarStation radarStation : RadarStationManager.getRadarStationsInArea(new Vector2(this.xCoord-this.MAX_RADIUS, this.zCoord-this.MAX_RADIUS), new Vector2(this.xCoord+this.MAX_RADIUS, this.zCoord+this.MAX_RADIUS)))
		        {
		        	if(!radarStation.isDisabled() && radarStation.prevElectricityStored > 0)
		        	{
		        		this.detectedRadarStations.add(radarStation);
		        	}
		        }
		        
		        if(previousMissileDetection != this.detectedMissiles.size() > 0)
		        {
			        this.worldObj.notifyBlocksOfNeighborChange((int)this.xCoord, (int)this.yCoord, (int)this.zCoord, this.getBlockType().blockID);
		        }
		        
		        if(this.secondTicks >= 25)
				{
			        if(this.soundAlarm && PLAY_SOUND)
			        {
						this.worldObj.playSoundEffect((int)this.xCoord, (int)this.yCoord, (int)this.zCoord, "icbm.alarm", 3F, 1F);
			        }
			        
			        this.secondTicks = 0;
				}
				
				
				this.secondTicks ++;
			}
			else
			{
				if(this.detectedMissiles.size() > 0)
				{
					this.worldObj.notifyBlocksOfNeighborChange((int)this.xCoord, (int)this.yCoord, (int)this.zCoord, this.getBlockType().blockID);
				}
				
				this.detectedMissiles.clear();
				this.detectedRadarStations.clear();
			}
		}
	}

	@Override
	public void handlePacketData(NetworkManager network, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput dataStream)
	{
		try
	    {
	        final int ID = dataStream.readInt();
			
	        if(ID == 1)
	        {
		        this.radarRotationYaw = dataStream.readFloat();
		        this.alarmRadius = dataStream.readInt();
		        this.safetyRadius = dataStream.readInt();
		        this.disabledTicks = dataStream.readInt();
		        this.electricityStored = dataStream.readFloat();
		        this.prevElectricityStored = dataStream.readFloat();
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
    public boolean canUpdate()
    {
		if(this.worldObj != null)
		{
			this.worldObj.notifyBlocksOfNeighborChange((int)this.xCoord, (int)this.yCoord, (int)this.zCoord, this.getBlockType().blockID);
		}

        return false;
    }
	
	
	@Override
	public float electricityRequest()
	{
		return this.ELECTRICITY_REQUIRED*2;
	}

	@Override
	public boolean canReceiveFromSide(ForgeDirection side)
	{
		return true;
	}

	@Override
	public float getVoltage()
	{
		return 220;
	}

	@Override
	public boolean isPoweringTo(byte side)
	{
        return this.detectedMissiles.size() > 0 && this.soundAlarm;
	}

	@Override
	public boolean isIndirectlyPoweringTo(byte side)
	{
		return this.detectedMissiles.size() > 0 && this.soundAlarm;
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
    	this.electricityStored = par1NBTTagCompound.getFloat("electricityStored");
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
    	par1NBTTagCompound.setFloat("electricityStored", this.electricityStored);
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
		BlockInvisible.makeInvisibleBlock(worldObj, Vector3.add(new Vector3(0, 1, 0), position), Vector3.get(this));
		
		BlockInvisible.makeInvisibleBlock(worldObj, Vector3.add(new Vector3(1, 1, 0), position), Vector3.get(this));
		BlockInvisible.makeInvisibleBlock(worldObj, Vector3.add(new Vector3(-1, 1, 0), position), Vector3.get(this));
		
		BlockInvisible.makeInvisibleBlock(worldObj, Vector3.add(new Vector3(0, 1, 1), position), Vector3.get(this));
		BlockInvisible.makeInvisibleBlock(worldObj, Vector3.add(new Vector3(0, 1, -1), position), Vector3.get(this));
		
		BlockInvisible.makeInvisibleBlock(worldObj, Vector3.add(new Vector3(1, 1, -1), position), Vector3.get(this));
		BlockInvisible.makeInvisibleBlock(worldObj, Vector3.add(new Vector3(-1, 1, 1), position), Vector3.get(this));
		
		BlockInvisible.makeInvisibleBlock(worldObj, Vector3.add(new Vector3(1, 1, 1), position), Vector3.get(this));
		BlockInvisible.makeInvisibleBlock(worldObj, Vector3.add(new Vector3(-1, 1, -1), position), Vector3.get(this));

	}
}
