package icbm.dianqi;

import icbm.api.ICBM;

import java.lang.reflect.Method;
import java.util.List;

import net.minecraft.src.Entity;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.World;
import net.minecraft.src.WorldClient;
import net.minecraft.src.WorldServer;
import universalelectricity.prefab.ItemElectric;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;

public class ItGenZongQi extends ItemElectric
{
	private static final float YONG_DIAN_LIANG = 0.05f;

    public ItGenZongQi(String name, int id, int iconIndex)
    {
        super(id);
        this.setIconIndex(iconIndex);
        this.setItemName(name);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack itemStack, List par2List)
    {
	    super.addInformation(itemStack, par2List);
	    
        Entity trackingEntity = getTrackingEntityClient((WorldClient)FMLClientHandler.instance().getClient().theWorld, itemStack);
        
        if(trackingEntity != null)
        {
        	par2List.add("Tracking: "+trackingEntity.getEntityName());
        }
    }
    
    public static void setTrackingEntity(ItemStack itemStack, Entity entity)
    {
    	if(itemStack.stackTagCompound == null)
        {
            itemStack.setTagCompound(new NBTTagCompound());
        }
    	
    	if(entity != null)
    	{
    		itemStack.stackTagCompound.setInteger("trackingEntity", entity.entityId);
    	}
    }
    
    @SideOnly(Side.CLIENT)
    public static Entity getTrackingEntityClient(World worldObj, ItemStack itemStack)
    {
    	if(worldObj != null)
    	{
    		if(itemStack.stackTagCompound != null)
            {
                int trackingID = itemStack.stackTagCompound.getInteger("trackingEntity");
                
                try
                {
                	return ((WorldClient)worldObj).getEntityByID(trackingID);
                }
                catch(Exception e)
                {
                	//e.printStackTrace();
                }
            }
        	
    	}
    	
    	return null;
    }
    
    @SideOnly(Side.SERVER)
    public static Entity getTrackingEntityServer(World worldObj, ItemStack itemStack)
    {
    	if(worldObj != null)
    	{
    		if(itemStack.stackTagCompound != null)
            {
                int trackingID = itemStack.stackTagCompound.getInteger("trackingEntity");
                
                try
                {
                	return ((WorldServer)worldObj).getEntityByID(trackingID);
                }
                catch(Exception e)
                {
                	//e.printStackTrace();
                }
            }
        	
    	}
    	
    	return null;
    }
    
    @Override
    public void onCreated(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) 
    {
    	super.onCreated(par1ItemStack, par2World, par3EntityPlayer);
    	setTrackingEntity(par1ItemStack, par3EntityPlayer);
    }
    
    @Override
    public void onUpdate(ItemStack par1ItemStack, World par2World, Entity par3Entity, int par4, boolean par5)
    {
    	super.onUpdate(par1ItemStack, par2World, par3Entity, par4, par5);
    	
    	if(par3Entity instanceof EntityPlayer)
    	{
    		EntityPlayer player = (EntityPlayer)par3Entity;
    		
    		if(player.inventory.getCurrentItem() != null)
    		{
	    		if(player.inventory.getCurrentItem().itemID == this.shiftedIndex)
	    		{
	    			Entity trackingEntity = null;

	    			try
	    			{
	    				try
		    			{
		    				Method m = ItGenZongQi.class.getMethod("getTrackingEntityServer", World.class, ItemStack.class);
		    				
		    				trackingEntity = (Entity) m.invoke(this, par2World, par1ItemStack);
		    			}
		    			catch(Exception e)
		    			{
		    				Method m = ItGenZongQi.class.getMethod("getTrackingEntityClient", World.class, ItemStack.class);
		    				
		    				trackingEntity = (Entity) m.invoke(this, par2World, par1ItemStack);
		    			}
	    			}
	    			catch(Exception e)
	    			{
	    				System.out.println("Failed to find method for tracker.");
	    			}

    				if(trackingEntity != null)
	    			{
	    				this.onUse(YONG_DIAN_LIANG, par1ItemStack);
	    				
	    				if(this.getJoules(par1ItemStack) < YONG_DIAN_LIANG)
	    				{
	    					this.setTrackingEntity(par1ItemStack, null);
	    				}
	    			}
	    		}
    		}
    	}
    }
    
    /**
     * Called when the player Left Clicks (attacks) an entity.
     * Processed before damage is done, if return value is true further processing is canceled
     * and the entity is not attacked.
     * 
     * @param itemStack The Item being used
     * @param player The player that is attacking
     * @param entity The entity being attacked
     * @return True to cancel the rest of the interaction.
     */
    public boolean onLeftClickEntity(ItemStack itemStack, EntityPlayer player, Entity entity) 
    {
    	if(!player.worldObj.isRemote)
    	{
	    	if(this.getJoules(itemStack) > YONG_DIAN_LIANG)
	    	{
	    		setTrackingEntity(itemStack, entity);
	            player.addChatMessage("Now tracking: "+entity.getEntityName());
	    		return true;
	    	}
	    	else
	    	{
	        	player.addChatMessage("Tracker out of electricity!");
	    	}
    	}
    	
    	return false;
    }

	@Override
	public double getVoltage()
	{
		return 20;
	}

	@Override
	public String getTextureFile()
	{
		return ICBM.TRACKER_TEXTURE_FILE;
	}

	@Override
	public double getMaxJoules()
	{
		return 100000;
	}
}
