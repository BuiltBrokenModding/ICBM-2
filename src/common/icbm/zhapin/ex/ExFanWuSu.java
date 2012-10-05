package icbm.zhapin.ex;


import icbm.ICBM;
import icbm.zhapin.EZhaPin;
import icbm.zhapin.ZhaPin;

import java.util.List;

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.Block;
import net.minecraft.src.DamageSource;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.MathHelper;
import net.minecraft.src.World;
import universalelectricity.UEConfig;
import universalelectricity.prefab.Vector3;

public class ExFanWuSu extends ZhaPin
{	
	private static final int RADUIS = 28;
	private static final int LAYERS_PER_TICK = 2;
	public boolean destroyBedrock = false;
	
	public ExFanWuSu(String name, int ID, int tier)
	{
		super(name, ID, tier);
		this.setYinXin(300);
		this.destroyBedrock = UEConfig.getConfigData(ICBM.CONFIGURATION, this.getMing()+" Destroy Bedrock", destroyBedrock);
	}
	
	/**
	 * Called before an explosion happens
	 */
	public void baoZhaQian(World worldObj, Vector3 position, Entity explosionSource)
	{
		worldObj.playSoundEffect(position.x, position.y, position.z, "icbm.antimatter", 5F, 1F);
		explosionSource.posY += 5;
	}

	@Override
	public boolean doBaoZha(World worldObj, Vector3 position, Entity explosionSource, int callCount)
	{
		if(!worldObj.isRemote)
		{
			while(position.y > 0)
			{
				for(int x = -RADUIS; x < RADUIS; x++)
				{
					for(int z = -RADUIS; z < RADUIS; z++)
					{
						double dist = MathHelper.sqrt_double((x*x + z*z));
		
						if(dist > RADUIS) continue;
						
						Vector3 targetPosition = Vector3.add(new Vector3(x, 0, z), position);
						int blockID = targetPosition.getBlockID(worldObj);		
						
						if(blockID > 0)
						{
							if(blockID == Block.bedrock.blockID && !destroyBedrock) continue;
							
							if(dist < RADUIS - 1 || worldObj.rand.nextFloat() > 0.7)
							{
								targetPosition.setBlock(worldObj, 0);
							}
						}
					}
				}
				
				position.y --;
			}
		}
		
		return false;
	}
	
	@Override
	public void baoZhaHou(World worldObj, Vector3 position, Entity explosionSource)
	{		
		AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(position.x - RADUIS, position.y - RADUIS, position.z - RADUIS, position.x + RADUIS, position.y + RADUIS, position.z + RADUIS);
        List<Entity> allEntities = worldObj.getEntitiesWithinAABB(Entity.class, bounds);
    	
        for(Entity entity : allEntities)
        {     
        	if(entity instanceof EZhaPin)
        	{
	        	((EZhaPin)entity).endExplosion();
        	}
        	else if(entity instanceof EntityLiving)
        	{
        		entity.attackEntityFrom(DamageSource.explosion, 99999999);
        	}
        }
	}

	/**
	 * Called when the explosive is on fuse and going to explode.
	 * Called only when the explosive is in it's TNT form.
	 * @param fuseTicks - The amount of ticks this explosive is on fuse
	 */
	@Override
	public void onYinZha(World worldObj, Vector3 position, int fuseTicks)
	{
        super.onYinZha(worldObj, position, fuseTicks);

        if(fuseTicks % 25 == 0)
        {
    		worldObj.playSoundEffect(position.x, position.y, position.z, "icbm.alarm", 4F, 1F);
        }
	}
}
