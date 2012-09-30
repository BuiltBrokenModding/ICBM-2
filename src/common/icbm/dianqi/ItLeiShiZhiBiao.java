package icbm.dianqi;

import icbm.ICBM;
import icbm.ICBMPacketManager.ICBMPacketType;
import icbm.api.IFrequency;
import icbm.extend.TFaSheQi;
import icbm.jiqi.FaSheQiGuanLi;
import icbm.jiqi.TXiaoFaSheQi;

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
import universalelectricity.network.PacketManager;
import universalelectricity.prefab.ItemElectric;
import universalelectricity.prefab.Vector2;
import universalelectricity.prefab.Vector3;
import cpw.mods.fml.common.network.PacketDispatcher;

public class ItLeiShiZhiBiao extends ItemElectric implements IFrequency
{
	public static final int BAN_JING = 500;
	public static final int ELECTRICITY_CAPACITY = 20;
	
    public ItLeiShiZhiBiao(String name, int id, int icon)
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
    public short getFrequency(Object... data)
    {
    	ItemStack itemStack = (ItemStack)data[0];
    	
    	if (itemStack.stackTagCompound == null)
		{
    		return 0;
		}
    	return itemStack.stackTagCompound.getShort("frequency");		
    }

    @Override
	public void setFrequency(short frequency, Object... data)
	{
		ItemStack itemStack = (ItemStack)data[0];
		
		if (itemStack.stackTagCompound == null)
		{
			 itemStack.setTagCompound(new NBTTagCompound());
		}

		itemStack.stackTagCompound.setShort("frequency", frequency);
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
    	super.onUpdate(par1ItemStack, par2World, par3Entity, par4, par5);
    	
    	if(!par2World.isRemote)
    	{
	    	List<TFaSheQi> connectedLaunchers = new ArrayList<TFaSheQi>();
	    	
	    	if(this.getLauncherCountDown(par1ItemStack) > 0 || this.getLauncherCount(par1ItemStack) > 0)
	    	{
	    		Vector3 position = new Vector3(par3Entity.posX, par3Entity.posY, par3Entity.posZ);
	        	List<TFaSheQi> launchers = FaSheQiGuanLi.naFaSheQiInArea(new Vector2(position.x - this.BAN_JING, position.z - this.BAN_JING), new Vector2(position.x + this.BAN_JING, position.z + this.BAN_JING));
	        	
	        	for(TFaSheQi missileLauncher : launchers)
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
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int x, int y, int z, int par7, float par8, float par9, float par10)
    {
    	if(!par3World.isRemote)
    	{
	    	//SET FREQUENCY OF REMOTE
	    	TileEntity tileEntity = par3World.getBlockTileEntity(x, y, z);
	    		 
	    	if(tileEntity != null)
	    	{
	    		if(tileEntity instanceof TFaSheQi)
	    		{
	    			TFaSheQi missileLauncher = (TFaSheQi)tileEntity;
	    			
				 	if(missileLauncher.getFrequency() > 0)
				 	{
						this.setFrequency(missileLauncher.getFrequency(), par1ItemStack);
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
	    	MovingObjectPosition objectMouseOver = par3EntityPlayer.rayTrace(BAN_JING*2, 1);
	    	
	    	if(objectMouseOver != null && objectMouseOver.typeOfHit == EnumMovingObjectType.TILE)
	        {
		        //Check for short-fused TNT. If there is a short fused TNT, then blow it up.
		    	int blockId = par2World.getBlockId(objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ);
		    	int blockMetadata = par2World.getBlockMetadata(objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ);
		    
		    	if(this.getLauncherCountDown(par1ItemStack) > 0)
		    	{
		    		return par1ItemStack;
		    	}
		    	
		    	//Prevents calling air strike if the user is trying to set the frequency of the remote.
		        if(blockId == ICBM.blockJiQi.blockID)
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
		        		if(this.getWattHours(par1ItemStack) > ELECTRICITY_CAPACITY)
		            	{
		    	        	Vector3 position = new Vector3(par3EntityPlayer.posX, par3EntityPlayer.posY, par3EntityPlayer.posZ);
		    	        	List<TFaSheQi> launchers = FaSheQiGuanLi.naFaSheQiInArea(new Vector2(position.x - this.BAN_JING, position.z - this.BAN_JING), new Vector2(position.x + this.BAN_JING, position.z + this.BAN_JING));
		    	        	
		    	        	boolean doAirStrike = false;
		    	        	int errorCount = 0;
		    	        	
		    	        	for(TFaSheQi missileLauncher : launchers)
		    	        	{
		    	        		 if(missileLauncher != null && missileLauncher.getFrequency() == airStrikeFreq)
	                    		 {
		    	        			 if(missileLauncher instanceof TXiaoFaSheQi)
		    	        			 {
		    	        				 missileLauncher.setTarget(new Vector3(objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ));
		    	        				 PacketDispatcher.sendPacketToServer(PacketManager.getPacket("ICBM", missileLauncher, (int)2, missileLauncher.getTarget().x, missileLauncher.getTarget().y, missileLauncher.getTarget().z));
		    	        			 }
		    	        			 else
		    	        			 {
		    	        				 
		    	        				 double previousY = 0;
		    	        				 
		    	        				 if(missileLauncher.getTarget() != null)
		    	        				 {
		    	        					 previousY = missileLauncher.getTarget().y;
		    	        				 }
		    	        				 
		    	        				 missileLauncher.setTarget(new Vector3(objectMouseOver.blockX, previousY, objectMouseOver.blockZ));
		    	        				 PacketDispatcher.sendPacketToServer(PacketManager.getPacket("ICBM", missileLauncher, (int)2, missileLauncher.getTarget().x, missileLauncher.getTarget().y, missileLauncher.getTarget().z));
		    	        			 }

	                    			 if(missileLauncher.canLaunch())
	                    			 {
	                    				 doAirStrike = true;
	                    			 }
	                    			 else
	                    			 {
	                    				 errorCount ++;
	                    				 //par3EntityPlayer.addChatMessage("#"+errorCount+" Missile Launcher Error: "+missileLauncher.getStatus());
	                    			 }
	                    		 }
		    	        	}
	
		    	        	if(doAirStrike && this.getLauncherCountDown(par1ItemStack) >= 0)
		    	        	{
		    	        		PacketDispatcher.sendPacketToServer(PacketManager.getPacketWithID(ICBM.CHANNEL, (int)ICBMPacketType.LASER_DESIGNATOR.ordinal(), objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ));
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
	public double getVoltage()
	{
		return 20;
	}
    
    @Override
	public double getMaxWattHours()
	{
		return 350;
	}

	@Override
	public double getTransferRate()
	{
		return 0.5;
	}
	
	@Override
	public String getTextureFile()
    {
        return ICBM.ITEM_TEXTURE_FILE;
    }
}
