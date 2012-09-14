package icbm.zhapin;

import icbm.ICBM;

import java.util.List;

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.Block;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.Item;
import net.minecraft.src.World;
import universalelectricity.Vector3;
import universalelectricity.recipe.RecipeManager;

public class EDu extends ZhaPin
{
	public EDu(String name, int ID, int tier)
	{
		super(name, ID, tier);
	}

	/**
	 * World worldObj, Vector3 position, int radius, boolean isContagious
	 */
	@Override
	public void doExplosion(World worldObj, Vector3 position, Entity explosionSource)
	{		
		boolean isContagious = false;
		int radius = 14;
		
		if(this.getTier() == 2)
		{
			isContagious = true;
			radius = 20;
		}
		
		if(explosionSource instanceof EShouLiuDan)
		{
			radius /= 2;
		}
		
        AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(position.x - radius, position.y - radius, position.z - radius, position.x + radius, position.y + radius, position.z + radius);
        List<EntityLiving> entitiesNearby = worldObj.getEntitiesWithinAABB(EntityLiving.class, bounds);

        for(EntityLiving entity : entitiesNearby)
        {
        	if(isContagious)
        	{
        		ICBM.yi2Chuan2Du2.poisonEntity(entity);
        	}
        	else
        	{
        		ICBM.du2.poisonEntity(entity);
        	}
        }
        
        worldObj.playSoundEffect(position.x + 0.5D, position.y + 0.5D, position.z + 0.5D, "icbm.gasleak", 4.0F, (1.0F + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.2F)*1F);
	
        if(isContagious)
        {
    		ZhaPin.Mutation.doExplosion(worldObj, position, null, radius, -1);
        }
	}
	
	@Override
	public void addCraftingRecipe()
	{
		if(this.getTier() == 1)
		{
	        RecipeManager.addRecipe(this.getItemStack(), new Object [] {"@@@", "@?@", "@@@", '@', ICBM.itemDu, '?', Block.tnt});
		}
		else if(this.getTier() == 2)
		{
	        RecipeManager.addRecipe(this.getItemStack(), new Object [] {" @ ", "@?@", " @ ", '?', Item.rottenFlesh, '@', Chemical.getItemStack()});
		}
	}
}
