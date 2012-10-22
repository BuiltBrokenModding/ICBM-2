package icbm.zhapin.ex;

import icbm.ParticleSpawner;
import icbm.ZhuYao;
import icbm.zhapin.EZhaDan;
import icbm.zhapin.EZhaPin;
import icbm.zhapin.ZhaPin;

import java.util.List;

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.Block;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityEnderman;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.World;
import universalelectricity.prefab.Vector3;
import universalelectricity.recipe.RecipeManager;

public class ExWan extends ZhaPin
{	
	public static final int BAN_JING = 20;
	public static final int SHI_JIAN = 20*8;
	
	public ExWan(String name, int ID, int tier)
	{
		super(name, ID, tier);
		this.isMobile = true;
	}
	
	public void baoZhaQian(World worldObj, Vector3 position, Entity explosionSource)
	{
		if(!worldObj.isRemote)
		{
			worldObj.createExplosion(explosionSource, position.x, position.y, position.z, 5.0F);
		}
	}
	
	@Override
	public boolean doBaoZha(World worldObj, Vector3 position, Entity explosionSource, int explosionMetadata, int callCount)
	{
		if(worldObj.isRemote)
		{
			int r = (int) (BAN_JING-(double)(((double)callCount/(double)SHI_JIAN)*BAN_JING));

			for(int x = -r; x < r; x++)
    		{
				for(int z = -r; z < r; z++)
				{
					for(int y = -r; y < r; y++)
					{
						Vector3 targetPosition = Vector3.add(position, new Vector3(x, y, z));

						double distance = targetPosition.distanceTo(position);
						
	                    if(distance < r && distance > r-1)
						{
							if(targetPosition.getBlockID(worldObj) != 0) continue;
							
							if(worldObj.rand.nextFloat() < Math.max(0.001*r, 0.01) || (ZhuYao.ADVANCED_VISUALS && worldObj.rand.nextFloat() < Math.max(0.0015*r, 0.015)))
							{
								float var13 = 0.0f;
								float var15 = 0.0f;
								float var17 = 0.0f;
					            var13 = (float) ((worldObj.rand.nextFloat() - 0.5D) * 0.5D);
					            var15 = (float) ((worldObj.rand.nextFloat() - 0.5D) * 0.5D);
					            var17 = (float) ((worldObj.rand.nextFloat() - 0.5D) * 0.5D);
					            
								ParticleSpawner.spawnParticle("portal", worldObj, targetPosition, var13, var15, var17, 5f, 1);
							}
						}
					}
				}
    		}
		}
		
		//Make the blocks controlled by this red matter orbit around it
		int radius = BAN_JING;
		AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(position.x - radius, position.y - radius, position.z - radius, position.x + radius, position.y + radius, position.z + radius);
        List<Entity> allEntities = worldObj.getEntitiesWithinAABB(Entity.class, bounds);
    	boolean explosionCreated = false;
        
        for(Entity entity : allEntities)
        {
        	if(entity == explosionSource) continue;
        	
			double xDifference = entity.posX - position.x;
			double yDifference = entity.posY - position.y;
        	double zDifference = entity.posZ - position.z;
        	            	
        	int r = BAN_JING;
        	if(xDifference < 0) r = (int)-BAN_JING;
        	
        	entity.motionX -= (r-xDifference) * Math.abs(xDifference) * 0.0005;
        	
        	r = BAN_JING;
        	if(entity.posY > position.y) r = -BAN_JING;
            entity.motionY += (r-yDifference) * Math.abs(yDifference) * 0.001;
            
            r = (int)BAN_JING;
            if(zDifference < 0) r = (int)-BAN_JING;
            
            entity.motionZ -= (r-zDifference) * Math.abs(zDifference) * 0.0005;
            
            if(Vector3.distance(new Vector3(entity.posX, entity.posY, entity.posZ), position) < 4)
            {
            	if(!explosionCreated && callCount % 5 == 0)
            	{
               	 	worldObj.spawnParticle("hugeexplosion", entity.posX, entity.posY, entity.posZ, 0.0D, 0.0D, 0.0D);
            		explosionCreated = true;
            	}
            	
            	if(!(entity instanceof EntityLiving))
            	{
            		if(entity instanceof EZhaPin)
            		{
            			worldObj.playSoundEffect(position.x, position.y, position.z, "icbm.explosion", 7.0F, (1.0F + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.2F) * 0.7F);
            		
            			if(worldObj.rand.nextFloat() > 0.85 && !worldObj.isRemote)
            			{
            				entity.setDead();
            				return false;
            			}
            		}
            		else if(entity instanceof EZhaDan)
            		{
            			((EZhaDan)entity).explode();
            		}
            		
            		entity.setDead();
            	}
            	else if(entity instanceof EntityPlayer)
            	{
            		((EntityPlayer)entity).travelToTheEnd(1);
            	}
            }
        }

		worldObj.playSoundEffect(position.x, position.y, position.z, "icbm.redmatter", 4.0F, (1.0F + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.2F) * 1F);

		if(callCount > SHI_JIAN)
		{
			return false;
		}
		
        return true;
	}
	
	@Override
	public void baoZhaHou(World worldObj, Vector3 position, Entity explosionSource)
	{
		if(!explosionSource.worldObj.isRemote)
		{
			for(int i = 0; i < 20; i ++)
			{
				EntityEnderman enderman = new EntityEnderman(worldObj);
				enderman.setPosition(position.x, position.y, position.z);
				explosionSource.worldObj.spawnEntityInWorld(enderman);
			}
		}
	}
	
	/**
	 * The interval in ticks before the next procedural call of this explosive
	 * @return - Return -1 if this explosive does not need proceudral calls
	 */
	@Override
	public int proceduralInterval() { return 1; }
	
	@Override
	public void init()
	{
        RecipeManager.addRecipe(this.getItemStack(), new Object [] {"EPE", "ETE", "EPE", 'P', Item.enderPearl, 'E', Block.whiteStone, 'T', Block.tnt}, this.getMing(), ZhuYao.CONFIGURATION, true);
        RecipeManager.addRecipe(this.getItemStack(), new Object [] {"EEE", "ETE", "EEE", 'E', "dustEndium", 'T', Block.tnt}, this.getMing(), ZhuYao.CONFIGURATION, true);
	}
}
