package icbm.zhapin;

import net.minecraft.src.Block;
import net.minecraft.src.Entity;
import net.minecraft.src.World;
import universalelectricity.prefab.Vector3;

public class ExBingDan2 extends ZhaPin
{	
	public ExBingDan2(String name, int ID, int tier)
	{
		super(name, ID, tier);
		this.setFuse(1);
	}

	@Override
	public boolean doExplosion(World worldObj, Vector3 position, Entity explosionSource, int metadata, int callCount)
	{
		int radius = callCount;
		
    	for(int x = -radius; x < radius; x++)
        {
     	   for(int y = -radius; y < radius; y++)
     	   {
     		   	for(int z = -radius; z < radius; z++)
     		   	{
	     		   	Vector3 blockPosition = new Vector3(x ,y ,z);
	 		   		blockPosition.add(position);
     		   		//Set fire by chance and distance
     	            double distanceFromCenter = position.distanceTo(blockPosition);
     	            
     	           if(distanceFromCenter > radius || distanceFromCenter < radius-2) continue;
     	            
     	            double chance = radius - (Math.random() * distanceFromCenter);

     	            //System.out.println("Distance: "+distance+", "+chance);
     	            if (chance > distanceFromCenter * 0.55)
     	            {
     	            	//Check to see if the block is an air block and there is a block below it to support the fire
     	               int blockID = worldObj.getBlockId((int)blockPosition.x, (int)blockPosition.y, (int)blockPosition.z);
     	               
     	               if (blockID == Block.fire.blockID || blockID == Block.lavaMoving.blockID || blockID == Block.lavaStill.blockID)
	   	               {
	   	                   worldObj.setBlockWithNotify((int)blockPosition.x, (int)blockPosition.y, (int)blockPosition.z, Block.snow.blockID);
	   	               }
     	               else if(blockID == 0 && worldObj.getBlockId((int)blockPosition.x, (int)blockPosition.y-1, (int)blockPosition.z) != Block.ice.blockID && worldObj.getBlockId((int)blockPosition.x, (int)blockPosition.y-1, (int)blockPosition.z) != 0)
     	               {
	   	                   worldObj.setBlockWithNotify((int)blockPosition.x, (int)blockPosition.y, (int)blockPosition.z, Block.ice.blockID);
     	               }
     	            }
     		   	}
			}
		}
    	
        worldObj.playSoundEffect(position.x + 0.5D, position.y + 0.5D, position.z + 0.5D, "icbm.redmatter", 6.0F, (1.0F + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.2F)* 1F);
		
        if(callCount > 40)
        {
        	return false;
        }
        return true;
	}
	
	/**
	 * The interval in ticks before the next procedural call of this explosive
	 * @return - Return -1 if this explosive does not need proceudral calls
	 */
	public int proceduralInterval() { return 3; }
	
	/**
	 * Called every tick
	 * @param ticksExisted 
	 
	@Override
	public void onUpdate(World worldObj, Vector3 position, int ticksExisted)
	{
		long worldTime = worldObj.getWorldTime();
		
		while(worldTime > 23999)
		{
			worldTime -= 23999;
		}
		
		if(worldTime < 0)
		{
			worldObj.setWorldTime(worldObj.getWorldTime()+150);
		}
	}*/

}
