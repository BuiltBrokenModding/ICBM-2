package icbm.zhapin.ex;

import icbm.ZhuYao;
import icbm.zhapin.ZhaPin;
import net.minecraft.src.Block;
import net.minecraft.src.Entity;
import net.minecraft.src.Item;
import net.minecraft.src.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.RecipeHelper;

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
			radius = 40;
		}

		ZhaPin.dianCiSignal.doBaoZha(worldObj, position, null, radius, callCount);
		ZhaPin.dianCiWave.doBaoZha(worldObj, position, null, radius, callCount);
		return false;
	}

	@Override
	public void init()
	{
		RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[] { "RBR", "BTB", "RBR", 'T', Block.tnt, 'R', Item.redstone, 'B', "battery" }), this.getMing(), ZhuYao.CONFIGURATION, true);
	}
}
