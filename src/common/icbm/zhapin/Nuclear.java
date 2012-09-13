package icbm.zhapin;


import java.util.ArrayList;
import java.util.List;

import net.minecraft.src.Block;
import net.minecraft.src.BlockFluid;
import net.minecraft.src.Entity;
import net.minecraft.src.MathHelper;
import net.minecraft.src.World;
import universalelectricity.Vector3;
import universalelectricity.recipe.RecipeManager;

public class Nuclear extends ZhaPin
{	
	public static final int MAX_RADIUS = 30;
	public static final int MAX_POWER = 100;
	public static final int CALL_COUNT = 100;
	
	public Nuclear(String name, int ID, int tier)
	{
		super(name, ID, tier);
		//this.setFuse(200);
	}
	
	@Override
	public void preExplosion(World worldObj, Vector3 position, Entity explosionSource)
	{
		EZhaPin source = (EZhaPin)explosionSource;

		for(int x = -MAX_RADIUS; x < MAX_RADIUS; x++)
		{
			for(int y = -MAX_RADIUS; y < MAX_RADIUS; y++)
			{
				for(int z = -MAX_RADIUS; z < MAX_RADIUS; z++)
				{
					double distance = MathHelper.sqrt_double((x*x + y*y + z*z));
					
                    if(distance < MAX_RADIUS && distance > MAX_RADIUS-1)
					{
                    	source.dataList.add(new Vector3(x, y, z));
					}
				}
			}
		}
		
		this.doDamageEntities(worldObj, position, MAX_RADIUS, MAX_POWER);
		
	}

	@Override
	public boolean doExplosion(World worldObj, Vector3 position, Entity explosionSource, int callCount)
	{
		if(!worldObj.isRemote)
		{
			List<Vector3> blocksToBreak = new ArrayList<Vector3>();
			
			EZhaPin source = (EZhaPin)explosionSource;
		
			for(int i = callCount; i < callCount+CALL_COUNT; i ++)
			{
				if(i >= source.dataList.size())
				{
					return false;
				}
				
				Vector3 corner = (Vector3)source.dataList.get(i);
				
				float power = MAX_POWER - (MAX_POWER*worldObj.rand.nextFloat()/2);
				
				double xStep = (double)((float)corner.intX() / ((float)MAX_RADIUS/2 - 1.0F) * 2.0F - 1.0F);
	            double yStep = (double)((float)corner.intY() / ((float)MAX_RADIUS/2 - 1.0F) * 2.0F - 1.0F);
	            double zStep = (double)((float)corner.intZ() / ((float)MAX_RADIUS/2 - 1.0F) * 2.0F - 1.0F);
	            double diagonalDistance = Math.sqrt(xStep * xStep + yStep * yStep + zStep * zStep);
	            xStep /= diagonalDistance;
	            yStep /= diagonalDistance;
	            zStep /= diagonalDistance;
	            
	            Vector3 targetPosition = position.copy();
				
				for(float var21 = 0.3F; power > 0f; power -= var21 * 0.75F)
				{
					int blockID = worldObj.getBlockId(targetPosition.intX(), targetPosition.intY(), targetPosition.intZ());
					
					if(blockID > 0)
					{
						if(blockID == Block.bedrock.blockID)
						{
							break;
						}
						else if(Block.blocksList[blockID] instanceof BlockFluid)
						{
							power -= 2;
						}
						else if(blockID == Block.obsidian.blockID)
						{
							power -= 50;
						}
						else
						{
							power -= Block.blocksList[blockID].getExplosionResistance(explosionSource, worldObj, targetPosition.intX(), targetPosition.intY(), targetPosition.intZ(), position.intX(), position.intY(), position.intZ());
						}
						
						if(power > 0f)
						{						
							if(!blocksToBreak.contains(targetPosition))
							{
								blocksToBreak.add(new Vector3(MathHelper.floor_double(targetPosition.x), MathHelper.floor_double(targetPosition.y), MathHelper.floor_double(targetPosition.z)) );
							}
						}
					}

					if(targetPosition.distanceTo(position) > MAX_RADIUS+10) break;
					
					targetPosition.x += xStep * (double)var21;
					targetPosition.y += yStep * (double)var21;
					targetPosition.z += zStep * (double)var21;
				}
			}
			
			for(Vector3 targetPosition : blocksToBreak)
			{
				int blockID = worldObj.getBlockId(targetPosition.intX(), targetPosition.intY(), targetPosition.intZ());
				
				if(blockID > 0)
				{
					worldObj.setBlockWithNotify(targetPosition.intX(), targetPosition.intY(), targetPosition.intZ(), 0);
		            Block.blocksList[blockID].onBlockDestroyedByExplosion(worldObj, targetPosition.intX(), targetPosition.intY(), targetPosition.intZ());
					
					if(worldObj.rand.nextFloat() > 0.9)
					{
						worldObj.spawnParticle("hugeexplosion", targetPosition.x, targetPosition.y, targetPosition.z, 0.0D, 0.0D, 0.0D);
					}
				}
			}
			
			worldObj.playSoundEffect(position.x, position.y, position.z, "icbm.explosion", 7.0F, (1.0F + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.2F) * 0.7F);
		}
		
		return true;
			
		/*
		for(int x = -MAX_RADIUS; x < MAX_RADIUS; x++)
		{
			for(int y = -MAX_RADIUS; y < MAX_RADIUS; y++)
			{
				for(int z = -MAX_RADIUS; z < MAX_RADIUS; z++)
				{
					double distance = MathHelper.sqrt_double((x*x + y*y + z*z));
					
                    if(distance < MAX_RADIUS && distance > MAX_RADIUS-1)
					{
						float power = MAX_POWER - (MAX_POWER*worldObj.rand.nextFloat()/2);
												
						double xStep = (double)((float)x / ((float)MAX_RADIUS - 1.0F) * 2.0F - 1.0F);
                        double yStep = (double)((float)y / ((float)MAX_RADIUS - 1.0F) * 2.0F - 1.0F);
                        double zStep = (double)((float)z / ((float)MAX_RADIUS - 1.0F) * 2.0F - 1.0F);
                        double diagonalDistance = Math.sqrt(xStep * xStep + yStep * yStep + zStep * zStep);
                        xStep /= diagonalDistance;
                        yStep /= diagonalDistance;
                        zStep /= diagonalDistance;
                        
                        Vector3 targetPosition = new Vector3(position.x, position.y, position.z);
						
						for(float var21 = 0.3F; power > 0f; power -= var21 * 0.75F)
						{
							int blockID = worldObj.getBlockId(targetPosition.intX(), targetPosition.intY(), targetPosition.intZ());
							
							if(blockID > 0)
							{
								if(blockID == Block.bedrock.blockID)
								{
									break;
								}
								else if(Block.blocksList[blockID] instanceof BlockFluid)
								{
									power -= 3;
								}
								else
								{
									power -= Block.blocksList[blockID].getExplosionResistance(explosionSource, worldObj, targetPosition.intX(), targetPosition.intY(), targetPosition.intZ(), position.intX(), position.intY(), position.intZ());
								}
								//System.out.println(power);
							}
							
							if(power > 0f)
							{
								worldObj.setBlockWithNotify(targetPosition.intX(), targetPosition.intY(), targetPosition.intZ(), 0);
							}

							targetPosition.x += xStep * (double)var21;
							targetPosition.y += yStep * (double)var21;
							targetPosition.z += zStep * (double)var21;
						}
						
						worldObj.playSoundEffect(position.x, position.y, position.z, "icbm.explosion", 7.0F, (1.0F + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.2F) * 0.7F);
					}
				}
			}
		}
		*/
	}
	
	@Override
	public int countIncrement() { return CALL_COUNT;}
	
	/**
	 * The interval in ticks before the next procedural call of this explosive
	 * @param return - Return -1 if this explosive does not need proceudral calls
	 */
	@Override
	public int proceduralInterval(){ return 1; }

	@Override
	public void postExplosion(World worldObj, Vector3 position, Entity explosionSource)
	{
		ZhaPin.DecayLand.doExplosion(worldObj, position, null, 40, -1);
		ZhaPin.Mutation.doExplosion(worldObj, position, null, 45, -1);
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
