package icbm.machines;

import icbm.extend.TileEntityLauncher;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NetworkManager;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.Vector3;
import universalelectricity.extend.IElectricityStorage;
import universalelectricity.extend.IRedstoneReceptor;
import universalelectricity.extend.IRotatable;
import universalelectricity.extend.ITier;
import universalelectricity.network.IPacketReceiver;
import universalelectricity.network.PacketManager;

import com.google.common.io.ByteArrayDataInput;

/**
 * This tile entity is for the screen of the missile launcher
 * @author Calclavia
 *
 */
public class TileEntityLauncherScreen extends TileEntityLauncher implements IElectricityStorage, IPacketReceiver, ITier, IRedstoneReceptor, IRotatable
{
    public final int WATT_HOURS_REQUIRED = 15000;
    
    //Is the block powered by redstone?
    private boolean isPowered = false;
    
    //The frequency of the missile launcher
    public short frequency = 0;
    
    //The rotation of this missile component
    private byte orientation = 3;
    
    //The tier of this screen
    private int tier = 0;
    
    //The missile launcher base in which this screen is connected with
    public TileEntityLauncherBase connectedBase = null;

    //The electricity stored in the launcher screen
    public float wattHourStored = 0;
    
    private boolean isGUIOpen = false;
      	
  	public TileEntityLauncherScreen()
  	{
  		super();
  	}
    
    /**
	 * Called every tick. Super this!
	 */
	@Override
	public void onUpdate(float watts, float voltage, ForgeDirection side)
	{		
    	if(!this.isDisabled())
    	{
    		float rejectedElectricity = Math.max((this.wattHourStored + watts) - this.WATT_HOURS_REQUIRED, 0);
    		this.wattHourStored = Math.max(this.wattHourStored+watts - rejectedElectricity, 0);
    		
	    	if(this.connectedBase == null)
	    	{
	        	for(byte i = 2; i < 6; i++)
	        	{
	        		Vector3 position = new Vector3(this.xCoord, this.yCoord, this.zCoord);
	        		position.modifyPositionFromSide(ForgeDirection.getOrientation(i));
	        		
	        		TileEntity tileEntity = this.worldObj.getBlockTileEntity(position.intX(), position.intY(), position.intZ());
	        		
	        		if(tileEntity != null)
	        		{
		        		if(tileEntity instanceof TileEntityLauncherBase)
		            	{
		            		this.connectedBase = (TileEntityLauncherBase)tileEntity;
		            		this.orientation = i;
		            	}
	        		}
	        	}
	    	}
	    	else
	        {
	        	if(this.connectedBase.isInvalid())
	        	{
	        		this.connectedBase = null;
	        	}
	        }
	    	
	    	if(isPowered)
	    	{
	    		isPowered = false;
	    		this.launch();
	    	}
    	}	
	    
    	if(!this.worldObj.isRemote)
		{
    		super.onUpdate(watts, voltage, side);

	    	if(this.isGUIOpen)
	    	{
	        	if(this.target == null) this.target = new Vector3(this.xCoord, 0, this.zCoord);
	    		PacketManager.sendTileEntityPacketWithRange(this, "ICBM", 20, (int)3, this.wattHourStored, this.disabledTicks, this.target.x, this.target.y, this.target.z);
	    	}
	    	
			PacketManager.sendTileEntityPacketWithRange(this, "ICBM", 100, (int)0, this.orientation, this.tier, this.frequency);
		}
	}
	
	@Override
	public void handlePacketData(NetworkManager network, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput dataStream)
	{
		try
        {
			int ID = dataStream.readInt();
			
			if(ID == -1 && !this.worldObj.isRemote)
	        {
		        this.isGUIOpen = dataStream.readBoolean();
	        }
	        else if(ID == 0)
			{
	            this.orientation = dataStream.readByte();
	            this.tier = dataStream.readInt();
				this.frequency = dataStream.readShort();
			}
			else if(ID == 1)
			{
				if(!this.worldObj.isRemote)
				{
					this.frequency = dataStream.readShort();
				}
			}
			else if(ID == 2)
			{
				if(!this.worldObj.isRemote)
				{
					this.target = new Vector3(dataStream.readDouble(), dataStream.readDouble(), dataStream.readDouble());
				}
			}
			else if(ID == 3)
			{
				this.wattHourStored = dataStream.readFloat();
	            this.disabledTicks = dataStream.readInt();
				this.target = new Vector3(dataStream.readDouble(), dataStream.readDouble(), dataStream.readDouble());
			}
			
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
	}
    
    //Checks if the missile is launchable
    public boolean canLaunch()
    {
    	if(this.connectedBase != null && !this.isDisabled())
    	{
	    	if(this.connectedBase.containingMissile != null)
	        {
	    		if(this.wattHourStored >= this.WATT_HOURS_REQUIRED)
	    		{
		            if(this.connectedBase.isInRange(this.target))
		            {
		            	return true;
		            }
		            
	    		}
	        }
    	}
    	
    	return false;
    }
    
    /**
     * Calls the missile launcher base to launch it's missile towards a targeted location
     */
    public void launch()
    {
    	if(this.canLaunch())
    	{
            this.wattHourStored = 0;
            this.connectedBase.launchMissile(this.target);
    	}          
    }
    

	/**
	 * Gets the display status of the missile launcher
	 * @return The string to be displayed
	 */
    public String getStatus()
    {
    	 String color = "\u00a74";
         String status = "Idle";
    	
    	if(this.isDisabled())
    	{
        	status = "Disabled";
    	}
        else if(this.connectedBase == null)
        {
        	status = "Not connected!";
        }
        else if(this.wattHourStored < this.WATT_HOURS_REQUIRED)
    	{
    		status = "Insufficient electricity!";
    	}
        else if(this.connectedBase.containingMissile == null)
    	{
    		status = "Missile silo is empty!";
    	}
        else if(this.target == null)
        {
        	status = "Target is invalid!";
        }
        else if(this.connectedBase.isTooClose(this.target))
    	{
    		status = "Target too close!";
    	}
        else if(this.connectedBase.isTooFar(this.target))
    	{
    		status = "Target too far!";
    	}
    	else
    	{
    		color = "\u00a72";
    		status = "Ready to launch!";
    	}
    	
    	return color+status;
    }
    

    /**
     * Reads a tile entity from NBT.
     */
    @Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
    	super.readFromNBT(par1NBTTagCompound);
    	
    	this.target = Vector3.readFromNBT("target", par1NBTTagCompound);
    	this.tier = par1NBTTagCompound.getInteger("tier");
    	this.frequency = par1NBTTagCompound.getShort("frequency");
    	this.orientation = par1NBTTagCompound.getByte("facingDirection");
    	this.wattHourStored = par1NBTTagCompound.getFloat("electricityStored");
    }

    /**
     * Writes a tile entity to NBT.
     */
    @Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
    	super.writeToNBT(par1NBTTagCompound);
    	if(this.target != null)
    	{
    		this.target.writeToNBT("target", par1NBTTagCompound);
    	}

    	par1NBTTagCompound.setInteger("tier", this.tier);
    	par1NBTTagCompound.setShort("frequency", this.frequency);
    	par1NBTTagCompound.setByte("facingDirection", this.orientation);
    	par1NBTTagCompound.setFloat("electricityStored", this.wattHourStored);
    }

	@Override
	public float getVoltage()
	{
		switch(this.getTier())
		{
			default: return 120;
			case 1: return 220;
			case 2: return 320;
		}
	}


	@Override
	public void onPowerOn()
	{
		this.isPowered = true;
	}


	@Override
	public void onPowerOff()
	{
		this.isPowered = false;
	}


	@Override
	public int getTier()
	{
		return this.tier;
	}


	@Override
	public void setTier(int tier)
	{
		this.tier = tier;
	}


	@Override
	public ForgeDirection getDirection()
	{
		return ForgeDirection.getOrientation(this.orientation);
	}

	@Override
	public void setDirection(ForgeDirection facingDirection) 
	{
		this.orientation = (byte) facingDirection.ordinal();
	}

	@Override
	public float ampRequest()
	{
		return this.WATT_HOURS_REQUIRED-this.wattHourStored;
	}

	@Override
	public boolean canReceiveFromSide(ForgeDirection side)
	{
		return true;
	}
	
	@Override
	public short getFrequency()
	{
		return this.frequency;
	}
	
	@Override
	public int getTickInterval()
	{
		return 10;
	}
	
	@Override
	public float getWattHours()
	{
		return this.wattHourStored;
	}

	@Override
	public void setWattHours(float WattHours)
	{
		this.wattHourStored = WattHours;
	}
}
