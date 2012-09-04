package icbm.explosions;

import icbm.EntityGravityBlock;
import icbm.EntityGrenade;
import icbm.EntityLightBeam;
import icbm.EntityProceduralExplosion;
import icbm.ICBMPotion;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.Block;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.Item;
import net.minecraft.src.MathHelper;
import net.minecraft.src.Potion;
import net.minecraft.src.PotionEffect;
import net.minecraft.src.World;
import universalelectricity.Vector3;
import universalelectricity.basiccomponents.BasicComponents;
import universalelectricity.recipe.RecipeManager;

public class ExplosiveEndothermic extends Explosive
{
	private final int iceRadius = 40;
	private final int radius = 5;
	
	public ExplosiveEndothermic(String name, int ID, int tier)
	{
		super(name, ID, tier);
	}
	
	/**
	 * Called before an explosion happens
	 */
	public void preExplosion(World worldObj, Vector3 position, Entity explosionSource)
	{
		EntityLightBeam lightBeam = new EntityLightBeam(worldObj, position, 20*20, 0F, 0.3F, 0.7F);
		worldObj.spawnEntityInWorld(lightBeam);
		((EntityProceduralExplosion)explosionSource).entityList.add(0, lightBeam);
		worldObj.createExplosion(null, position.x, position.y, position.z, 4F);
	}
	
	@Override
	public boolean doExplosion(World worldObj, Vector3 position, Entity explosionSource, int callCount)
	{		
		List<Entity> gravityBlocks = new ArrayList();
		
		int radius = this.radius;
		
		if(explosionSource instanceof EntityGrenade)
		{
			radius /= 2;
		}
		
		if(this.canFocusBeam(worldObj, position))
		{
			Vector3 currentPos;
			int blockID;
			int metadata;
			double dist;
			
			int r = (int)radius;
			
			for(int x = -r; x < r; x++)
			{
				for(int y = -r; y < r; y++)
				{
					for(int z = -r; z < r; z++)
					{
						dist = MathHelper.sqrt_double((x*x + y*y + z*z));
	
						if(dist > r || dist < r-3) continue;
						currentPos = new Vector3(position.x + x, position.y + y, position.z + z);
						blockID = worldObj.getBlockId(currentPos.intX(), currentPos.intY(), currentPos.intZ());
						
						if(blockID == 0 || blockID == Block.bedrock.blockID || blockID == Block.obsidian.blockID) continue;
						
						metadata = worldObj.getBlockMetadata(currentPos.intX(), currentPos.intY(), currentPos.intZ());
						
						if(worldObj.rand.nextInt(3) > 0)
						{
							worldObj.setBlockWithNotify(currentPos.intX(), currentPos.intY(), currentPos.intZ(), 0);
							
							currentPos.add(0.5D);
							EntityGravityBlock entity = new EntityGravityBlock(worldObj, currentPos, blockID, metadata);
							worldObj.spawnEntityInWorld(entity);
							gravityBlocks.add(entity);
							entity.pitchChange = 50*worldObj.rand.nextFloat();
						}
					}
				}
			}
		
			((EntityProceduralExplosion)explosionSource).entityList.addAll(gravityBlocks);
			
			gravityBlocks = ((EntityProceduralExplosion)explosionSource).entityList;
			
			for(Entity unspecifiedEntity : gravityBlocks)
			{
				if(unspecifiedEntity instanceof EntityGravityBlock)
				{
					EntityGravityBlock entity = (EntityGravityBlock)unspecifiedEntity;
					double xDifference = entity.posX - position.x;
		        	double zDifference = entity.posZ - position.z;
		        	
		        	r = (int)radius;
		        	if(xDifference < 0) r = (int)-radius;
		        	
		        	if(xDifference > 4)
		        	{
		            	entity.motionX += (r - xDifference) * -0.02*worldObj.rand.nextFloat();
		        	}
		        	
		        	if(entity.posY < position.y + 15)
		        	{
		                entity.motionY += 0.5+	0.6*worldObj.rand.nextFloat();
		                
		                if(entity.posY < position.y + 3)
		                {
		                	entity.motionY += 1.5;
		                }
		        	}
		            
		            r = (int)radius;
		            if(zDifference < 0) r = (int)-radius;
		            
		            if(zDifference > 4)
		            {
		                entity.motionZ += (r - zDifference) * -0.02*worldObj.rand.nextFloat();
		            }
		            
		            entity.yawChange += 3*worldObj.rand.nextFloat();
				}
			}
		}
		else
		{
			return false;
		}
		
		worldObj.playSoundEffect(position.x, position.y, position.z, "icbm.redmatter", 4.0F, 0.8F);
	
		if(callCount > 35)
		{
			return false;
		}
		
		return true;
	}
	
	@Override
	public void postExplosion(World worldObj, Vector3 position, Entity explosionSource)
	{
		if(!worldObj.isRemote)
		{
			((EntityProceduralExplosion)explosionSource).entityList.get(0).setDead();
			
			if(this.canFocusBeam(worldObj, position))
			{
				for(Entity entity : ((EntityProceduralExplosion)explosionSource).entityList)
				{
					if(!(entity instanceof EntityLightBeam))
					{
						double xDifference = entity.posX - position.x;
			        	double zDifference = entity.posZ - position.z;
			        	
			        	int m = 1;
			        	
			        	if(xDifference < 0) m = (int)-1;
			        	
			        	entity.motionX += m*5*worldObj.rand.nextFloat();
		
			            m = 1;
			            
			            if(zDifference < 0) m = (int)-1;
			            
			            entity.motionZ += m*5*worldObj.rand.nextFloat();
					}
				}
							
		        List<EntityLiving> livingEntities = worldObj.getEntitiesWithinAABB(EntityLiving.class, AxisAlignedBB.getBoundingBox(position.x-iceRadius, position.y-iceRadius, position.z-iceRadius, position.x+iceRadius, position.y+iceRadius, position.z+iceRadius));
				
		        for(EntityLiving entity : livingEntities)
		        {
					entity.addPotionEffect(new PotionEffect(ICBMPotion.frostBite.id, 60 * 20, 1));
		    		entity.addPotionEffect(new PotionEffect(Potion.confusion.id, 10 * 20, 2));
		    		entity.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 45 * 20, 4));
		        }
		        
				Explosive.EndothermicIce.spawnExplosive(worldObj, position, (byte)0);
			}			
		}
	}
	
	@Override
	public void addCraftingRecipe()
	{
        RecipeManager.addRecipe(this.getItemStack(), new Object [] {"?!?", "!@!", "?!?", '@', Block.tnt, '?', Block.ice, '!', Block.blockSnow});		
	}
	
	public boolean canFocusBeam(World worldObj, Vector3 position)
	{
		long worldTime = worldObj.getWorldTime();
		
		while(worldTime > 23999)
		{
			worldTime -= 23999;
		}
		
		return (worldTime > 12000 && worldObj.canBlockSeeTheSky(position.intX(), position.intY() + 1, position.intZ()));
	}
	
	/**
	 * The interval in ticks before the next procedural call of this explosive
	 * @param return - Return -1 if this explosive does not need proceudral calls
	 */
	@Override
	public int proceduralInterval(){ return 5; }
}
