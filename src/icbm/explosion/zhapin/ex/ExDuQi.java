package icbm.explosion.zhapin.ex;

import icbm.core.ICBMConfiguration;
import icbm.core.ICBMCore;
import icbm.core.base.ModelICBM;
import icbm.explosion.explosive.explosion.BzQi;
import icbm.explosion.model.missiles.MMDuQi;
import icbm.explosion.model.missiles.MMGanRanDu;
import icbm.explosion.zhapin.daodan.DaoDan;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import universalelectricity.prefab.RecipeHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ExDuQi extends DaoDan
{
	public ExDuQi(String mingZi, int tier)
	{
		super(mingZi, tier);
	}

	@Override
	public void init()
	{
		if (this.getTier() == 1)
		{
			RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[] { "@@@", "@?@", "@@@", '@', ICBMCore.itDu, '?', wuQi.getItemStack() }), "Chemical", ICBMConfiguration.CONFIGURATION, true);
		}
		else if (this.getTier() == 2)
		{
			RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(2), new Object[] { " @ ", "@?@", " @ ", '?', Item.rottenFlesh, '@', duQi.getItemStack() }), "Contagious", ICBMConfiguration.CONFIGURATION, true);
		}
	}

	@Override
	public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
	{
		if (this.getTier() == 1)
		{
			new BzQi(world, entity, x, y, z, 20, 20 * 30, false).setPoison().setRGB(0.8f, 0.8f, 0).explode();
		}
		else if (this.getTier() == 2)
		{
			new BzQi(world, entity, x, y, z, 20, 20 * 30, false).setContagious().setRGB(0.3f, 0.8f, 0).explode();
		}

	}

	@Override
	@SideOnly(Side.CLIENT)
	public ModelICBM getMissileModel()
	{
		if (this.getTier() == 1)
		{
			return new MMDuQi();
		}
		else if (this.getTier() == 2)
		{
			return new MMGanRanDu();
		}

		return null;
	}

}
