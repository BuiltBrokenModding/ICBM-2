package icbm.explosion.zhapin.ex;

import icbm.core.ICBMConfiguration;
import icbm.core.base.ModelICBM;
import icbm.explosion.explosive.explosion.BzQi;
import icbm.explosion.model.missiles.MMWuQi;
import icbm.explosion.zhapin.Explosive;
import icbm.explosion.zhapin.missile.Missile;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import universalelectricity.prefab.RecipeHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ExDebilitation extends Missile
{
	public ExDebilitation(String mingZi, int tier)
	{
		super(mingZi, tier);
	}

	@Override
	public void init()
	{
		RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(3), new Object[] { "SSS", "WRW", "SSS", 'R', Explosive.tui.getItemStack(), 'W', Item.bucketWater, 'S', "dustSulfur" }), this.getUnlocalizedName(), ICBMConfiguration.CONFIGURATION, true);
	}

	@Override
	public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
	{
		new BzQi(world, entity, x, y, z, 20, 20 * 30, false).setConfuse().explode();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ModelICBM getMissileModel()
	{
		return new MMWuQi();
	}
}
