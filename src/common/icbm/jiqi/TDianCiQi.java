package icbm.jiqi;

import icbm.BYinXing;
import icbm.ICBM;
import icbm.ICBMCommonProxy;
import icbm.extend.IMultiBlock;
import icbm.zhapin.ZhaPin;
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
import universalelectricity.network.IPacketReceiver;
import universalelectricity.network.PacketManager;
import universalelectricity.prefab.TileEntityElectricityReceiver;
import universalelectricity.prefab.Vector3;

import com.google.common.io.ByteArrayDataInput;

public class TDianCiQi extends TileEntityElectricityReceiver implements IElectricityStorage, IPacketReceiver, IMultiBlock, IRedstoneReceptor
{
    //The maximum possible radius for the EMP to strike
    public static final int MAX_RADIUS = 60;
	
	public float rotationYaw = 0;
	private float rotationSpeed;
	
    private double wattHourStored = 0;
    
    //The EMP mode. 0 = All, 1 = Missiles Only, 2 = Electricity Only
    public byte EMPMode = 0;
    
    //The EMP explosion radius
    public int radius = 60;
    
    //Used to calculate every second
    private int secondTick = 0;

	private int playersUsing = 0;
    
    public TDianCiQi()
    {
		super();
    }
    
  	/**
	 * Called every tick. Super this!
	 */
	@Override
	public void onReceive(TileEntity sender, double amps, double voltage, ForgeDirection side)
	{		
		if(!this.worldObj.isRemote)
		{		
			if(!this.isDisabled())
	    	{
				this.setWattHours(this.wattHourStored+ElectricInfo.getWattHours(amps, voltage));
	    	}
		}
	}
	
	public void updateEntity()
	{
		super.updateEntity();
		
		if(!this.worldObj.isRemote)
		{
			if(!this.isDisabled())
	    	{
	    		
				if(this.secondTick >= 20 && this.wattHourStored > 0)
				{
					this.worldObj.playSoundEffect((int)this.xCoord, (int)this.yCoord, (int)this.zCoord, "icbm.machinehum", 0.5F, (float) (0.85F*this.wattHourStored/this.getMaxWattHours()));
					this.secondTick = 0;
				}
				
				this.rotationSpeed = (float) (0.5*this.wattHourStored/this.getMaxWattHours()*this.wattHourStored/this.getMaxWattHours());
				this.rotationYaw += rotationSpeed;
				if(this.rotationYaw > 360) this.rotationYaw = 0;
	    	}
			
			//Slowly let the EMP tower lose it's electricity
			if(this.wattHourStored < this.getMaxWattHours())
			{
				this.wattHourStored = Math.max(0, this.wattHourStored - 1);
			}
			
			this.secondTick ++;
			
	        if(Ticker.inGameTicks % 40 == 0 && this.playersUsing  > 0)
	        {
	        	PacketManager.sendTileEntityPacketWithRange(this, "ICBM", 20, (int)1, this.wattHourStored, this.rotationYaw, this.disabledTicks, this.radius, this.EMPMode);
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
					this.playersUsing ++;
				}
				else
				{
					this.playersUsing --;
				}
			}
			else if(ID == 1)
			{
	            this.wattHourStored = dataStream.readFloat();
	            this.rotationYaw = dataStream.readFloat();
	            this.disabledTicks = dataStream.readInt();
	            this.radius = dataStream.readInt();
	            this.EMPMode = dataStream.readByte();
			}
			else if(ID == 2)
			{
	            this.radius = dataStream.readInt();
			}
			else if(ID == 3)
			{
	            this.EMPMode = dataStream.readByte();
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
            return this.getMaxWattHours() - this.wattHourStored;
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
		return 220;
	}
	
	/**
     * Reads a tile entity from NBT.
     */
    @Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
    	super.readFromNBT(par1NBTTagCompound);
    	
    	this.wattHourStored = par1NBTTagCompound.getDouble("electricityStored");
    	this.radius = par1NBTTagCompound.getInteger("radius");
    	this.EMPMode = par1NBTTagCompound.getByte("EMPMode");
    }

    /**
     * Writes a tile entity to NBT.
     */
    @Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
    	super.writeToNBT(par1NBTTagCompound);

    	par1NBTTagCompound.setDouble("electricityStored", this.wattHourStored);
    	par1NBTTagCompound.setInteger("radius", this.radius);
    	par1NBTTagCompound.setByte("EMPMode", (byte)this.EMPMode);
    }

	@Override
	public void onPowerOn() 
	{
		if(this.wattHourStored >= this.getMaxWattHours())
		{
			if(this.EMPMode == 0 || this.EMPMode == 1)
			{
				ZhaPin.EMPSignal.doExplosion(worldObj, new Vector3(this.xCoord, this.yCoord, this.zCoord), null, this.radius, -1);
			}
			
			if(this.EMPMode == 0 || this.EMPMode == 2)
			{
				ZhaPin.EMPWave.doExplosion(worldObj, new Vector3(this.xCoord, this.yCoord, this.zCoord), null, this.radius, -1);
			}

			this.wattHourStored = 0;
		}
	}

	@Override
	public void onPowerOff() { }
	
	@Override
	public void onDestroy(TileEntity callingBlock)
	{
		this.worldObj.setBlockWithNotify(this.xCoord, this.yCoord, this.zCoord, 0);
		this.worldObj.setBlockWithNotify(this.xCoord, this.yCoord+1, this.zCoord, 0);
	}

	@Override
	public boolean onActivated(EntityPlayer entityPlayer)
	{
		entityPlayer.openGui(ICBM.instance, ICBMCommonProxy.GUI_EMP_TOWER, this.worldObj, this.xCoord, this.yCoord, this.zCoord);
		return true;
	}

	@Override
	public void onCreate(Vector3 position)
	{
		position.y += 1;
		BYinXing.makeInvisibleBlock(this.worldObj, position, Vector3.get(this));
	}

	@Override
	public double getMaxWattHours()
	{
		return Math.max(150000*((float)this.radius/(float)MAX_RADIUS), 15000);
	}

	@Override
    public double getWattHours(Object... data)
    {
    	return this.wattHourStored;
    }

	@Override
	public void setWattHours(double wattHours, Object... data)
	{
		this.wattHourStored = Math.max(Math.min(wattHours, this.getMaxWattHours()), 0);
	}
}
