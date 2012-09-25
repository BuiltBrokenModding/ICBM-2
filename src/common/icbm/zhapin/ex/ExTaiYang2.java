package icbm.zhapin.ex;

import icbm.ICBM;
import icbm.zhapin.ZhaPin;
import net.minecraft.src.Block;
import net.minecraft.src.Entity;
import net.minecraft.src.World;
import universalelectricity.UniversalElectricity;
import universalelectricity.prefab.Vector3;

public class ExTaiYang2 extends ZhaPin
{	
	public boolean createNetherrack = true;
	
	public ExTaiYang2(String name, int ID, int tier)
	{
		super(name, ID, tier);
		this.setYinXin(1);
		this.createNetherrack =  UniversalElectricity.getConfigData(ICBM.CONFIGURATION, this.getMing()+" Create Netherrack", createNetherrack);
	}

	@Override
	public boolean doBaoZha(World worldObj, Vector3 position, Entity explosionSource, int metadata, int callCount)
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

     	               if (blockID == Block.waterStill.blockID || blockID == Block.waterMoving.blockID || blockID == Block.ice.blockID)
	   	               {
	   	                   worldObj.setBlockWithNotify((int)blockPosition.x, (int)blockPosition.y, (int)blockPosition.z, 0);
	   	               }
     	               
     	               if ((blockID == 0 || blockID == Block.snow.blockID) && worldObj.getBlockMaterial((int)blockPosition.x, (int)blockPosition.y - 1, (int)blockPosition.z).isSolid())
     	               {
     	                   if(worldObj.rand.nextFloat() > 0.999)
     	                   {
     	                	   worldObj.setBlockWithNotify((int)blockPosition.x, (int)blockPosition.y, (int)blockPosition.z, Block.lavaMoving.blockID);
     	                   }
     	                   else
     	                   {
     	                	   worldObj.setBlockWithNotify((int)blockPosition.x, (int)blockPosition.y, (int)blockPosition.z, Block.fire.blockID);
        	                   
     	                	   blockID = worldObj.getBlockId((int)blockPosition.x, (int)blockPosition.y-1, (int)blockPosition.z);
        	                   
     	                	   if(this.createNetherrack && (blockID == Block.stone.blockID || blockID == Block.grass.blockID || blockID == Block.dirt.blockID) && worldObj.rand.nextFloat() > 0.75)
	   	     	               {
	   		   	                   worldObj.setBlockWithNotify((int)blockPosition.x, (int)blockPosition.y-1, (int)blockPosition.z, Block.netherrack.blockID);
	   	     	               }
     	                   }
     	               }
     	               else if (blockID == Block.ice.blockID)
    	               {
    	                   worldObj.setBlockWithNotify((int)blockPosition.x, (int)blockPosition.y, (int)blockPosition.z, 0);
    	               }
     	            }
     		   	}
			}
		}
    	
        worldObj.playSoundEffect(position.x + 0.5D, position.y + 0.5D, position.z + 0.5D, "icbm.explosionfire", 6.0F, (1.0F + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.2F)* 1F);
		
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
	 */
	@Override
	public void gengXin(World worldObj, Vector3 position, int ticksExisted)
	{
		long worldTime = worldObj.getWorldTime();
		
		while(worldTime > 23999)
		{
			worldTime -= 23999;
		}
		
		if(worldTime < 18000)
		{
			worldObj.setWorldTime(worldObj.getWorldTime()+150);
		}
	}
}
