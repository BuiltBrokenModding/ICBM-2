package icbm.zhapin;

import icbm.ICBM;
import net.minecraft.src.Block;
import net.minecraft.src.Entity;
import net.minecraft.src.Item;
import net.minecraft.src.World;
import universalelectricity.BasicComponents;
import universalelectricity.prefab.ItemElectric;
import universalelectricity.prefab.Vector3;
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
	public boolean doBaoZha(World worldObj, Vector3 position, Entity explosionSource, int radius, int callCount)
	{
		if(radius < 0) radius = 30;
		
		ZhaPin.EMPSignal.doBaoZha(worldObj, position, null, radius, callCount);
		ZhaPin.EMPWave.doBaoZha(worldObj, position, null, radius, callCount);
    	return false;
	}

	@Override
	public void init()
	{
        RecipeManager.addRecipe(this.getItemStack(), new Object [] {"?!?", "!@!", "?!?", '@', Block.tnt, '?', Item.redstone, '!', ((ItemElectric)BasicComponents.itemBattery).getUnchargedItemStack()}, ICBM.CONFIGURATION, true);		
	}
}
