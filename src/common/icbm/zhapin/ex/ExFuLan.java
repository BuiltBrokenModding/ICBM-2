package icbm.zhapin.ex;

import icbm.ZhuYao;
import icbm.zhapin.ZhaPin;
import net.minecraft.src.Block;
import net.minecraft.src.Entity;
import net.minecraft.src.MathHelper;
import net.minecraft.src.World;
import universalelectricity.core.Vector3;

public class ExFuLan extends ZhaPin
{
	public ExFuLan(String name, int ID, int tier)
	{
		super(name, ID, tier);
	}
	
	@Override
	public boolean doBaoZha(World worldObj, Vector3 position, Entity explosionSource, int radius, int callCount)
	{
		for(int x = -radius; x < radius; x++)
        {
     	   for(int y = -radius; y < radius; y++)
     	   {
     		   	for(int z = -radius; z < radius; z++)
     		   	{
	     		   	double dist = MathHelper.sqrt_double((x*x + y*y + z*z));
	     		   	
					if(dist > radius) continue;
					
     		   		Vector3 blockPosition = new Vector3(x ,y ,z);
     		   		blockPosition.add(position);	   	
					
     		   		//Check what type of block this is. Decay the land depending on the block type.
     		   		int blockID = worldObj.getBlockId((int)blockPosition.x, (int)blockPosition.y, (int)blockPosition.z);
         		   	
     		   		if (blockID == Block.grass.blockID || blockID == Block.sand.blockID)
                    {
	     	           if(worldObj.rand.nextFloat() > 0.9)
	     	           {
	     	        	   worldObj.setBlockWithNotify((int)blockPosition.x, (int)blockPosition.y, (int)blockPosition.z, ZhuYao.bFuShe.blockID);
	     	           }
                    }
         		   	
         		   	if(blockID == Block.stone.blockID)
                    {
                    	if(worldObj.rand.nextFloat() > 0.97)
                    	{
                    		worldObj.setBlockWithNotify((int)blockPosition.x, (int)blockPosition.y, (int)blockPosition.z, ZhuYao.bFuShe.blockID);
                    	}
                    }
                    
         		   	else if (blockID == Block.leaves.blockID)
                    {
                    	worldObj.setBlockWithNotify((int)blockPosition.x, (int)blockPosition.y, (int)blockPosition.z, 0);
                    }
                    else if (blockID == Block.tallGrass.blockID)
                    {
                    	if(Math.random()*100 > 50) worldObj.setBlockWithNotify((int)blockPosition.x, (int)blockPosition.y, (int)blockPosition.z, Block.cobblestone.blockID);
                    	else worldObj.setBlockWithNotify((int)blockPosition.x, (int)blockPosition.y, (int)blockPosition.z, 0);
                    }
                    else if (blockID == Block.tilledField.blockID)
                    {
                    	worldObj.setBlockWithNotify((int)blockPosition.x, (int)blockPosition.y, (int)blockPosition.z, Block.mycelium.blockID);
                    }
     		   	}
			}
		}
        
		return false;
	}
}
