package icbm.zhapin.zhapin.ex;

import icbm.core.SheDing;
import icbm.core.di.MICBM;
import icbm.zhapin.baozha.bz.BzShengBuo;
import icbm.zhapin.muoxing.daodan.MMChaoShengBuo;
import icbm.zhapin.muoxing.daodan.MMShengBuo;
import icbm.zhapin.zhapin.ZhaPin;
import icbm.zhapin.zhapin.daodan.DaoDan;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import universalelectricity.prefab.RecipeHelper;
import calclavia.lib.UniversalRecipes;

public class ExShengBuo extends DaoDan {
	public ExShengBuo(String mingZi, int tier) {
		super(mingZi, tier);
	}

	@Override
	public void init() {
		if (this.getTier() == 3) {
			RecipeHelper.addRecipe(
					new ShapedOreRecipe(this.getItemStack(), new Object[] {
							" S ", "S S", " S ", 'S',
							ZhaPin.shengBuo.getItemStack() }),
					this.getUnlocalizedName(), SheDing.CONFIGURATION, true);
		} else {
			RecipeHelper.addRecipe(
					new ShapedOreRecipe(this.getItemStack(), new Object[] {
							"@?@", "?R?", "@?@", 'R',
							ZhaPin.tui.getItemStack(), '?', Block.music, '@',
							UniversalRecipes.SECONDARY_METAL }),
					this.getUnlocalizedName(), SheDing.CONFIGURATION, true);
		}
	}

	@Override
	public void doCreateExplosion(World world, double x, double y, double z,
			Entity entity) {
		if (this.getTier() == 3) {
			new BzShengBuo(world, entity, x, y, z, 15, 30).setShockWave()
					.explode();
		} else {
			new BzShengBuo(world, entity, x, y, z, 10, 25).explode();
		}
	}

	@Override
	public MICBM getMuoXing() {
		if (this.getTier() == 3) {
			return new MMChaoShengBuo();
		} else {
			return new MMShengBuo();
		}
	}

}
