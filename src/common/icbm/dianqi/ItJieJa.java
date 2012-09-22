package icbm.dianqi;

import icbm.ICBM;
import icbm.zhapin.EZhaDan;

import java.util.Random;

import net.minecraft.src.Block;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityTNTPrimed;
import net.minecraft.src.ItemStack;
import universalelectricity.prefab.ItemElectric;

public class ItJieJa extends ItemElectric
{
	private int electricityConsumption = 150;

    public ItJieJa(String name, int par1, int par2)
    {
        super(par1);
        this.iconIndex = par2;
        this.setItemName(name);
    }
    
    @Override
	public String getTextureFile()
    {
        return ICBM.ITEM_TEXTURE_FILE;
    }
    
    /**
     * Called when the player Left Clicks (attacks) an entity.
     * Processed before damage is done, if return value is true further processing is canceled
     * and the entity is not attacked.
     * 
     * @param stack The Item being used
     * @param player The player that is attacking
     * @param entity The entity being attacked
     * @return True to cancel the rest of the interaction.
     */
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) 
    {
    	if(this.getWattHours(stack) > electricityConsumption)
    	{
    		if(entity instanceof EZhaDan)
	    	{
	    		EZhaDan entityTNT = (EZhaDan)entity;
	    		EntityItem entityItem = new EntityItem(entity.worldObj, entity.posX, entity.posY, entity.posZ, new ItemStack(ICBM.blockZha4Dan4, 1, entityTNT.explosiveID));
		        float var13 = 0.05F;
		        Random random = new Random();
		        entityItem.motionX = ((float)random.nextGaussian() * var13);
		        entityItem.motionY = ((float)random.nextGaussian() * var13 + 0.2F);
		        entityItem.motionZ = ((float)random.nextGaussian() * var13);
		        entity.worldObj.spawnEntityInWorld(entityItem);		        
	        }
    		else if(entity instanceof EntityTNTPrimed)
	    	{
	    		EntityItem entityItem = new EntityItem(entity.worldObj, entity.posX, entity.posY, entity.posZ, new ItemStack(Block.tnt));
		        float var13 = 0.05F;
		        Random random = new Random();
		        entityItem.motionX = ((float)random.nextGaussian() * var13);
		        entityItem.motionY = ((float)random.nextGaussian() * var13 + 0.2F);
		        entityItem.motionZ = ((float)random.nextGaussian() * var13);
		        entity.worldObj.spawnEntityInWorld(entityItem);
	        }
    		
    		entity.setDead();
    		this.onUseElectricity(electricityConsumption, stack);
    		return true;
    	}
    	else
    	{
        	player.addChatMessage("Defuser out of electricity!");
    	}
    	
    	return false;
    }

	@Override
	public double getVoltage()
	{
		return 20;
	}
    
    @Override
	public double getMaxWattHours()
	{
		return 1800;
	}

	@Override
	public double getTransferRate()
	{
		return 25;
	}

}
