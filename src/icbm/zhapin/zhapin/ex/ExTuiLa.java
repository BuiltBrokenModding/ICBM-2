package icbm.zhapin.zhapin.ex;

import icbm.core.SheDing;
import icbm.core.di.MICBM;
import icbm.zhapin.baozha.bz.BzYaSuo;
import icbm.zhapin.muoxing.daodan.MMLa;
import icbm.zhapin.muoxing.daodan.MMTui;
import icbm.zhapin.zhapin.ZhaPin;
import icbm.zhapin.zhapin.daodan.DaoDan;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import universalelectricity.prefab.RecipeHelper;

public class ExTuiLa extends DaoDan
{
	public ExTuiLa(String mingZi, int tier)
	{
		super(mingZi, tier);
		this.setYinXin(120);
	}

	@Override
	public void init()
	{
		if (this.getID() == ZhaPin.la.getID())
		{
			RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[] { "YY", 'Y', ZhaPin.yaSuo.getItemStack() }), this.getUnlocalizedName(), SheDing.CONFIGURATION, true);
		}
		else
		{
			RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[] { "Y", "Y", 'Y', ZhaPin.yaSuo.getItemStack() }), this.getUnlocalizedName(), SheDing.CONFIGURATION, true);
		}
	}

	@Override
	public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
	{
		if (this.getID() == ZhaPin.la.getID())
		{
			new BzYaSuo(world, entity, x, y, z, 2f).setDestroyItems().setPushType(1).explode();
		}
		else
		{
			new BzYaSuo(world, entity, x, y, z, 2f).setDestroyItems().setPushType(2).explode();

		}
	}

	@Override
	public MICBM getMuoXing()
	{
		if (this.getID() == ZhaPin.la.getID())
		{
			return new MMLa();
		}
		else
		{
			return new MMTui();
		}
	}
}
