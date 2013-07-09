package icbm.zhapin.baozha.ex;

import icbm.core.ZhuYaoICBM;
import icbm.zhapin.zhapin.ZhaPin;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import universalelectricity.prefab.RecipeHelper;

public class ExTuPuo extends ZhaPin
{
	public ExTuPuo(String mingZi, int tier)
	{
		super(mingZi, tier);
	}

	@Override
	public void init()
	{
		RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(2), new Object[] { "GCG", "GCG", "GCG", 'C', yaSuo.getItemStack(), 'G', Item.gunpowder }), this.getUnlocalizedName(), ZhuYaoICBM.CONFIGURATION, true);
	}

	@Override
	public void createExplosion(World world, double x, double y, double z, Entity entity)
	{
		new BzTuPuo(world, entity, x, y, z, 6).explode();
	}
}
