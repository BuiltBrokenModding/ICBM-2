package icbm.zhapin;

import java.util.List;

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityPig;
import net.minecraft.src.EntityPigZombie;
import net.minecraft.src.EntityVillager;
import net.minecraft.src.EntityZombie;
import net.minecraft.src.World;
import universalelectricity.Vector3;

public class ExBianZhong extends ZhaPin
{
	public ExBianZhong(String name, int ID, int tier)
	{
		super(name, ID, tier);
	}

	@Override
	public boolean doExplosion(World worldObj, Vector3 position, Entity explosionSource, int radius, int callCount)
	{
		if(!worldObj.isRemote)
		{
	    	AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(position.x - radius, position.y - radius, position.z - radius, position.x + radius, position.y + radius, position.z + radius);
	        List<EntityLiving> entitiesNearby = worldObj.getEntitiesWithinAABB(EntityLiving.class, bounds);
	        
	        for(EntityLiving entity : entitiesNearby)
	        {
	        	if(entity instanceof EntityPig)
	            {
	        		EntityPigZombie newEntity = new EntityPigZombie(worldObj);
	        		newEntity.preventEntitySpawning = true;
	                newEntity.setPosition(entity.posX, entity.posY, entity.posZ);
	                entity.setDead();
	            }
	        	else if(entity instanceof EntityVillager)
	            {
	        		EntityZombie newEntity = new EntityZombie(worldObj);
	        		newEntity.preventEntitySpawning = true;
	                newEntity.setPosition(entity.posX, entity.posY, entity.posZ);
	                entity.setDead();
	            }
	        }
		}
        
		return false;
	}


}
