package icbm.zhapin.ex;

import icbm.ICBM;
import icbm.zhapin.ZhaPin;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.src.Entity;
import net.minecraft.src.World;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.implement.IRotatable;
import universalelectricity.prefab.Vector3;
import universalelectricity.recipe.RecipeManager;

public class ExTuPuoDan extends ZhaPin
{
	private static final int NENG_LIANG = 50;
	private static final int BAN_JING = 10;

	public ExTuPuoDan(String name, int ID, int tier)
	{
		super(name, ID, tier);
		this.setYinXin(10);
	}

	@Override
	public void doBaoZha(World worldObj, Vector3 position, Entity explosionSource)
	{
		List<Vector3> blocksToDestroy = new ArrayList<Vector3>();
		
		Vector3 difference = new Vector3();
		
		if(explosionSource instanceof IRotatable)
		{
			difference.modifyPositionFromSide(((IRotatable)explosionSource).getDirection());
		}
		else
		{
			difference.modifyPositionFromSide(ForgeDirection.DOWN);
		}
			/*	
		for(int x = 0; x < BAN_JING; x++)
		{
			for(int y = 0; y < BAN_JING; y++)
			{
				for(int z = 0; z < BAN_JING; z++)
				{					
					Vector3 corner = new Vector3(position.intX() + x, position.intY() + y, position.intZ() + z);
					
					if (corner.intX() == 0 || corner.intX() == BAN_JING - 1 || corner.intY() == 0 || corner.intY() == BAN_JING - 1 || corner.intZ() == 0 || corner.intZ() == BAN_JING - 1)
                    {
						float power = NENG_LIANG - (NENG_LIANG*worldObj.rand.nextFloat()/2);
						
						double xStep = (double)((float)corner.intX() / ((float)BAN_JING - 1.0F) * 2.0F - 1.0F);
			            double yStep = (double)((float)corner.intY() / ((float)BAN_JING - 1.0F) * 2.0F - 1.0F);
			            double zStep = (double)((float)corner.intZ() / ((float)BAN_JING - 1.0F) * 2.0F - 1.0F);
			            double diagonalDistance = Math.sqrt(xStep * xStep + yStep * yStep + zStep * zStep);
			            xStep /= diagonalDistance;
			            yStep /= diagonalDistance;
			            zStep /= diagonalDistance;
			            
			            Vector3 targetPosition = position.clone();
						
						for(float var21 = 0.3F; power > 0f; power -= var21 * 0.75F)
						{
							int blockID = worldObj.getBlockId(targetPosition.intX(), targetPosition.intY(), targetPosition.intZ());
							
							if(blockID > 0)
							{
								float resistance = 0;
								
								if(blockID == Block.bedrock.blockID)
								{
									break;
								}
								else
								{
									resistance = Block.blocksList[blockID].getExplosionResistance(explosionSource, worldObj, targetPosition.intX(), targetPosition.intY(), targetPosition.intZ(), position.intX(), position.intY(), position.intZ());
								}
								
								power -= resistance;	

								if(power > 0f)
								{						
									if(!blocksToDestroy.contains(targetPosition))
									{
										blocksToDestroy.add(targetPosition.clone());
									}
								}
							}
	
							if(targetPosition.distanceTo(position) > BAN_JING) break;
							
							targetPosition.x += xStep * (double)var21;
							targetPosition.y += yStep * (double)var21;
							targetPosition.z += zStep * (double)var21;
						}
                    }
				}
			}
		}
		
        worldObj.playSoundEffect(position.x, position.y, position.z, "random.explode", 4.0F, (1.0F + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.2F) * 0.7F);
		
		for(Vector3 targetPosition : blocksToDestroy)
		{
			int blockID = worldObj.getBlockId(targetPosition.intX(), targetPosition.intY(), targetPosition.intZ());
			
			if(blockID > 0)
			{
				worldObj.setBlockWithNotify(targetPosition.intX(), targetPosition.intY(), targetPosition.intZ(), 0);
	            Block.blocksList[blockID].onBlockDestroyedByExplosion(worldObj, targetPosition.intX(), targetPosition.intY(), targetPosition.intZ());
			}
		}
		*/
		
		Condensed.doBaoZha(worldObj, position, explosionSource);

		
		
		for(int i = 0; i < 6; i ++)
		{
			position.add(difference);
			position.add(difference);
			Condensed.doBaoZha(worldObj, position, explosionSource);
		}
    }
	
	@Override
	public void init()
	{
        RecipeManager.addRecipe(this.getItemStack(2), new Object [] {"@", "@", "@", '@', Condensed.getItemStack()}, this.getMing(), ICBM.CONFIGURATION, true);
	}
}
