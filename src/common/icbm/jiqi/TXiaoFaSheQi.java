package icbm.jiqi;

import icbm.daodan.DaoDan;
import icbm.daodan.EDaoDan;
import icbm.daodan.ItDaoDan;
import icbm.daodan.ItTe4Bie2Dao3Dan4;
import icbm.extend.TLauncher;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagList;
import net.minecraft.src.NetworkManager;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ISidedInventory;
import universalelectricity.Vector3;
import universalelectricity.extend.IElectricityStorage;
import universalelectricity.extend.IRedstoneReceptor;
import universalelectricity.network.IPacketReceiver;
import universalelectricity.network.PacketManager;

import com.google.common.io.ByteArrayDataInput;

public class TXiaoFaSheQi extends TLauncher implements IElectricityStorage, IPacketReceiver, IInventory, ISidedInventory, IRedstoneReceptor
{
    //The missile that this launcher is holding
    public EDaoDan containingMissile = null;
    
    public float rotationYaw = 0;
    
    public float rotationPitch = 0;
    
    /**
     * The ItemStacks that hold the items currently being used in the missileLauncher
     */
    private ItemStack[] containingItems = new ItemStack[1];

	public short frequency = 0;

	private boolean isPowered = false;

	private float wattHoursStored = 0;
    
	public TXiaoFaSheQi()
	{
		super();
		this.target = new Vector3();
	}
	
    /**
     * Returns the number of slots in the inventory.
     */
	@Override
    public int getSizeInventory()
    {
        return this.containingItems.length;
    }

    /**
     * Returns the stack in slot i
     */
	@Override
    public ItemStack getStackInSlot(int par1)
    {
        return this.containingItems[par1];
    }

    /**
     * Decrease the size of the stack in slot (first int arg) by the amount of the second int arg. Returns the new
     * stack.
     */
	@Override
    public ItemStack decrStackSize(int par1, int par2)
    {		
        if (this.containingItems[par1] != null)
        {
            ItemStack var3;

            if (this.containingItems[par1].stackSize <= par2)
            {
                var3 = this.containingItems[par1];
                this.containingItems[par1] = null;
                return var3;
            }
            else
            {
                var3 = this.containingItems[par1].splitStack(par2);

                if (this.containingItems[par1].stackSize == 0)
                {
                    this.containingItems[par1] = null;
                }

                return var3;
            }
        }
        else
        {
            return null;
        }
    }

    /**
     * When some containers are closed they call this on each slot, then drop whatever it returns as an EntityItem -
     * like when you close a workbench GUI.
     */
	@Override
    public ItemStack getStackInSlotOnClosing(int par1)
    {
        if (this.containingItems[par1] != null)
        {
            ItemStack var2 = this.containingItems[par1];
            this.containingItems[par1] = null;
            return var2;
        }
        else
        {
            return null;
        }
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
	@Override
    public void setInventorySlotContents(int par1, ItemStack par2ItemStack)
    {
        this.containingItems[par1] = par2ItemStack;

        if (par2ItemStack != null && par2ItemStack.stackSize > this.getInventoryStackLimit())
        {
            par2ItemStack.stackSize = this.getInventoryStackLimit();
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
        else if(this.wattHoursStored < this.getMaxWattHours())
    	{
    		status = "No Power!";
    	}
        else if(this.containingMissile == null)
    	{
    		status = "Silo Empty!";
    	}
        else if(this.target == null)
        {
        	status = "Invalid Target!";
        }
        else
    	{
    		color = "\u00a72";
    		status = "Ready!";
    	}
    	
    	return color+status;
    }

    /**
     * Returns the name of the inventory.
     */
	@Override
    public String getInvName()
    {
        return "Cruise Launcher";
    }

    @Override
	public void onUpdate(float amps, float voltage, ForgeDirection side)
    {
    	if(!this.worldObj.isRemote)
	    {
	    	super.onUpdate(amps, voltage, side);
	    	
	    	this.wattHoursStored += amps;
	    	
	    	//Rotate the yaw
			if(this.getYawFromTarget() - this.rotationYaw != 0)
			{
				this.rotationYaw += (this.getYawFromTarget() - this.rotationYaw)*0.1;
			}
			if(this.getPitchFromTarget() - this.rotationPitch != 0)
			{
				this.rotationPitch += (this.getPitchFromTarget() - this.rotationPitch)*0.1;
			}
	    	
	    	if (this.containingItems[0] != null)
	        {
	            if (this.containingItems[0].getItem() instanceof ItDaoDan)
	            {
	                int missileId = this.containingItems[0].getItemDamage();
	
	            	if(!(this.containingItems[0].getItem() instanceof ItTe4Bie2Dao3Dan4) && DaoDan.list[missileId].isCruise() && DaoDan.list[missileId].getTier() <= 3 && containingMissile == null)
	            	{
	        			Vector3 startingPosition = new Vector3((this.xCoord+0.5f), (this.yCoord+0.2f), (this.zCoord+0.5f));
	                    this.containingMissile = new EDaoDan(this.worldObj, startingPosition, Vector3.get(this), missileId);
	                    this.worldObj.spawnEntityInWorld(this.containingMissile);
	            	}
	            	else if(this.containingMissile != null && this.containingMissile.missileID !=  missileId)
	            	{
	            		if(this.containingMissile != null) this.containingMissile.setDead();
	            		this.containingMissile = null;
	            	}
	            }
	            else
	        	{
	            	if(this.containingMissile != null) this.containingMissile.setDead();
	        		this.containingMissile = null;
	        	}
	        }
	    	else
	    	{
	    		if(this.containingMissile != null) this.containingMissile.setDead();
	    		this.containingMissile = null;
	    	}
	    	
	    	if(this.isPowered)
			{
	    		if(canLaunch())
	    		{
	    			this.launch();
	    		}
	    		
				this.isPowered = false;
			}
	    	
	    	if(this.target == null) this.target = new Vector3(this.xCoord, this.yCoord, this.zCoord);
			PacketManager.sendTileEntityPacket(this, "ICBM", (int)0, this.wattHoursStored, this.frequency, this.disabledTicks, this.rotationYaw, this.rotationPitch, this.target.x, this.target.y, this.target.z);
    	}
    }
    
    @Override
	public void handlePacketData(NetworkManager network, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput dataStream)
	{
		try
        {
			int ID = dataStream.readInt();
			
			if(ID == 0)
			{
				this.wattHoursStored = dataStream.readFloat();
	            this.frequency = dataStream.readShort();
	            this.disabledTicks = dataStream.readInt();
	            this.rotationYaw = dataStream.readFloat();
	            this.rotationPitch = dataStream.readFloat();
	            
				this.target = new Vector3(dataStream.readDouble(), dataStream.readDouble(), dataStream.readDouble());
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
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
	}
    
    private float getPitchFromTarget()
    {
    	double distance = Math.sqrt((this.target.x - this.xCoord)*(this.target.x - this.xCoord) + (this.target.z - this.zCoord)*(this.target.z - this.zCoord));
    	return (float)Math.toDegrees(Math.atan((this.target.y - (this.yCoord + 0.5F))/distance));
	}

	private float getYawFromTarget()
    {
		double xDifference = this.target.x - (double)((float)this.xCoord + 0.5F);
        double yDifference = this.target.z - (double)((float)this.zCoord + 0.5F);
        return (float)Math.toDegrees(Math.atan2(yDifference, xDifference));
	}

	@Override
	public boolean canLaunch()
	{
		if(this.containingMissile != null && this.containingItems[0] != null)
		{
			DaoDan missile = DaoDan.list[this.containingItems[0].getItemDamage()];
			
			if(missile != null)
			{
				if(!(this.containingItems[0].getItem() instanceof ItTe4Bie2Dao3Dan4) && missile.isCruise() && missile.getTier() <= 3)
		        {
		    		if(this.wattHoursStored >= this.getMaxWattHours())
		    		{
		    			if(!this.isTooClose(this.target))
		    			{
		    				return true;
		    			}
		    		}
		        }
			}
		}
		
		return false;
	}
	
	/**
     * Launches the missile
     * @param target - The target in which the missile will land in
     */
    public void launch()
    {
    	if(canLaunch())
    	{
			this.decrStackSize(0, 1);
			this.setWattHours(0);
	        this.containingMissile.launchMissile(this.target);
	        this.containingMissile = null;    
    	}
    }
    
    //Is the target too close?
    public boolean isTooClose(Vector3 target)
    {
    	//Check if it is greater than the minimum range
		if(Vector3.distance(new Vector3(this.xCoord, 0, this.zCoord), new Vector3(target.x, 0, target.z)) < 8)
		{
			return true;
		}
		
		return false;
    }
    
    /**
     * Reads a tile entity from NBT.
     */
    @Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
    	super.readFromNBT(par1NBTTagCompound);
    	
    	this.frequency = par1NBTTagCompound.getShort("frequency");
    	this.target = Vector3.readFromNBT("target", par1NBTTagCompound);
    	
    	NBTTagList var2 = par1NBTTagCompound.getTagList("Items");
    	
        this.containingItems = new ItemStack[this.getSizeInventory()];
        this.wattHoursStored = par1NBTTagCompound.getFloat("electricityStored");

        for (int var3 = 0; var3 < var2.tagCount(); ++var3)
        {
            NBTTagCompound var4 = (NBTTagCompound)var2.tagAt(var3);
            byte var5 = var4.getByte("Slot");

            if (var5 >= 0 && var5 < this.containingItems.length)
            {
                this.containingItems[var5] = ItemStack.loadItemStackFromNBT(var4);
            }
        }
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
    	
    	par1NBTTagCompound.setShort("frequency", this.frequency);
    	par1NBTTagCompound.setFloat("electricityStored", this.wattHoursStored);
    	
    	NBTTagList var2 = new NBTTagList();

        for (int var3 = 0; var3 < this.containingItems.length; ++var3)
        {
            if (this.containingItems[var3] != null)
            {
                NBTTagCompound var4 = new NBTTagCompound();
                var4.setByte("Slot", (byte)var3);
                this.containingItems[var3].writeToNBT(var4);
                var2.appendTag(var4);
            }
        }
        
        par1NBTTagCompound.setTag("Items", var2);
    }

    /**
     * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended. *Isn't
     * this more of a set than a get?*
     */
    @Override
	public int getInventoryStackLimit()
    {
        return 1;
    }

    /**
     * Do not make give this method the name canInteractWith because it clashes with Container
     */
    @Override
	public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer)
    {
        return this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : par1EntityPlayer.getDistanceSq(this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D) <= 64.0D;
    }

    @Override
	public void openChest() {}

    @Override
	public void closeChest() {}

    @Override
    public int getStartInventorySide(ForgeDirection side)
    {
        return 0;
    }

    @Override
    public int getSizeInventorySide(ForgeDirection side)
    {
        return 1;
    }

	@Override
	public void onPowerOn() 
	{
		this.isPowered = true;
	}

	@Override
	public void onPowerOff()
	{
		this.isPowered  = false;
	}

	@Override
	public float ampRequest()
	{
		return this.getMaxWattHours() - this.wattHoursStored;
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
	public float getWattHours()
	{
		return this.wattHoursStored;
	}

	@Override
	public void setWattHours(float wattHours)
	{
		this.wattHoursStored = Math.max(Math.min(wattHours, this.getMaxWattHours()), 0);
	}

	@Override
	public float getMaxWattHours() 
	{
		return 12000;
	}
}
