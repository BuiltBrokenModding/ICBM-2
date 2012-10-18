package icbm.zhapin.ex;

import icbm.ZhuYao;
import icbm.zhapin.ZhaPin;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.src.Block;
import net.minecraft.src.ChunkPosition;
import net.minecraft.src.Entity;
import net.minecraft.src.Item;
import net.minecraft.src.MathHelper;
import net.minecraft.src.World;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.implement.IRotatable;
import universalelectricity.prefab.Vector3;
import universalelectricity.recipe.RecipeManager;
import chb.mods.mffs.api.IForceFieldBlock;

public class ExTuPuo extends ZhaPin
{
	private static final float NENG_LIANG = 7f;;
	private static final int MAX_BAN_JING = 16;
    private static final float BAN_JING = 2f;

	public ExTuPuo(String name, int ID, int tier)
	{
		super(name, ID, tier);
		this.setYinXin(10);
	}

	@Override
	public void doBaoZha(World worldObj, Vector3 position, Entity explosionSource)
	{
		if(!worldObj.isRemote)
		{			
			Vector3 difference = new Vector3();
			
			if(explosionSource instanceof IRotatable)
			{
				difference.modifyPositionFromSide(((IRotatable)explosionSource).getDirection());
			}
			else
			{
				difference.modifyPositionFromSide(ForgeDirection.DOWN);
			}
			
	        worldObj.playSoundEffect(position.x, position.y, position.z, "random.explode", 5.0F, (1.0F + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.2F) * 0.7F);
	        
	        Vector3 targetPosition = position.clone();
	        
			for(int i = 0; i < 8; i ++)
			{	
				if(Block.blocksList[targetPosition.getBlockID(worldObj)] != null)
				{
					if(Block.blocksList[targetPosition.getBlockID(worldObj)].getExplosionResistance(explosionSource, worldObj, targetPosition.intX(), targetPosition.intY(), targetPosition.intZ(), position.x, position.y, position.z) > Block.obsidian.getExplosionResistance(explosionSource)
							|| Block.blocksList[targetPosition.getBlockID(worldObj)] instanceof IForceFieldBlock)
					{
						break;
					}
				}
				
				List blownBlocks = new ArrayList();
				
		        for (int x = 0; x < MAX_BAN_JING; ++x)
		        {
		            for (int y = 0; y < MAX_BAN_JING; ++y)
		            {
		                for (int z = 0; z < MAX_BAN_JING; ++z)
		                {
		                    if (x == 0 || x == MAX_BAN_JING - 1 || y == 0 || y == MAX_BAN_JING - 1 || z == 0 || z == MAX_BAN_JING - 1)
		                    {
		                        double xStep = (double)((float)x / ((float)MAX_BAN_JING - 1.0F) * 2.0F - 1.0F);
		                        double yStep = (double)((float)y / ((float)MAX_BAN_JING - 1.0F) * 2.0F - 1.0F);
		                        double zStep = (double)((float)z / ((float)MAX_BAN_JING - 1.0F) * 2.0F - 1.0F);
		                        double diagonalDistance = Math.sqrt(xStep * xStep + yStep * yStep + zStep * zStep);
		                        xStep /= diagonalDistance;
		                        yStep /= diagonalDistance;
		                        zStep /= diagonalDistance;
		                        float var14 = BAN_JING * (0.7F + worldObj.rand.nextFloat() * 0.6F);
		                        double var15 = targetPosition.x;
		                        double var17 = targetPosition.y;
		                        double var19 = targetPosition.z;
		
		                        for (float var21 = 0.3F; var14 > 0.0F; var14 -= var21 * 0.75F)
		                        {
		                            int var22 = MathHelper.floor_double(var15);
		                            int var23 = MathHelper.floor_double(var17);
		                            int var24 = MathHelper.floor_double(var19);
		                            int var25 = worldObj.getBlockId(var22, var23, var24);
		
		                            if (var25 > 0)
		                            {
		                                var14 -= (Block.blocksList[var25].getExplosionResistance(explosionSource, worldObj, var22, var23, var24, targetPosition.intX(), targetPosition.intY(), targetPosition.intZ()) + 0.3F) * var21;
		                            }
		
		                            if (var14 > 0.0F)
		                            {
		                            	blownBlocks.add(new ChunkPosition(var22, var23, var24));
		                            }
		
		                            var15 += xStep * (double)var21;
		                            var17 += yStep * (double)var21;
		                            var19 += zStep * (double)var21;
		                        }
		                    }
		                }
		            }
		        }
		        
		        doDamageEntities(worldObj, targetPosition, BAN_JING, NENG_LIANG/2, false);
		        
		        int var3;
		        ChunkPosition var4;
		        int var5;
		        int var6;
		        int var7;
		        int blockID;
		        int metadata;
		
		        for(var3 = blownBlocks.size() - 1; var3 >= 0; --var3)
		        {
		            var4 = (ChunkPosition)blownBlocks.get(var3);
		            var5 = var4.x;
		            var6 = var4.y;
		            var7 = var4.z;
		            blockID = worldObj.getBlockId(var5, var6, var7);
		            metadata = worldObj.getBlockMetadata(var5, var6, var7);
		
		            double var9 = (var5 + worldObj.rand.nextFloat());
		            double var11 = (var6 + worldObj.rand.nextFloat());
		            double var13 = (var7 + worldObj.rand.nextFloat());
		            double var151 = var9 - targetPosition.y;
		            double var171 = var11 - targetPosition.y;
		            double var191 = var13 - targetPosition.z;
		            double var211 = MathHelper.sqrt_double(var151 * var151 + var171 * var171 + var191 * var191);
		            var151 /= var211;
		            var171 /= var211;
		            var191 /= var211;
		            double var23 = 0.5D / (var211 / BAN_JING + 0.1D);
		            var23 *= (worldObj.rand.nextFloat() * worldObj.rand.nextFloat() + 0.3F);
		            var151 *= var23;
		            var171 *= var23;
		            var191 *= var23;
		            worldObj.spawnParticle("explode", (var9 + targetPosition.x * 1.0D) / 2.0D, (var11 + targetPosition.y * 1.0D) / 2.0D, (var13 + targetPosition.z * 1.0D) / 2.0D, var151, var171, var191);
		            worldObj.spawnParticle("smoke", var9, var11, var13, var151, var171, var191);
		            
		
		            if(blockID > 0)
		            {    	
		                Block.blocksList[blockID].dropBlockAsItemWithChance(worldObj, var5, var6, var7, worldObj.getBlockMetadata(var5, var6, var7), 1F, 0);
		                Block.blocksList[blockID].onBlockDestroyedByExplosion(worldObj, var5, var6, var7);
		                worldObj.setBlockWithNotify(var5, var6, var7, 0);
		            }
		        }
		        
		        targetPosition.add(difference);
			}
		}
    }
	
	@Override
	public void init()
	{
        RecipeManager.addRecipe(this.getItemStack(2), new Object [] {"GCG", "GCG", "GCG", 'C', yaSuo.getItemStack(), 'G', Item.gunpowder}, this.getMing(), ZhuYao.CONFIGURATION, true);
	}
}
