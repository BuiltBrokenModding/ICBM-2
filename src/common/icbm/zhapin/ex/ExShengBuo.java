package icbm.zhapin.ex;

import icbm.EFeiBlock;
import icbm.ICBM;
import icbm.daodan.EDaoDan;
import icbm.zhapin.ZhaPin;

import java.util.List;

import chb.mods.mffs.api.IForceFieldBlock;

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.Block;
import net.minecraft.src.Entity;
import net.minecraft.src.MathHelper;
import net.minecraft.src.World;
import universalelectricity.BasicComponents;
import universalelectricity.prefab.Vector3;
import universalelectricity.recipe.RecipeManager;

public class ExShengBuo extends ZhaPin
{	
	private static final int MAX_RADIUS = 10;
	
	public ExShengBuo(String name, int ID, int tier)
	{
		super(name, ID, tier);
	}

	//Sonic Explosion is a procedural explosive
	@Override
	public boolean doBaoZha(World worldObj, Vector3 position, Entity explosionSource, int explosionMetadata, int callCount)
	{		
		int r = callCount;
		
		for(int x = -r; x < r; x++)
		{
			for(int y = -r; y < r; y++)
			{
				for(int z = -r; z < r; z++)
				{
					double dist = MathHelper.sqrt_double((x*x + y*y + z*z));
					
					if(dist > r || dist < r-3) continue;
					
					Vector3 currentPos = new Vector3(position.x + x, position.y + y, position.z + z);
					int blockID = worldObj.getBlockId(currentPos.intX(), currentPos.intY(), currentPos.intZ());
					
					if(blockID == 0 || blockID == Block.bedrock.blockID || blockID == Block.obsidian.blockID) continue;
					
					if(Block.blocksList[blockID] instanceof IForceFieldBlock) continue;
					
					int metadata = worldObj.getBlockMetadata(currentPos.intX(), currentPos.intY(), currentPos.intZ());
					
					if(dist < r - 1 || worldObj.rand.nextInt(3) > 0)
					{
						worldObj.setBlockWithNotify(currentPos.intX(), currentPos.intY(), currentPos.intZ(), 0);
						
						currentPos.add(0.5D);
						
						if(!worldObj.isRemote && worldObj.rand.nextFloat() < 0.4*(MAX_RADIUS-r))
						{
							EFeiBlock entity = new EFeiBlock(worldObj, currentPos, blockID, metadata);
							worldObj.spawnEntityInWorld(entity);
							entity.yawChange = 50*worldObj.rand.nextFloat();
							entity.pitchChange = 100*worldObj.rand.nextFloat();
						}
					}
				}
			}
		}
		
		int radius = 2*callCount;
		AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(position.x - radius, position.y - radius, position.z - radius, position.x + radius, position.y + radius, position.z + radius);
        List<Entity> allEntities = worldObj.getEntitiesWithinAABB(Entity.class, bounds);
    	
        for (int var11 = 0; var11 < allEntities.size(); ++var11)
        {
            Entity entity = (Entity)allEntities.get(var11);
            
            if(entity instanceof EDaoDan)
            {
            	((EDaoDan)entity).explode();
            	break;
            }
            else
            {      	
            	double xDifference = entity.posX - position.x;
            	double zDifference = entity.posZ - position.z;
            	            	
            	r = (int)MAX_RADIUS;
            	if(xDifference < 0) r = (int)-MAX_RADIUS;
            	
            	entity.motionX += (r-xDifference) * 0.02*worldObj.rand.nextFloat();
                entity.motionY += 4*worldObj.rand.nextFloat();
                
                r = (int)MAX_RADIUS;
                if(zDifference < 0) r = (int)-MAX_RADIUS;
                
                entity.motionZ += (r-zDifference) * 0.02*worldObj.rand.nextFloat();
            }
        }
        
        if(callCount > 12)
        {
        	return false;
        }
        
        return true;
	}
	
	@Override
	public void baoZhaQian(World worldObj, Vector3 position, Entity explosionSource)
	{
		worldObj.playSoundEffect(position.x, position.y, position.z, "icbm.sonicwave", 6.0F, (1.0F + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.2F) * 0.7F);
	}
	
	/**
	 * The interval in ticks before the next procedural call of this explosive
	 * @return - Return -1 if this explosive does not need proceudral calls
	 */
	@Override
	public int proceduralInterval() { return 5; }

	@Override
	public void init()
	{
        RecipeManager.addRecipe(this.getItemStack(), new Object [] {"@?@", "?!?", "@?@", '!', Block.tnt, '?', Block.music, '@', BasicComponents.itemBronzePlate}, this.getMing(), ICBM.CONFIGURATION, true);
	}
}
