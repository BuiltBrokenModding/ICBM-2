package icbm.zhapin;

import net.minecraft.src.Block;
import net.minecraft.src.Entity;
import net.minecraft.src.Item;
import net.minecraft.src.World;
import universalelectricity.Vector3;
import universalelectricity.basiccomponents.BasicComponents;
import universalelectricity.extend.ItemElectric;
import universalelectricity.recipe.RecipeManager;

public class ExDianCi extends ZhaPin
{
	public ExDianCi(String name, int ID, int tier)
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
		
		if(explosionSource instanceof EShouLiuDan)
		{
			radius /= 2;
		}
		
		ZhaPin.EMPSignal.doExplosion(worldObj, position, null, radius, callCount);
		ZhaPin.EMPWave.doExplosion(worldObj, position, null, radius, callCount);
    	return false;
	}

	@Override
	public void addCraftingRecipe()
	{
        RecipeManager.addRecipe(this.getItemStack(), new Object [] {"?!?", "!@!", "?!?", '@', Block.tnt, '?', Item.redstone, '!', ((ItemElectric)BasicComponents.itemBattery).getUnchargedItemStack()});		
	}
}
