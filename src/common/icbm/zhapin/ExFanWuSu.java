package icbm.zhapin;


import java.util.List;

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.Block;
import net.minecraft.src.Entity;
import net.minecraft.src.MathHelper;
import net.minecraft.src.World;
import universalelectricity.prefab.Vector3;

public class ExFanWuSu extends ZhaPin
{	
	public boolean destroyBedrock = false;
	
	public ExFanWuSu(String name, int ID, int tier)
	{
		super(name, ID, tier);
		this.setFuse(300);
		this.destroyBedrock = getExplosiveConfig("Destroy Bedrock", destroyBedrock);
	}
	
	/**
	 * Called before an explosion happens
	 */
	public void preExplosion(World worldObj, Vector3 position, Entity explosionSource)
	{
		explosionSource.posY += 5;
	}

	@Override
	public boolean doExplosion(World worldObj, Vector3 position, Entity source, int radius)
	{
		radius = 50;
		
		Vector3 currentPos;
		int blockID;
		double dist;
				    		
		for(int x = -radius; x < radius; x++)
		{
			for(int z = -radius; z < radius; z++)
			{
				dist = MathHelper.sqrt_double((x*x + z*z));

				if(dist > radius) continue;
				
				currentPos = new Vector3(position.x + x, position.y, position.z + z);
				blockID = worldObj.getBlockId(currentPos.intX(), currentPos.intY(), currentPos.intZ());		
				
				if(blockID > 0)
				{
					if(blockID == Block.bedrock.blockID && !destroyBedrock) continue;
					
					if(dist < radius - 1 || worldObj.rand.nextInt(3) > 0)
					{
						worldObj.setBlockWithNotify(currentPos.intX(), currentPos.intY(), currentPos.intZ(), 0);
					}
				}
			}
		}
    	
		worldObj.playSoundEffect(position.x, position.y, position.z, "icbm.explosion", 6.0F, (1.0F + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.2F) * 0.7F);
		this.doDamageEntities(worldObj, position, radius/2, radius);
		
		AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(position.x - radius, position.y - radius, position.z - radius, position.x + radius, position.y + radius, position.z + radius);
        List<EZhaPin> allEntities = worldObj.getEntitiesWithinAABB(EZhaPin.class, bounds);
    	
        for(EZhaPin entity : allEntities)
        {            
        	if(entity != source) entity.endExplosion();
        }
		
		if(position.y <= 0)
		{
			return false;
		}
		
		source.posY = Math.round(position.y - 1);
		return true;
	}
	
	/**
	 * The interval in ticks before the next procedural call of this explosive
	 * @param return - Return -1 if this explosive does not need proceudral calls
	 */
	@Override
	public int proceduralInterval(){ return 5; }

	@Override
	public void postExplosion(World worldObj, Vector3 position, Entity explosionSource)
	{
		ZhaPin.DecayLand.doExplosion(worldObj, position, null, 45, -1);
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
		
	}

}
