package icbm.zhapin.zhapin.ex;

import icbm.core.ZhuYaoICBM;
import icbm.zhapin.baozha.bz.BzYaSuo;
import icbm.zhapin.zhapin.ZhaPin;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import universalelectricity.prefab.RecipeHelper;

public class ExTuiLa extends ZhaPin
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
			RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[] { "YY", 'Y', ZhaPin.yaSuo.getItemStack() }), this.getUnlocalizedName(), ZhuYaoICBM.CONFIGURATION, true);
		}
		else
		{
			RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[] { "Y", "Y", 'Y', ZhaPin.yaSuo.getItemStack() }), this.getUnlocalizedName(), ZhuYaoICBM.CONFIGURATION, true);
		}
	}

	@Override
	public void createExplosion(World world, double x, double y, double z, Entity entity)
	{
		if (this.getID() == ZhaPin.la.getID())
		{
			new BzYaSuo(world, entity, x, y, z, 2f).setPushType(1).explode();
		}
		else
		{
			new BzYaSuo(world, entity, x, y, z, 2f).setPushType(2).explode();

		}
	}
}
