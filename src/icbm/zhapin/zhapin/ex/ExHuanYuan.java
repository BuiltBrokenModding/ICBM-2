package icbm.zhapin.zhapin.ex;

import icbm.core.SheDing;
import icbm.core.di.MICBM;
import icbm.zhapin.baozha.bz.BzHuanYuan;
import icbm.zhapin.muoxing.daodan.MMHuanYuan;
import icbm.zhapin.zhapin.daodan.DaoDan;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import universalelectricity.prefab.RecipeHelper;

public class ExHuanYuan extends DaoDan
{
	public ExHuanYuan(String mingZi, int tier)
	{
		super(mingZi, tier);
	}

	@Override
	public void init()
	{
		RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[] { "ICI", "CDC", "ICI", 'D', Block.blockDiamond, 'C', Item.pocketSundial, 'I', Block.blockIron }), this.getUnlocalizedName(), SheDing.CONFIGURATION, true);
	}

	@Override
	public void createExplosion(World world, double x, double y, double z, Entity entity)
	{
		new BzHuanYuan(world, entity, x, y, z, 16).doExplode();
	}

	@Override
	public MICBM getMuoXing()
	{
		return new MMHuanYuan();
	}
}
