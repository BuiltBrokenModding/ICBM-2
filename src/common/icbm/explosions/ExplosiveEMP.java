package icbm.explosions;

import icbm.EntityGrenade;
import icbm.ICBM;
import net.minecraft.src.Block;
import net.minecraft.src.Entity;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import universalelectricity.Vector3;
import universalelectricity.basiccomponents.BasicComponents;
import universalelectricity.recipe.RecipeManager;

public class ExplosiveEMP extends Explosive
{
	public ExplosiveEMP(String name, int ID, int tier)
	{
		super(name, ID, tier);
	}

	/**
	 * World worldObj, Vector3 position, int amount, boolean isExplosive
	 */
	@Override
	public boolean doExplosion(World worldObj, Vector3 position, Entity explosionSource, int radius, int callCount)
	{
		if(radius < 0) radius = 25;
		
		if(explosionSource instanceof EntityGrenade)
		{
			radius /= 2;
		}
		
		Explosive.EMPSignal.doExplosion(worldObj, position, null, radius, callCount);
		Explosive.EMPWave.doExplosion(worldObj, position, null, radius, callCount);
    	return false;
	}

	@Override
	public void addCraftingRecipe()
	{
        RecipeManager.addRecipe(this.getItemStack(), new Object [] {"?!?", "!@!", "?!?", '@', Block.tnt, '?', Item.redstone, '!', BasicComponents.itemBattery});		
	}
}
