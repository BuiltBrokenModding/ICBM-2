package icbm.zhapin.baozha.ex;

import icbm.core.ZhuYaoICBM;
import icbm.core.di.MICBM;
import icbm.zhapin.muoxing.daodan.MMDuQi;
import icbm.zhapin.muoxing.daodan.MMGanRanDu;
import icbm.zhapin.zhapin.daodan.DaoDan;
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
			RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[] { "@@@", "@?@", "@@@", '@', ZhuYaoICBM.itDu, '?', wuQi.getItemStack() }), "Chemical", ZhuYaoICBM.CONFIGURATION, true);
		}
		else if (this.getTier() == 2)
		{
			RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(2), new Object[] { " @ ", "@?@", " @ ", '?', Item.rottenFlesh, '@', duQi.getItemStack() }), "Contagious", ZhuYaoICBM.CONFIGURATION, true);
		}
	}

	@Override
	public void createExplosion(World world, double x, double y, double z, Entity entity)
	{
		if (this.getTier() == 1)
		{
			this.spawnEZhaPin(new BzQi(world, entity, x, y, z, 20, 20 * 30, false).setPoison().setRGB(0.8f, 0.8f, 0));
		}
		else if (this.getTier() == 2)
		{
			this.spawnEZhaPin(new BzQi(world, entity, x, y, z, 20, 20 * 30, false).setContagious().setRGB(0.3f, 0.8f, 0));
		}

	}

	@SideOnly(Side.CLIENT)
	public MICBM getMuoXing()
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
