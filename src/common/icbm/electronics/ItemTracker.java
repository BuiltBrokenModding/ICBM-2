package icbm.electronics;

import icbm.ICBM;

import java.util.List;

import net.minecraft.src.Entity;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.World;
import net.minecraft.src.WorldClient;
import universalelectricity.extend.ItemElectric;
import cpw.mods.fml.client.FMLClientHandler;

public class ItemTracker extends ItemElectric
{
	private static final int ELECTRICITY_REQUIRED = 100;

    public ItemTracker(String name, int id, int texture)
    {
        super(id);
        this.setIconIndex(texture);
        this.setItemName(name);
    }
    
    @Override
    public void addInformation(ItemStack itemStack, List par2List)
    {
	    super.addInformation(itemStack, par2List);
	    
        Entity trackingEntity = getTrackingEntity(itemStack);
        
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
    
    public static Entity getTrackingEntity(ItemStack itemStack)
    {
    	if(itemStack.stackTagCompound != null)
        {
            int trackingID = itemStack.stackTagCompound.getInteger("trackingEntity");
            return ((WorldClient)FMLClientHandler.instance().getClient().theWorld).getEntityByID(trackingID);
        }
    	
    	return null;
    }

    
    @Override
	public String getTextureFile()
    {
        return ICBM.ITEM_TEXTURE_FILE;
    }
    
    @Override
    public void onCreated(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) 
    {
    	setTrackingEntity(par1ItemStack, par3EntityPlayer);
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
	    	if(this.getElectricityStored(itemStack) > ELECTRICITY_REQUIRED)
	    	{
	    		setTrackingEntity(itemStack, entity);
	            player.addChatMessage("Now tracking: "+entity.getEntityName());
	    		this.onUseElectricity(ELECTRICITY_REQUIRED, itemStack);
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
	public float getVoltage()
	{
		return 20;
	}
    
    @Override
	public float getElectricityCapacity() 
	{
		return 2000;
	}

	@Override
	public float getTransferRate()
	{
		return 25;
	}

}
