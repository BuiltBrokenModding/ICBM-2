package icbm.zhapin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import net.minecraft.src.Block;
import net.minecraft.src.ChunkPosition;
import net.minecraft.src.Entity;
import net.minecraft.src.Item;
import net.minecraft.src.MathHelper;
import net.minecraft.src.World;
import universalelectricity.prefab.Vector3;
import universalelectricity.recipe.RecipeManager;

public class ExYaSuo extends ZhaPin
{
    private int field_77289_h = 16;

	public ExYaSuo(String name, int ID, int tier)
	{
		super(name, ID, tier);
		this.setYinXin(1);
	}

	@Override
	public void doBaoZha(World worldObj, Vector3 position, Entity explosionSource)
	{
		if(!worldObj.isRemote)
		{
		    List blownBlocks = new ArrayList();
	
			float radius = 2F;
			float power = 9F;
					
	        HashSet var2 = new HashSet();
	        int x;
	        int y;
	        int z;
	        double var15;
	        double var17;
	        double var19;
	
	        for (x = 0; x < this.field_77289_h; ++x)
	        {
	            for (y = 0; y < this.field_77289_h; ++y)
	            {
	                for (z = 0; z < this.field_77289_h; ++z)
	                {
	                    if (x == 0 || x == this.field_77289_h - 1 || y == 0 || y == this.field_77289_h - 1 || z == 0 || z == this.field_77289_h - 1)
	                    {
	                        double var6 = (double)((float)x / ((float)this.field_77289_h - 1.0F) * 2.0F - 1.0F);
	                        double var8 = (double)((float)y / ((float)this.field_77289_h - 1.0F) * 2.0F - 1.0F);
	                        double var10 = (double)((float)z / ((float)this.field_77289_h - 1.0F) * 2.0F - 1.0F);
	                        double var12 = Math.sqrt(var6 * var6 + var8 * var8 + var10 * var10);
	                        var6 /= var12;
	                        var8 /= var12;
	                        var10 /= var12;
	                        float var14 = radius * (0.7F + worldObj.rand.nextFloat() * 0.6F);
	                        var15 = position.x;
	                        var17 = position.y;
	                        var19 = position.z;
	
	                        for (float var21 = 0.3F; var14 > 0.0F; var14 -= var21 * 0.75F)
	                        {
	                            int var22 = MathHelper.floor_double(var15);
	                            int var23 = MathHelper.floor_double(var17);
	                            int var24 = MathHelper.floor_double(var19);
	                            int var25 = worldObj.getBlockId(var22, var23, var24);
	
	                            if (var25 > 0)
	                            {
	                                var14 -= (Block.blocksList[var25].getExplosionResistance(explosionSource, worldObj, var22, var23, var24, position.intX(), position.intY(), position.intZ()) + 0.3F) * var21;
	                            }
	
	                            if (var14 > 0.0F)
	                            {
	                                var2.add(new ChunkPosition(var22, var23, var24));
	                            }
	
	                            var15 += var6 * (double)var21;
	                            var17 += var8 * (double)var21;
	                            var19 += var10 * (double)var21;
	                        }
	                    }
	                }
	            }
	        }
	
	        blownBlocks.addAll(var2);
	        
	        doDamageEntities(worldObj, position, radius, power, false);
	
	        //Set 3: Drop blocks, play animations and sound fxs
	        worldObj.playSoundEffect(position.x, position.y, position.z, "random.explode", 4.0F, (1.0F + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.2F) * 0.7F);
	
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
	            double var151 = var9 - position.y;
	            double var171 = var11 - position.y;
	            double var191 = var13 - position.z;
	            double var211 = MathHelper.sqrt_double(var151 * var151 + var171 * var171 + var191 * var191);
	            var151 /= var211;
	            var171 /= var211;
	            var191 /= var211;
	            double var23 = 0.5D / (var211 / radius + 0.1D);
	            var23 *= (worldObj.rand.nextFloat() * worldObj.rand.nextFloat() + 0.3F);
	            var151 *= var23;
	            var171 *= var23;
	            var191 *= var23;
	            worldObj.spawnParticle("explode", (var9 + position.x * 1.0D) / 2.0D, (var11 + position.y * 1.0D) / 2.0D, (var13 + position.z * 1.0D) / 2.0D, var151, var171, var191);
	            worldObj.spawnParticle("smoke", var9, var11, var13, var151, var171, var191);
	            
	
	            if(blockID > 0)
	            {    	
	                Block.blocksList[blockID].dropBlockAsItemWithChance(worldObj, var5, var6, var7, worldObj.getBlockMetadata(var5, var6, var7), 1F, 0);
	                Block.blocksList[blockID].onBlockDestroyedByExplosion(worldObj, var5, var6, var7);
	                worldObj.setBlockWithNotify(var5, var6, var7, 0);
	            }
	        }
		}
    }
	
	public String getDaoDanMing() { return "Conventional Missile"; }
	
	@Override
	public void init()
	{
        RecipeManager.addRecipe(this.getItemStack(3), new Object [] {"@?@", '@', Block.tnt, '?', Item.redstone});
	}
}
