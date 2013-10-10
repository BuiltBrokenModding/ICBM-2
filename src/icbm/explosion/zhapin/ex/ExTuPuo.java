package icbm.explosion.zhapin.ex;

import icbm.core.ICBMConfiguration;
import icbm.core.base.ModelICBM;
import icbm.explosion.explosive.explosion.BzTuPuo;
import icbm.explosion.model.missiles.MMTuPuo;
import icbm.explosion.zhapin.Explosive;
import icbm.explosion.zhapin.daodan.DaoDan;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import universalelectricity.prefab.RecipeHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

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
		RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(2), new Object[] { "GCG", "GCG", "GCG", 'C', Explosive.yaSuo.getItemStack(), 'G', Item.gunpowder }), this.getUnlocalizedName(), ICBMConfiguration.CONFIGURATION, true);
	}

	@Override
	public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
	{
		new BzTuPuo(world, entity, x, y, z, 2.5f, 7).explode();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public ModelICBM getMissileModel()
	{
		return new MMTuPuo();
	}
}
