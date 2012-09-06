package icbm.machines;

import icbm.BlockInvisible;
import icbm.ICBM;
import icbm.ICBMCommonProxy;
import icbm.explosions.Explosive;
import icbm.extend.IMultiBlock;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NetworkManager;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.Vector3;
import universalelectricity.electricity.ElectricityManager;
import universalelectricity.electricity.TileEntityElectricUnit;
import universalelectricity.extend.IElectricityStorage;
import universalelectricity.extend.IRedstoneReceptor;
import universalelectricity.network.IPacketReceiver;
import universalelectricity.network.PacketManager;

import com.google.common.io.ByteArrayDataInput;

public class TileEntityEMPTower extends TileEntityElectricUnit implements IElectricityStorage, IPacketReceiver, IMultiBlock, IRedstoneReceptor
{
	public static int MAX_WATTS_REQUIREMENT = 150000;
	
	public float wattsRequired = MAX_WATTS_REQUIREMENT;
	
    //The maximum possible radius for the EMP to strike
    public static final int MAX_RADIUS = 60;
	
	public float rotationYaw = 0;
	private float rotationSpeed;
	
    //The electricity stored
    public float electricityStored = 0;
    
    //The EMP mode. 0 = All, 1 = Missiles Only, 2 = Electricity Only
    public byte EMPMode = 0;
    
    //The EMP explosion radius
    public int radius = 60;
    
    //Used to calculate every second
    private int secondTick = 0;
    
    public TileEntityEMPTower()
    {
		super();
    }
    
  	/**
	 * Called every tick. Super this!
	 */
	@Override
	public void onUpdate(float watts, float voltage, ForgeDirection side)
	{
		this.wattsRequired = MAX_WATTS_REQUIREMENT * ((float)this.radius/(float)MAX_RADIUS);
		
		if(!this.worldObj.isRemote)
		{
			super.onUpdate(watts, voltage, side);
		
			if(!this.isDisabled())
	    	{
	    		float rejectedElectricity = Math.max((this.electricityStored + watts) - this.wattsRequired, 0);
	    		this.electricityStored = Math.max(this.electricityStored+watts - rejectedElectricity, 0);
	    		
				if(this.secondTick >= 20 && this.electricityStored > 0)
				{
					this.worldObj.playSoundEffect((int)this.xCoord, (int)this.yCoord, (int)this.zCoord, "icbm.machinehum", 0.5F, 0.85F*this.electricityStored/this.wattsRequired);
					this.secondTick = 0;
				}
				
				this.rotationSpeed = (float) (0.5*this.electricityStored/this.wattsRequired*this.electricityStored/this.wattsRequired);
				this.rotationYaw += rotationSpeed;
				if(this.rotationYaw > 360) this.rotationYaw = 0;
	    	}
			
			//Slowly let the EMP tower lose it's electricity
			if(this.electricityStored < this.wattsRequired)
			{
				this.electricityStored = Math.max(0, this.electricityStored - 1);
			}
			
			this.secondTick ++;
			
			PacketManager.sendTileEntityPacket(this, "ICBM", (int)1, this.electricityStored, this.rotationYaw, this.disabledTicks, this.radius, this.EMPMode);
		}
    }
	
	@Override
	public void handlePacketData(NetworkManager network, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput dataStream)
	{
		try
        {
			int ID = dataStream.readInt();
			
			if(ID == 1)
			{
	            this.electricityStored = dataStream.readFloat();
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
	public float electricityRequest()
	{
		return this.wattsRequired-this.electricityStored;
	}

	@Override
	public boolean canReceiveFromSide(ForgeDirection side)
	{
		return true;
	}

	@Override
	public float getVoltage()
	{
		return 220F;
	}
	
	/**
     * Reads a tile entity from NBT.
     */
    @Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
    	super.readFromNBT(par1NBTTagCompound);
    	
    	this.electricityStored = par1NBTTagCompound.getFloat("electricityStored");
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

    	par1NBTTagCompound.setFloat("electricityStored", this.electricityStored);
    	par1NBTTagCompound.setInteger("radius", this.radius);
    	par1NBTTagCompound.setByte("EMPMode", (byte)this.EMPMode);
    }

	@Override
	public void onPowerOn() 
	{
		if(this.electricityStored >= this.wattsRequired)
		{
			if(this.EMPMode == 0 || this.EMPMode == 1)
			{
				Explosive.EMPSignal.doExplosion(worldObj, new Vector3(this.xCoord, this.yCoord, this.zCoord), null, this.radius, -1);
			}
			
			if(this.EMPMode == 0 || this.EMPMode == 2)
			{
				Explosive.EMPWave.doExplosion(worldObj, new Vector3(this.xCoord, this.yCoord, this.zCoord), null, this.radius, -1);
			}

			this.electricityStored = 0;
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
		BlockInvisible.makeInvisibleBlock(this.worldObj, position, Vector3.get(this));
	}

	@Override
	public float getAmpHours()
	{
		return this.electricityStored;
	}

	@Override
	public void setAmpHours(float AmpHours)
	{
		this.electricityStored = AmpHours;
	}
}
