package icbm.explosions;

import icbm.EntityProceduralExplosion;
import net.minecraft.src.Block;
import net.minecraft.src.Entity;
import net.minecraft.src.EnumMovingObjectType;
import net.minecraft.src.MathHelper;
import net.minecraft.src.MovingObjectPosition;
import net.minecraft.src.World;
import universalelectricity.Vector3;
import universalelectricity.recipe.RecipeManager;

public class ExplosiveNuclear extends Explosive
{	
	public static final int MAX_RADIUS = 30;
	
	public ExplosiveNuclear(String name, int ID, int tier)
	{
		super(name, ID, tier);
		this.setFuse(200);
	}
	
	@Override
	public void preExplosion(World worldObj, Vector3 position, Entity explosionSource)
	{
		
	}

	@Override
	public boolean doExplosion(World worldObj, Vector3 position, Entity explosionSource, int callCount)
	{
		EntityProceduralExplosion source = (EntityProceduralExplosion)explosionSource;
		
		int radius = MAX_RADIUS;
		
		for(int x = -radius; x < radius; x++)
		{
			for(int y = -radius; y < radius; y++)
			{
				for(int z = -radius; z < radius; z++)
				{
					double distance = MathHelper.sqrt_double((x*x + y*y + z*z));
					
					if(distance < radius && distance > radius-1)
					{
						float power = 70;
						
						Vector3 target = Vector3.add(position, new Vector3(x, y, z));
						
						MovingObjectPosition movingObjectPosotion = worldObj.rayTraceBlocks(position.toVec3(), target.toVec3());
						
						if(movingObjectPosotion != null)
						{
							while(power > 0 && movingObjectPosotion.blockX != target.intX() && movingObjectPosotion.blockY != target.intY() && movingObjectPosotion.blockZ != target.intZ())
							{
								movingObjectPosotion = worldObj.rayTraceBlocks(position.toVec3(), target.toVec3());
	
								if(movingObjectPosotion != null)
								{
									if(movingObjectPosotion.typeOfHit == EnumMovingObjectType.TILE)
									{
										int blockID = worldObj.getBlockId(movingObjectPosotion.blockX, movingObjectPosotion.blockY, movingObjectPosotion.blockZ);
										
										if(blockID > 0)
										{
											power -= Block.blocksList[blockID].getExplosionResistance(explosionSource);
											System.out.println(power);
											worldObj.setBlockWithNotify(movingObjectPosotion.blockX, movingObjectPosotion.blockY, movingObjectPosotion.blockZ, 0);
										}
									}
								}
								else
								{
									break;
								}
							}
						}

						//source.dataList.add(new Vector3(position.x+x, position.y+y, position.z+z));
					}
				}
			}
		}
		
		return false;
		
		/*
		Vector3 currentPos;
		int blockID;
		double dist;
		    		
		for(int x = -radius; x < radius; x++)
		{
			for(int y = -radius; y < radius; y++)
			{
				for(int z = -radius; z < radius; z++)
				{
					dist = MathHelper.sqrt_double((x*x + y*y + z*z));
					
					if(dist > radius || dist < radius-2) continue;
					
					currentPos = new Vector3(position.x + x, position.y + y, position.z + z);
					blockID = worldObj.getBlockId(currentPos.intX(), currentPos.intY(), currentPos.intZ());		
					
					if(blockID == 0) continue;
					
					if(blockID == Block.waterStill.blockID || blockID == Block.waterMoving.blockID || blockID == Block.lavaStill.blockID)
                	{
						if(dist > radius - 1)
    					{
    						if(worldObj.rand.nextInt(3) > 0)
    						{
    							worldObj.setBlockWithNotify(currentPos.intX(), currentPos.intY(), currentPos.intZ(), 0);
    						}
    					}
						else
						{
							worldObj.setBlockWithNotify(currentPos.intX(), currentPos.intY(), currentPos.intZ(), 0);
						}
    					
						continue;
                	}
					
                    float power = maxRadius * (0.7F + worldObj.rand.nextFloat() * 0.6F);

                    power -= (Block.blocksList[blockID].getExplosionResistance(source) + 0.3F);

					if (power > 0.0F)
                    {
    					Block.blocksList[blockID].onBlockDestroyedByExplosion(worldObj, currentPos.intX(), currentPos.intY(), currentPos.intZ());
    					
    					if(dist < radius - 1 || worldObj.rand.nextInt(3) > 0)
    					{
							worldObj.setBlockWithNotify(currentPos.intX(), currentPos.intY(), currentPos.intZ(), 0);
							
							if(worldObj.rand.nextFloat() > 0.98)
							{
								worldObj.spawnParticle("hugeexplosion", currentPos.x, currentPos.y, currentPos.z, 0.0D, 0.0D, 0.0D);
							}
    					}
                    }
                    //End
				}
			}
		}
    	
		worldObj.playSoundEffect(position.x, position.y, position.z, "icbm.explosion", 6.0F, (1.0F + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.2F) * 0.7F);
		this.doDamageEntities(worldObj, position, radius, radius);
		
		if(radius >= maxRadius)
		{
			return false;
		}
		
		return true;*/
	}
	
	/**
	 * The interval in ticks before the next procedural call of this explosive
	 * @param return - Return -1 if this explosive does not need proceudral calls
	 */
	@Override
	public int proceduralInterval(){ return 2; }

	@Override
	public void postExplosion(World worldObj, Vector3 position, Entity explosionSource)
	{
		Explosive.DecayLand.doExplosion(worldObj, position, null, 45, -1);
		Explosive.Mutation.doExplosion(worldObj, position, null, 45, -1);
	}

	/**
	 * Called when the explosive is on fuse and going to explode.
	 * Called only when the explosive is in it's TNT form.
	 * @param fuseTicks - The amount of ticks this explosive is on fuse
	 */
	@Override
	public void onDetonating(World worldObj, Vector3 position, int fuseTicks)
	{
        super.onDetonating(worldObj, position, fuseTicks);
        
        if(fuseTicks % 25 == 0)
        {
    		worldObj.playSoundEffect((int)position.x, (int)position.y, (int)position.z, "icbm.alarm", 4F, 1F);
        }
	}
	
	@Override
	public void addCraftingRecipe()
	{
        RecipeManager.addRecipe(this.getItemStack(), new Object [] {"?@?", "@!@", "?@?", '!', Condensed.getItemStack(), '@', Block.tnt, '?', "ingotUranium"});
	}
}
