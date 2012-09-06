package icbm.electronics;

import icbm.ICBM;
import icbm.ICBMPacketManager;
import icbm.LauncherManager;
import icbm.extend.IItemFrequency;
import icbm.extend.TileEntityLauncher;
import icbm.machines.TileEntityCruiseLauncher;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.src.Entity;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EnumMovingObjectType;
import net.minecraft.src.ItemStack;
import net.minecraft.src.MovingObjectPosition;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import universalelectricity.Vector2;
import universalelectricity.Vector3;
import universalelectricity.extend.ItemElectric;
import universalelectricity.network.PacketManager;

public class ItemLaserDesignator extends ItemElectric implements IItemFrequency
{
	public static final int RANGE = 500;
	public static final int ELECTRICITY_CAPACITY = 250;
	
    public ItemLaserDesignator(String name, int id, int icon)
    {
        super(id);
        this.setIconIndex(icon);
        this.setItemName(name);
    }
    
    /**
     * Allows items to add custom lines of information to the mouseover description
     */
    @Override
	public void addInformation(ItemStack par1ItemStack, List par2List)
    {
    	super.addInformation(par1ItemStack, par2List);
    	
    	if(this.getFrequency(par1ItemStack) > 0)
    	{
    		par2List.add("Frequency: "+getFrequency(par1ItemStack));
    	}
    	else
    	{
    		par2List.add("Frequency: Not Set");
    	}
    }
    
    @Override
    public short getFrequency(ItemStack par1ItemStack)
    {
    	if (par1ItemStack.stackTagCompound == null)
		{
    		return 0;
		}
    	return par1ItemStack.stackTagCompound.getShort("frequency");		
    }

    @Override
	public void setFrequency(ItemStack par1ItemStack, short frequency)
	{
		if (par1ItemStack.stackTagCompound == null)
		{
			 par1ItemStack.setTagCompound(new NBTTagCompound());
		}

		par1ItemStack.stackTagCompound.setShort("frequency", frequency);
	}
    
    public int getLauncherCountDown(ItemStack par1ItemStack)
    {
    	if(par1ItemStack.stackTagCompound == null)
		{
    		return -1;
		}
    	
    	return par1ItemStack.stackTagCompound.getInteger("countDown");		
    }

    public void setLauncherCountDown(ItemStack par1ItemStack, int value)
	{
		if (par1ItemStack.stackTagCompound == null)
		{
			 par1ItemStack.setTagCompound(new NBTTagCompound());
		}

		par1ItemStack.stackTagCompound.setInteger("countDown", value);
	}
	
	public int getLauncherCount(ItemStack par1ItemStack)
    {
    	if (par1ItemStack.stackTagCompound == null)
		{
    		return 0;
		}
    	return par1ItemStack.stackTagCompound.getInteger("launcherCount");		
    }

	public void setLauncherCount(ItemStack par1ItemStack, int value)
	{
		if (par1ItemStack.stackTagCompound == null)
		{
			 par1ItemStack.setTagCompound(new NBTTagCompound());
		}

		par1ItemStack.stackTagCompound.setInteger("launcherCount", value);
	}
	
	public int getLauncherDelay(ItemStack par1ItemStack)
    {
    	if (par1ItemStack.stackTagCompound == null)
		{
    		return 0;
		}
    	return par1ItemStack.stackTagCompound.getInteger("launcherDelay");		
    }
	
	public void setLauncherDelay(ItemStack par1ItemStack, int value)
	{
		if (par1ItemStack.stackTagCompound == null)
		{
			 par1ItemStack.setTagCompound(new NBTTagCompound());
		}

		par1ItemStack.stackTagCompound.setInteger("launcherDelay", value);
	}
	
	/**
     * Called each tick as long the item is on a player inventory. Uses by maps to check if is on a player hand and
     * update it's contents.
     */
    @Override
	public void onUpdate(ItemStack par1ItemStack, World par2World, Entity par3Entity, int par4, boolean par5)
    {
    	if(!par2World.isRemote)
    	{
	    	List<TileEntityLauncher> connectedLaunchers = new ArrayList<TileEntityLauncher>();
	    	
	    	if(this.getLauncherCountDown(par1ItemStack) > 0 || this.getLauncherCount(par1ItemStack) > 0)
	    	{
	    		Vector3 position = new Vector3(par3Entity.posX, par3Entity.posY, par3Entity.posZ);
	        	List<TileEntityLauncher> launchers = LauncherManager.getLaunchersInArea(new Vector2(position.x - this.RANGE, position.z - this.RANGE), new Vector2(position.x + this.RANGE, position.z + this.RANGE));
	        	
	        	for(TileEntityLauncher missileLauncher : launchers)
	        	{
	        		 if(missileLauncher != null && missileLauncher.getFrequency() == this.getFrequency(par1ItemStack))
	        		 {
	        			 if(missileLauncher.canLaunch())
	        			 {
	        				 connectedLaunchers.add(missileLauncher);
	        			 }
	        		 }
	        	}
	    	}
	    	
	    	if(this.getLauncherCountDown(par1ItemStack) > 0 && connectedLaunchers.size() > 0)
	    	{
	    		if(this.getLauncherCountDown(par1ItemStack) % 20 == 0)
	    		{
	    			((EntityPlayer)par3Entity).addChatMessage("Calling air strike in: "+(int)Math.floor(this.getLauncherCountDown(par1ItemStack)/20));
	    		}
	    		
	    		if(this.getLauncherCountDown(par1ItemStack) == 1)
	    		{
	    			this.setLauncherCount(par1ItemStack, connectedLaunchers.size());
	    			this.setLauncherDelay(par1ItemStack, 0);
	        		((EntityPlayer)par3Entity).addChatMessage("Incoming air strike!");
	    		}
	    		
	    		this.setLauncherCountDown(par1ItemStack, this.getLauncherCountDown(par1ItemStack)-1);
	    	}
	    	
	    	if(this.getLauncherCount(par1ItemStack) > 0 && connectedLaunchers.size() > 0)
	    	{
	    		//Launch a missile every two seconds from different launchers
	    		if(this.getLauncherDelay(par1ItemStack) % 40 == 0)
	    		{
	        		connectedLaunchers.get(this.getLauncherCount(par1ItemStack)-1).launch();
	
	        		this.setLauncherCount(par1ItemStack, this.getLauncherCount(par1ItemStack)-1);
	    		}
	    		
	    		if(this.getLauncherCount(par1ItemStack) == 0)
	    		{
	    			this.setLauncherDelay(par1ItemStack, 0);
	    			connectedLaunchers.clear();
	    		}
				
				this.setLauncherDelay(par1ItemStack, this.getLauncherDelay(par1ItemStack)+1);
	    	}
    	}
    }
    
    /**
     * Callback for item usage. If the item does something special on right clicking, he will have one of those. Return
     * True if something happen and false if it don't. This is for ITEMS, not BLOCKS !
     */
    @Override
    public boolean tryPlaceIntoWorld(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int x, int y, int z, int par7, float par8, float par9, float par10)
    {
    	if(!par3World.isRemote)
    	{
	    	//SET FREQUENCY OF REMOTE
	    	TileEntity tileEntity = par3World.getBlockTileEntity(x, y, z);
	    		 
	    	if(tileEntity != null)
	    	{
	    		if(tileEntity instanceof TileEntityLauncher)
	    		{
	    			TileEntityLauncher missileLauncher = (TileEntityLauncher)tileEntity;
	    			
				 	if(missileLauncher.getFrequency() > 0)
				 	{
						this.setFrequency(par1ItemStack, missileLauncher.getFrequency());
						par2EntityPlayer.addChatMessage("Laser designator frequency Set: " + this.getFrequency(par1ItemStack));
					}
					else
					{
						par2EntityPlayer.addChatMessage("Frequency must be greater than zero.");
					}
	    		}
			}
    	}
        
    	return false;
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    @Override
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {	
    	if(par2World.isRemote)
    	{
	    	MovingObjectPosition objectMouseOver = par3EntityPlayer.rayTrace(RANGE*2, 1);
	    	
	    	if(objectMouseOver != null && objectMouseOver.typeOfHit == EnumMovingObjectType.TILE)
	        {
		        //Check for short-fused TNT. If there is a short fused TNT, then blow it up.
		    	int blockId = par2World.getBlockId(objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ);
		    	int blockMetadata = par2World.getBlockMetadata(objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ);
		    
		    	 //Prevents calling air strike if the user is trying to set the frequency of the remote.
		        if(blockId == ICBM.blockMachine.blockID)
		        {
		        	return par1ItemStack;
		        }
		        else
		        {
		        	//Update the airStrikeFrequency
		            short airStrikeFreq = this.getFrequency(par1ItemStack);
		            
		            //Check if it is possible to do an air strike. If so, do one.
		        	if(airStrikeFreq > 0)
		            {
		        		if(this.getElectricityStored(par1ItemStack) > ELECTRICITY_CAPACITY)
		            	{
		    	        	Vector3 position = new Vector3(par3EntityPlayer.posX, par3EntityPlayer.posY, par3EntityPlayer.posZ);
		    	        	List<TileEntityLauncher> launchers = LauncherManager.getLaunchersInArea(new Vector2(position.x - this.RANGE, position.z - this.RANGE), new Vector2(position.x + this.RANGE, position.z + this.RANGE));
		    	        	
		    	        	boolean doAirStrike = false;
		    	        	int errorCount = 0;
		    	        	
		    	        	for(TileEntityLauncher missileLauncher : launchers)
		    	        	{
		    	        		 if(missileLauncher != null && missileLauncher.getFrequency() == airStrikeFreq)
	                    		 {
	                    			 //Preserve and not change the Y value of the missile launcher 
		    	        			 if(missileLauncher instanceof TileEntityCruiseLauncher)
		    	        			 {
		    	        				 missileLauncher.target = new Vector3(objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ);
				        	    		 PacketManager.sendTileEntityPacketToServer(missileLauncher, "ICBM", (int)2, missileLauncher.target.intX(), missileLauncher.target.intY(), missileLauncher.target.intZ());
		    	        			 }
		    	        			 else
		    	        			 {
		    	        				 int previousY = missileLauncher.target.intY();
		    	        				 missileLauncher.target = new Vector3(objectMouseOver.blockX, missileLauncher.target.y, objectMouseOver.blockZ);
				        	    		 PacketManager.sendTileEntityPacketToServer(missileLauncher, "ICBM", (int)2, missileLauncher.target.intX(), previousY, missileLauncher.target.intZ());
		    	        			 }

	                    			 if(missileLauncher.canLaunch())
	                    			 {
	                    				 doAirStrike = true;
	                    			 }
	                    			 else
	                    			 {
	                    				 errorCount ++;
	                    				 par3EntityPlayer.addChatMessage("#"+errorCount+" Missile Launcher Error: "+missileLauncher.getStatus());
	                    			 }
	                    		 }
		    	        	}
	
		    	        	if(doAirStrike && this.getLauncherCountDown(par1ItemStack) >= 0)
		    	        	{
				            	ICBMPacketManager.sendUnspecifiedPacketToServer("ICBM", (int)ICBMPacketManager.LASER_DESIGNATOR_PACKET, objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ);
				            	
		    	        		par3EntityPlayer.addChatMessage("Calling air strike into designated position!");
		    	        	}
		            	}
		        		else
		        		{
		        			par3EntityPlayer.addChatMessage("Laser designator out of electricity!");
		        		}
		            }
		        	else
	        		{
	        			par3EntityPlayer.addChatMessage("Laser designator frequency not set!");
	        		}
		        }
	        }
    	}
    	
    	return par1ItemStack;
    }

	@Override
	public float getVoltage()
	{
		return 20;
	}
    
    @Override
	public float getElectricityCapacity() 
	{
		return 3750;
	}

	@Override
	public float getTransferRate()
	{
		return 25;
	}
	
	@Override
	public String getTextureFile()
    {
        return ICBM.ITEM_TEXTURE_FILE;
    }
}
