package icbm.zhapin.zhapin.ex;

import icbm.core.SheDing;
import icbm.core.di.MICBM;
import icbm.zhapin.baozha.bz.BzTuPuo;
import icbm.zhapin.muoxing.daodan.MMTuPuo;
import icbm.zhapin.zhapin.daodan.DaoDan;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import universalelectricity.prefab.RecipeHelper;

public class ExTuPuo extends DaoDan
{
	public ExTuPuo(String mingZi, int tier)
	{
		super(mingZi, tier);
		this.setYinXin(40);
	}

	@Override
	public void init()
	{
		RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(2), new Object[] { "GCG", "GCG", "GCG", 'C', yaSuo.getItemStack(), 'G', Item.gunpowder }), this.getUnlocalizedName(), SheDing.CONFIGURATION, true);
	}

	@Override
	public void createExplosion(World world, double x, double y, double z, Entity entity)
	{
		new BzTuPuo(world, entity, x, y, z, 2.5f, 7).explode();
	}

	@Override
	public MICBM getMuoXing()
	{
		return new MMTuPuo();
	}
}
