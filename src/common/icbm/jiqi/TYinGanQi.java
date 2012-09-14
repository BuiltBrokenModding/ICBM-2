package icbm.jiqi;

import icbm.dianqi.ItHuoLuanQi;

import java.util.List;

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NetworkManager;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.Vector3;
import universalelectricity.electricity.TileEntityElectricUnit;
import universalelectricity.extend.IRedstoneProvider;
import universalelectricity.network.IPacketReceiver;
import universalelectricity.network.PacketManager;

import com.google.common.io.ByteArrayDataInput;

public class TYinGanQi extends TileEntityElectricUnit implements IRedstoneProvider, IPacketReceiver
{
	public static final float YAO_DIAN = 10;
	
    //The electricity stored
    public float dian = 0;
    
    public float prevElectricityStored = 0;
    
    public short frequency = 0;

    public boolean isDetect = false;
    
    public boolean firstUpdate = true;
    
    public Vector3 minCoord = new Vector3(9, 9, 9);
    public Vector3 maxCoord = new Vector3(9, 9, 9);

	public byte mode = 0;

	private boolean isGUIOpen = false;

    public TYinGanQi()
    {
		super();
    }
    
  	/**
	 * Called every tick. Super this!
	 */
	@Override
	public void onUpdate(float watts, float voltage, ForgeDirection side)
	{
		if(!this.worldObj.isRemote)
		{
			if(this.firstUpdate)
			{
				this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.getBlockType().blockID);
				this.firstUpdate = false;
			}
			
			super.onUpdate(watts, voltage, side);
			
			if(!this.isDisabled())
	    	{
	    		float rejectedElectricity = Math.max((this.dian + watts) - this.YAO_DIAN, 0);
	    		this.dian = Math.max(this.dian+watts - rejectedElectricity, 0);
	
	    		if(this.dian >= this.YAO_DIAN)
	    		{
	    			boolean isDetectThisCheck = false;
	    			
					AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(this.xCoord - minCoord.x, this.yCoord - minCoord.y, this.zCoord - minCoord.z, this.xCoord + maxCoord.x+1D, this.yCoord + maxCoord.y+1D, this.zCoord + maxCoord.z+1D);
					List<EntityLiving> entitiesNearby = worldObj.getEntitiesWithinAABB(EntityLiving.class, bounds);
					
					for(EntityLiving entity : entitiesNearby)
					{
						if(entity instanceof EntityPlayer && (this.mode == 0 || this.mode == 1))
						{			
							boolean gotDisrupter = false;
							
							for(ItemStack inventory : ((EntityPlayer)entity).inventory.mainInventory)
							{
								if(inventory != null)
								{
									if(inventory.getItem() instanceof ItHuoLuanQi)
									{
										if(((ItHuoLuanQi)inventory.getItem()).getFrequency(inventory) == this.frequency)
										{
											gotDisrupter = true;
											break;
										}
									}
								}
							}
							
							if(gotDisrupter)
							{
								continue;
							}
							
							isDetectThisCheck = true;
						}
						else if(!(entity instanceof EntityPlayer) && (this.mode == 0 || this.mode == 2))
						{
							isDetectThisCheck = true;
							break;
						}
					}
	
					if(isDetectThisCheck != isDetect)
					{
						isDetect = isDetectThisCheck;
						this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.getBlockType().blockID);
					}
	    		}
	    		else
	    		{
	    			if(isDetect)
	    			{
						this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.getBlockType().blockID);
	    			}
	    			
	    			isDetect = false;
	    		}
	    		
	    		if(this.isGUIOpen)
				{
					PacketManager.sendTileEntityPacketWithRange(this, "ICBM", 15, (int)1, this.prevElectricityStored, this.frequency, this.mode, this.minCoord.x, this.minCoord.y, this.minCoord.z, this.maxCoord.x, this.maxCoord.y, this.maxCoord.z);
				}
	    		
	    		this.prevElectricityStored = this.dian;
				this.dian = 0;
	    	}
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
				this.prevElectricityStored = dataStream.readFloat();
	            this.frequency = dataStream.readShort();
	            this.mode = dataStream.readByte();
	            this.minCoord = new Vector3(dataStream.readDouble(), dataStream.readDouble(), dataStream.readDouble());
	            this.maxCoord = new Vector3(dataStream.readDouble(), dataStream.readDouble(), dataStream.readDouble());
			}
			else if(ID == 2)
			{
				this.mode = dataStream.readByte();
			}
			else if(ID == 3)
			{
				this.frequency = dataStream.readShort();
			}
			else if(ID == 4)
			{
	            this.minCoord = new Vector3(dataStream.readDouble(), dataStream.readDouble(), dataStream.readDouble());
			}
			else if(ID == 5)
			{
	            this.maxCoord = new Vector3(dataStream.readDouble(), dataStream.readDouble(), dataStream.readDouble());
			}
			else if(ID == 6)
			{
				this.isGUIOpen = dataStream.readBoolean();
			}
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
	}
	@Override
	public float ampRequest()
	{
		return this.YAO_DIAN-this.dian;
	}

	@Override
	public boolean canReceiveFromSide(ForgeDirection side)
	{
		return true;
	}

	@Override
	public float getVoltage()
	{
		return 120F;
	}
	
	/**
     * Reads a tile entity from NBT.
     */
    @Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
    	super.readFromNBT(par1NBTTagCompound);
    	
    	this.mode = par1NBTTagCompound.getByte("mode");
    	this.frequency = par1NBTTagCompound.getShort("frequency");
    	this.minCoord = Vector3.readFromNBT("minCoord", par1NBTTagCompound);
    	this.maxCoord = Vector3.readFromNBT("maxCoord", par1NBTTagCompound);
    }

    /**
     * Writes a tile entity to NBT.
     */
    @Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
    	super.writeToNBT(par1NBTTagCompound);

    	par1NBTTagCompound.setShort("frequency", this.frequency);
    	par1NBTTagCompound.setByte("mode", this.mode);
    	this.minCoord.writeToNBT("minCoord", par1NBTTagCompound);
    	this.maxCoord.writeToNBT("maxCoord", par1NBTTagCompound);
    }

	@Override
	public boolean isPoweringTo(byte side)
	{
		return isDetect;
	}

	@Override
	public boolean isIndirectlyPoweringTo(byte side)
	{
		return isDetect;
	}

	/**
	 * How many world ticks there should be before this tile entity gets ticked?
	 * E.x Returning "1" will make this tile entity tick every single tick.
	 * @return - The tick intervals. Returns 0 if you wish it to not tick at all.
	 */
	@Override
	public int getTickInterval()
	{
		return 10;
	}
}
