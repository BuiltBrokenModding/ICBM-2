package icbm.explosion.missile.ex;

import icbm.core.ICBMConfiguration;
import icbm.core.base.ModelICBM;
import icbm.explosion.explosive.explosion.ExplosionEmp;
import icbm.explosion.model.missiles.MMDianCi;
import icbm.explosion.model.missiles.Missile;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import universalelectricity.prefab.RecipeHelper;
import calclavia.lib.UniversalRecipes;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ExEMP extends Missile
{
	public ExEMP(String mingZi, int tier)
	{
		super(mingZi, tier);
	}

	@Override
	public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
	{
		new ExplosionEmp(world, entity, x, y, z, 50).setEffectBlocks().setEffectEntities().explode();
	}

	@Override
	public void init()
	{
		RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[] { "RBR", "BTB", "RBR", 'T', replsive.getItemStack(), 'R', Block.blockRedstone, 'B', UniversalRecipes.BATTERY }), this.getUnlocalizedName(), ICBMConfiguration.CONFIGURATION, true);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public ModelICBM getMissileModel()
	{
		return new MMDianCi();
	}
}
