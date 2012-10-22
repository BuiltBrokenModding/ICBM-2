package icbm.dianqi;

import icbm.ZhuYao;
import icbm.api.ICBM;
import icbm.cart.EChe;
import icbm.zhapin.EZhaDan;

import java.util.Random;

import net.minecraft.src.Block;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityTNTPrimed;
import net.minecraft.src.ItemStack;
import universalelectricity.prefab.ItemElectric;

//Explosive Defuser
public class ItJieJa extends ItemElectric
{
	private static final int YONG_DIAN_LIANG = 2000;

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
    	if(this.getJoules(stack) > YONG_DIAN_LIANG)
    	{
    		
    		if(entity instanceof EZhaDan)
	    	{
    			if(!entity.worldObj.isRemote)
        		{
		    		EZhaDan entityTNT = (EZhaDan)entity;
		    		EntityItem entityItem = new EntityItem(entity.worldObj, entity.posX, entity.posY, entity.posZ, new ItemStack(ZhuYao.bZha4Dan4, 1, entityTNT.explosiveID));
			        float var13 = 0.05F;
			        Random random = new Random();
			        entityItem.motionX = ((float)random.nextGaussian() * var13);
			        entityItem.motionY = ((float)random.nextGaussian() * var13 + 0.2F);
			        entityItem.motionZ = ((float)random.nextGaussian() * var13);
			        entity.worldObj.spawnEntityInWorld(entityItem);
        		}
	    		entity.setDead();
	        }
    		else if(entity instanceof EntityTNTPrimed)
	    	{
    			if(!entity.worldObj.isRemote)
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
	        }
    		else if(entity instanceof EChe)
	    	{
    			((EChe)entity).setPrimed(false);
    			((EChe)entity).setFuse(-1);
	    	}
    		
    		this.onUse(YONG_DIAN_LIANG, stack);
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
	public double getMaxJoules()
	{
		return 50000;
	}
}
