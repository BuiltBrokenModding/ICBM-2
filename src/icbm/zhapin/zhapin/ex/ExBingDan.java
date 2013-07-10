package icbm.zhapin.zhapin.ex;

import icbm.core.ZhuYaoICBM;
import icbm.zhapin.baozha.bz.BzBingDan;
import icbm.zhapin.zhapin.ZhaPin;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import universalelectricity.prefab.RecipeHelper;

public class ExBingDan extends ZhaPin
{
	public ExBingDan(String mingZi, int tier)
	{
		super(mingZi, tier);
	}

	@Override
	public void init()
	{
		RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[] { "?!?", "!@!", "?!?", '@', la.getItemStack(), '?', Block.ice, '!', Block.blockSnow }), this.getUnlocalizedName(), ZhuYaoICBM.CONFIGURATION, true);
	}

	@Override
	public void createExplosion(World world, double x, double y, double z, Entity entity)
	{
		new BzBingDan(world, entity, x, y, z, 50).explode();
	}
}
