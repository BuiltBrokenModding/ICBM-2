package icbm.zhapin.zhapin.ex;

import icbm.core.ZhuYaoBase;
import icbm.zhapin.ZhuYaoZhaPin;
import icbm.zhapin.zhapin.ZhaPin;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import universalelectricity.core.item.ElectricItemHelper;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.RecipeHelper;
import calclavia.lib.UniversalRecipes;

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
		if (radius < 0)
		{
			radius = (int) this.getRadius();
		}

		ZhaPin.dianCiSignal.doBaoZha(worldObj, position, explosionSource, radius, callCount);
		ZhaPin.dianCiWave.doBaoZha(worldObj, position, explosionSource, radius, callCount);

		ZhuYaoZhaPin.proxy.spawnParticle("shockwave", worldObj, position, 0, 0, 0, 0, 0, 255, 10, 3);

		return false;
	}

	@Override
	public void init()
	{
		if (OreDictionary.getOres(UniversalRecipes.BATTERY).size() > 0)
		{
			RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[] { "RBR", "BTB", "RBR", 'T', tui.getItemStack(), 'R', Block.blockRedstone, 'B', ElectricItemHelper.getUncharged(OreDictionary.getOres(UniversalRecipes.BATTERY).get(0)) }), this.getUnlocalizedName(), ZhuYaoBase.CONFIGURATION, true);
		}
	}

	@Override
	public float getRadius()
	{
		return 50;
	}

	@Override
	public double getEnergy()
	{
		return 0;
	}
}
