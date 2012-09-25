package icbm.jiqi;

import icbm.extend.TFaSheQi;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NetworkManager;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.Ticker;
import universalelectricity.electricity.ElectricInfo;
import universalelectricity.implement.IElectricityStorage;
import universalelectricity.implement.IRedstoneReceptor;
import universalelectricity.implement.IRotatable;
import universalelectricity.implement.ITier;
import universalelectricity.network.ConnectionHandler;
import universalelectricity.network.ConnectionHandler.ConnectionType;
import universalelectricity.network.IPacketReceiver;
import universalelectricity.network.ISimpleConnectionHandler;
import universalelectricity.network.PacketManager;
import universalelectricity.prefab.Vector3;

import com.google.common.io.ByteArrayDataInput;

/**
 * This tile entity is for the screen of the missile launcher
 * @author Calclavia
 *
 */
public class TFaSheShiMuo extends TFaSheQi implements IElectricityStorage, IPacketReceiver, ITier, IRedstoneReceptor, IRotatable, ISimpleConnectionHandler
{    
    //Is the block powered by redstone?
    private boolean isPowered = false;
    
    //The frequency of the missile launcher
    public short frequency = 0;
    
    //The rotation of this missile component
    private byte orientation = 3;
    
    //The tier of this screen
    private int tier = 0;
    
    //The missile launcher base in which this screen is connected with
    public TFaSheDi connectedBase = null;

    //The electricity stored in the launcher screen
    public double dianXiaoShi = 0;
    
	private int yongZhe = 0;

	private boolean packetGengXin;
      	
  	public TFaSheShiMuo()
  	{
  		super();
		ConnectionHandler.registerConnectionHandler(this);
  	}
    
  	@Override
	public void onReceive(TileEntity sender, double amps, double voltage, ForgeDirection side)
	{
		if(!this.isDisabled())
    	{
			this.setWattHours(this.dianXiaoShi+ElectricInfo.getWattHours(amps, voltage));
    	}
	}
  	
  	public void updateEntity()
	{
  		super.updateEntity();
  		
    	if(!this.isDisabled())
    	{    		
	    	if(this.connectedBase == null)
	    	{
	        	for(byte i = 2; i < 6; i++)
	        	{
	        		Vector3 position = new Vector3(this.xCoord, this.yCoord, this.zCoord);
	        		position.modifyPositionFromSide(ForgeDirection.getOrientation(i));
	        		
	        		TileEntity tileEntity = this.worldObj.getBlockTileEntity(position.intX(), position.intY(), position.intZ());
	        		
	        		if(tileEntity != null)
	        		{
		        		if(tileEntity instanceof TFaSheDi)
		            	{
		            		this.connectedBase = (TFaSheDi)tileEntity;
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
	    	
	    	if(isPowered && !this.worldObj.isRemote)
	    	{
	    		isPowered = false;
	    		this.launch();
	    	}
    	}
	    
    	if(!this.worldObj.isRemote)
		{
	        if(Ticker.inGameTicks % 40 == 0)
	    	{
	        	if(this.yongZhe  > 0)
	        	{
		        	if(this.target == null) this.target = new Vector3(this.xCoord, 0, this.zCoord);
		    		PacketManager.sendTileEntityPacketWithRange(this, "ICBM", 15, (int)3, this.dianXiaoShi, this.disabledTicks, this.target.x, this.target.y, this.target.z);
	        	}
	    	}
	        
	        if(this.packetGengXin)
	        {
				PacketManager.sendTileEntityPacket(this, "ICBM", (int)0, this.orientation, this.tier, this.frequency);
				this.packetGengXin = false;
	        }
		}
	}
	
	@Override
	public void handlePacketData(NetworkManager network, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput dataStream)
	{
		try
        {
			final int ID = dataStream.readInt();
			
			if(ID == -1)
			{
				if(dataStream.readBoolean())
				{
					this.yongZhe ++;
				}
				else
				{
					this.yongZhe --;
				}
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
				if(this.worldObj.isRemote)
				{
					this.dianXiaoShi = dataStream.readDouble();
		            this.disabledTicks = dataStream.readInt();
					this.target = new Vector3(dataStream.readDouble(), dataStream.readDouble(), dataStream.readDouble());
				}
			}
			
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
	}
	
	@Override
	public void handelConnection(ConnectionType type, Object... data)
	{
		if(type == ConnectionType.LOGIN_SERVER)
		{
        	this.packetGengXin = true;
		}
	}
    
    //Checks if the missile is launchable
    public boolean canLaunch()
    {
    	if(this.connectedBase != null && !this.isDisabled())
    	{
	    	if(this.connectedBase.containingMissile != null)
	        {
	    		if(this.dianXiaoShi >= this.getMaxWattHours())
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
            this.setWattHours(0);
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
        else if(this.dianXiaoShi < this.getMaxWattHours())
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
    	this.dianXiaoShi = par1NBTTagCompound.getDouble("electricityStored");
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
    	par1NBTTagCompound.setDouble("electricityStored", this.dianXiaoShi);
    }

	@Override
	public double getVoltage()
	{
		switch(this.getTier())
		{
			default: return 120;
			case 1: return 240;
			case 2: return 580;
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
    public double wattRequest()
    {
        if (!this.isDisabled())
        {
            return Math.ceil(ElectricInfo.getWatts(this.getMaxWattHours()) - ElectricInfo.getWatts(this.dianXiaoShi));
        }

        return 0;
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
    public double getWattHours(Object... data)
    {
    	return this.dianXiaoShi;
    }

	@Override
	public void setWattHours(double wattHours, Object... data)
	{
		this.dianXiaoShi = Math.max(Math.min(wattHours, this.getMaxWattHours()), 0);
	}

	@Override
	public double getMaxWattHours()
	{
		return Math.max(Math.min(1.1*this.getVoltage(), 300), 150);
	}
}
