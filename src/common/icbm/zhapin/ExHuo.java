package icbm.zhapin;

import icbm.ICBM;
import net.minecraft.src.Block;
import net.minecraft.src.Entity;
import net.minecraft.src.Item;
import net.minecraft.src.World;
import universalelectricity.prefab.Vector3;
import universalelectricity.recipe.RecipeManager;

public class ExHuo extends ZhaPin
{
	public ExHuo(String name, int ID, int tier)
	{
		super(name, ID, tier);
	}

	/**
	 * World worldObj, Vector3 position, int amount, boolean isExplosive
	 */
	@Override
	public void doBaoZha(World worldObj, Vector3 position, Entity explosionSource)
	{    	
		int radius = 12;
		
		if(explosionSource instanceof EShouLiuDan)
		{
			radius = 6;
		}
		
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
     	            double chance = radius - (Math.random() * distanceFromCenter);

     	            //System.out.println("Distance: "+distance+", "+chance);
     	            if (chance > distanceFromCenter * 0.55)
     	            {
     	            	//Check to see if the block is an air block and there is a block below it to support the fire
     	               int blockID = worldObj.getBlockId((int)blockPosition.x, (int)blockPosition.y, (int)blockPosition.z);

     	               if ((blockID == 0 || blockID == Block.snow.blockID) && worldObj.getBlockMaterial((int)blockPosition.x, (int)blockPosition.y - 1, (int)blockPosition.z).isSolid())
     	               {
     	                   worldObj.setBlockWithNotify((int)blockPosition.x, (int)blockPosition.y, (int)blockPosition.z, Block.fire.blockID);
     	               }
     	               else if (blockID == Block.ice.blockID)
     	               {
     	                   worldObj.setBlockWithNotify((int)blockPosition.x, (int)blockPosition.y, (int)blockPosition.z, 0);
     	               }
     	            }
     		   	}
			}
		}
            
        worldObj.playSoundEffect(position.x + 0.5D, position.y + 0.5D, position.z + 0.5D, "icbm.explosionfire", 4.0F, (1.0F + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.2F)* 1F);
	}

	@Override
	public void init()
	{
        RecipeManager.addRecipe(this.getItemStack(), new Object [] {"@@@", "@?@", "@!@", '@', ICBM.itemLiu, '?', Block.tnt, '!', Item.bucketLava});
	}

}
