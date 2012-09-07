package icbm.explosives;

import icbm.ICBM;

import java.util.List;

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.Block;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityPig;
import net.minecraft.src.EntityPigZombie;
import net.minecraft.src.EntityVillager;
import net.minecraft.src.EntityZombie;
import net.minecraft.src.MathHelper;
import net.minecraft.src.World;
import universalelectricity.Vector3;

public class ExplosiveDecayLand extends Explosive
{
	public ExplosiveDecayLand(String name, int ID, int tier)
	{
		super(name, ID, tier);
	}

	@Override
	public boolean doExplosion(World worldObj, Vector3 position, Entity explosionSource, int radius, int callCount)
	{
		for(int x = -radius; x < radius; x++)
        {
     	   for(int y = -radius; y < radius; y++)
     	   {
     		   	for(int z = -radius; z < radius; z++)
     		   	{
	     		   	double dist = MathHelper.sqrt_double((x*x + y*y + z*z));
	     		   	
					if(dist > radius) continue;
					
     		   		Vector3 blockPosition = new Vector3(x ,y ,z);
     		   		blockPosition.add(position);	   	
					
     		   		//Check what type of block this is. Decay the land depending on the block type.
     		   		int blockID = worldObj.getBlockId((int)blockPosition.x, (int)blockPosition.y, (int)blockPosition.z);
         		   	if (blockID == Block.grass.blockID || blockID == Block.sand.blockID)
                    {
	     	           if(Math.random()*100 > 75)
	     	           {
	     	        	   worldObj.setBlockWithNotify((int)blockPosition.x, (int)blockPosition.y, (int)blockPosition.z, ICBM.blockRadioactive.blockID);
	     	           }
                    }
         		   	
         		   	if(blockID == Block.stone.blockID)
                    {
                    	if(Math.random()*100 > 90)
                    	{
                    		worldObj.setBlockWithNotify((int)blockPosition.x, (int)blockPosition.y, (int)blockPosition.z, ICBM.blockRadioactive.blockID);
                    	}
                    }
                    
         		   	else if (blockID == Block.leaves.blockID)
                    {
                    	worldObj.setBlockWithNotify((int)blockPosition.x, (int)blockPosition.y, (int)blockPosition.z, 0);
                    }
                    else if (blockID == Block.tallGrass.blockID)
                    {
                    	if(Math.random()*100 > 50) worldObj.setBlockWithNotify((int)blockPosition.x, (int)blockPosition.y, (int)blockPosition.z, Block.cobblestone.blockID);
                    	else worldObj.setBlockWithNotify((int)blockPosition.x, (int)blockPosition.y, (int)blockPosition.z, 0);
                    }
                    else if (blockID == Block.tilledField.blockID)
                    {
                    	worldObj.setBlockWithNotify((int)blockPosition.x, (int)blockPosition.y, (int)blockPosition.z, Block.mycelium.blockID);
                    }
     		   	}
			}
		}
    	
    	AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(position.x - radius, position.y - radius, position.z - radius, position.x + radius, position.y + radius, position.z + radius);
        List<EntityLiving> entitiesNearby = worldObj.getEntitiesWithinAABB(EntityLiving.class, bounds);
        
        for (EntityLiving entity : entitiesNearby)
        {
        	if(entity instanceof EntityPig)
            {
        		EntityPigZombie var2 = new EntityPigZombie(worldObj);
                var2.setLocationAndAngles(entity.posX, entity.posY, entity.posZ, entity.rotationYaw, entity.rotationPitch);
                worldObj.spawnEntityInWorld(var2);
                entity.setDead();
            }
        	else if(entity instanceof EntityVillager)
            {
        		EntityZombie var2 = new EntityZombie(worldObj);
                var2.setLocationAndAngles(entity.posX, entity.posY, entity.posZ, entity.rotationYaw, entity.rotationPitch);
                worldObj.spawnEntityInWorld(var2);
                entity.setDead();
            }
        }
        
		return false;
	}


}
