package icbm.jiqi;

import icbm.ZhuYao;
import icbm.ICBMCommonProxy;
import icbm.api.Launcher.LauncherType;
import icbm.extend.IBActivate;
import icbm.extend.TFaSheQi;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NetworkManager;
import net.minecraft.src.Packet;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.Ticker;
import universalelectricity.electricity.ElectricInfo;
import universalelectricity.implement.IRotatable;
import universalelectricity.implement.ITier;
import universalelectricity.network.IPacketReceiver;
import universalelectricity.network.PacketManager;
import universalelectricity.prefab.Vector3;

import com.google.common.io.ByteArrayDataInput;

/**
 * This tile entity is for the screen of the missile launcher
 * @author Calclavia
 *
 */
public class TFaSheShiMuo extends TFaSheQi implements IBActivate, IPacketReceiver, ITier, IRotatable 
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
    
	private int yongZhe = 0;

	private boolean packetGengXin = true;
      	
  	public TFaSheShiMuo()
  	{
  		super();
  	}
    
  	@Override
	public void onReceive(TileEntity sender, double amps, double voltage, ForgeDirection side)
	{
		if(!this.isDisabled())
    	{
			this.setJoules(this.dianXiaoShi+ElectricInfo.getJoules(amps, voltage));
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
	    	
	    	if(isPowered)
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
		        	if(this.muBiao == null) this.muBiao = new Vector3(this.xCoord, 0, this.zCoord);
		    		PacketManager.sendPacketToClients(PacketManager.getPacket("ICBM", this, (int)3, this.dianXiaoShi, this.disabledTicks, this.muBiao.x, this.muBiao.y, this.muBiao.z), this.worldObj, Vector3.get(this), 15);
	        	}
	    	}
	        
	        if(Ticker.inGameTicks % 600 == 0 || this.packetGengXin)
	        {
				PacketManager.sendPacketToClients(this.getDescriptionPacket());
				this.packetGengXin = false;
	        }
		}
	}
  	
  	@Override
    public Packet getDescriptionPacket()
    {
        return PacketManager.getPacket(ZhuYao.CHANNEL, this, (int)0, this.orientation, this.tier, this.frequency);
    }
	
  	@Override
	public void placeMissile(ItemStack itemStack)
	{
		if(this.connectedBase != null)
		{
			if(!this.connectedBase.isInvalid())
			{
				this.connectedBase.setInventorySlotContents(0, itemStack);
			}
		}
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
					PacketManager.sendPacketToClients(this.getDescriptionPacket());
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
					this.muBiao = new Vector3(dataStream.readDouble(), dataStream.readDouble(), dataStream.readDouble());
				}
			}
			else if(ID == 3)
			{
				if(this.worldObj.isRemote)
				{
					this.dianXiaoShi = dataStream.readDouble();
		            this.disabledTicks = dataStream.readInt();
					this.muBiao = new Vector3(dataStream.readDouble(), dataStream.readDouble(), dataStream.readDouble());
				}
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
	    	if(this.connectedBase.eDaoDan != null)
	        {
	    		if(this.dianXiaoShi >= this.getMaxJoules())
	    		{
		            if(this.connectedBase.isInRange(this.muBiao))
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
            this.setJoules(0);
            this.connectedBase.launchMissile(this.muBiao);
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
        else if(this.dianXiaoShi < this.getMaxJoules())
    	{
    		status = "Insufficient electricity!";
    	}
        else if(this.connectedBase.eDaoDan == null)
    	{
    		status = "Missile silo is empty!";
    	}
        else if(this.muBiao == null)
        {
        	status = "Target is invalid!";
        }
        else if(this.connectedBase.isTooClose(this.muBiao))
    	{
    		status = "Target too close!";
    	}
        else if(this.connectedBase.isTooFar(this.muBiao))
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
    	
    	this.muBiao = Vector3.readFromNBT("target", par1NBTTagCompound);
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
    	
    	if(this.muBiao != null)
    	{
    		this.muBiao.writeToNBT("target", par1NBTTagCompound);
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
            return Math.ceil(ElectricInfo.getWatts(this.getMaxJoules()) - ElectricInfo.getWatts(this.dianXiaoShi));
        }

        return 0;
    }

	@Override
	public boolean canReceiveFromSide(ForgeDirection side)
	{
		return true;
	}

	@Override
	public double getMaxJoules()
	{
		return Math.max(Math.min(1500*this.getVoltage(), 150), 50);
	}

	@Override
	public boolean onActivated(EntityPlayer entityPlayer)
	{
		entityPlayer.openGui(ZhuYao.instance, ICBMCommonProxy.GUI_LAUNCHER_SCREEN, this.worldObj, this.xCoord, this.yCoord, this.zCoord);
		return true;
	}
	
	@Override
	public LauncherType getLauncherType()
	{
		return LauncherType.TRADITIONAL;
	}

	@Override
	public short getFrequency(Object... data)
	{
		return this.frequency;
	}

	@Override
	public void setFrequency(short frequency, Object... data)
	{
		this.frequency = frequency;		
	}
}
