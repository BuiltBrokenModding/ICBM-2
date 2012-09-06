package icbm.electronics;

import icbm.EntityExplosive;
import icbm.ICBM;

import java.util.Random;

import net.minecraft.src.Block;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityTNTPrimed;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import universalelectricity.extend.ItemElectric;

public class ItemTracker extends ItemElectric
{
	private int electricityConsumption = 150;

    public ItemTracker(String name, int id, int texture)
    {
        super(id);
        this.setIconIndex(texture);
        this.setIconCoord(6, 3);
        this.setItemName(name);
    }
    /*
    @Override
	public String getTextureFile()
    {
        return ICBM.ITEM_TEXTURE_FILE;
    }*/
    
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
	    	if(this.getElectricityStored(itemStack) > electricityConsumption)
	    	{
	    		if (itemStack.stackTagCompound == null)
	            {
	                itemStack.setTagCompound(new NBTTagCompound());
	            }
	
	            itemStack.stackTagCompound.setInteger("trackingEntity", entity.entityId);
	            player.addChatMessage("Now tracking: "+entity.getEntityName());
	    		this.onUseElectricity(electricityConsumption, itemStack);
	    		return true;
	    	}
	    	else
	    	{
	        	player.addChatMessage("Defuser out of electricity!");
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
