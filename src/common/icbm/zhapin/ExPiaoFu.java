package icbm.zhapin;

import icbm.EntityGravityBlock;

import java.util.List;

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.Block;
import net.minecraft.src.Entity;
import net.minecraft.src.Item;
import net.minecraft.src.MathHelper;
import net.minecraft.src.World;
import universalelectricity.prefab.Vector3;
import universalelectricity.recipe.RecipeManager;

public class ExPiaoFu extends ZhaPin
{	
	private static final int MAX_RADIUS = 15;
	
	public ExPiaoFu(String name, int ID, int tier)
	{
		super(name, ID, tier);
	}

	//Sonic Explosion is a procedural explosive
	@Override
	public boolean doBaoZha(World worldObj, Vector3 position, Entity explosionSource, int explosionMetadata, int callCount)
	{
		Vector3 currentPos;
		int blockID;
		int metadata;
		double dist;
		
		int r = MAX_RADIUS;
		int blocksToTake = 10;
		
		for(int x = -r; x < r; x++)
		{
			for(int y = -r; y < r; y++)
			{
				for(int z = -r; z < r; z++)
				{
					dist = MathHelper.sqrt_double((x*x + y*y + z*z));
					
					if(dist > r || blocksToTake <= 0) continue;
					
					currentPos = new Vector3(position.x + x, position.y + y, position.z + z);
					blockID = worldObj.getBlockId(currentPos.intX(), currentPos.intY(), currentPos.intZ());
					
					if(blockID == 0 || blockID == Block.bedrock.blockID || blockID == Block.obsidian.blockID) continue;
					
					metadata = worldObj.getBlockMetadata(currentPos.intX(), currentPos.intY(), currentPos.intZ());
					
					if((dist < r - 1 || worldObj.rand.nextInt(3) > 0) && worldObj.rand.nextFloat() > 0.99)
					{
						worldObj.setBlockWithNotify(currentPos.intX(), currentPos.intY(), currentPos.intZ(), 0);
						
						currentPos.add(0.5D);
						
						if(worldObj.rand.nextFloat() > 0.85)
						{
							if(!worldObj.isRemote)
							{
								EntityGravityBlock entity = new EntityGravityBlock(worldObj, currentPos, blockID, metadata, 0);
								worldObj.spawnEntityInWorld(entity);
								entity.yawChange = 15*worldObj.rand.nextFloat();
								entity.pitchChange = 30*worldObj.rand.nextFloat();
								entity.motionY += Math.max(0.3*worldObj.rand.nextFloat(), 0.1);
							}
					        worldObj.spawnParticle("hugeexplosion", currentPos.x, currentPos.y, currentPos.z, 0.0D, 0.0D, 0.0D);
						}
						
						blocksToTake --;
					}
				}
				
			}
		}
		
		
		int radius = MAX_RADIUS;
		AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(position.x - radius, position.y - radius, position.z - radius, position.x + radius, 100, position.z + radius);
        List<Entity> allEntities = worldObj.getEntitiesWithinAABB(Entity.class, bounds);
    	
        for(Entity entity : allEntities)
        {
            if(!(entity instanceof EntityGravityBlock) && entity.posY < 100+position.y)
            {
            	if(entity.motionY < 0.4)
            	{
            		entity.motionY += 0.12;
            	}
            }
        }
        
        if(callCount > 20*120)
        {
        	return false;
        }
        
        return true;
	}
	
	@Override
	public void baoZhaQian(World worldObj, Vector3 position, Entity explosionSource)
	{
		worldObj.playSoundEffect(position.x, position.y, position.z, "icbm.antigravity", 6.0F, (1.0F + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.2F) * 0.7F);
	}
	
	/**
	 * The interval in ticks before the next procedural call of this explosive
	 * @return - Return -1 if this explosive does not need proceudral calls
	 */
	@Override
	public int proceduralInterval() { return 1; }

	@Override
	public void addCraftingRecipe()
	{
        RecipeManager.addRecipe(this.getItemStack(), new Object [] {"EEE", "ETE", "EEE", 'T', Block.tnt, 'E', Item.eyeOfEnder});
	}
}
