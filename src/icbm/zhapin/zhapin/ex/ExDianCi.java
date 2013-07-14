package icbm.zhapin.zhapin.ex;

import icbm.core.SheDing;
import icbm.core.di.MICBM;
import icbm.zhapin.baozha.bz.BzDianCi;
import icbm.zhapin.muoxing.daodan.MMDianCi;
import icbm.zhapin.zhapin.daodan.DaoDan;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import universalelectricity.prefab.RecipeHelper;
import calclavia.lib.UniversalRecipes;

public class ExDianCi extends DaoDan {
	public ExDianCi(String mingZi, int tier) {
		super(mingZi, tier);
	}

	@Override
	public void doCreateExplosion(World world, double x, double y, double z,
			Entity entity) {
		new BzDianCi(world, entity, x, y, z, 50).setEffectBlocks()
				.setEffectEntities().explode();
	}

	@Override
	public void init() {
		RecipeHelper.addRecipe(
				new ShapedOreRecipe(this.getItemStack(), new Object[] { "RBR",
						"BTB", "RBR", 'T', tui.getItemStack(), 'R',
						Block.blockRedstone, 'B', UniversalRecipes.BATTERY }),
				this.getUnlocalizedName(), SheDing.CONFIGURATION, true);
	}

	@Override
	public MICBM getMuoXing() {
		return new MMDianCi();
	}
}
