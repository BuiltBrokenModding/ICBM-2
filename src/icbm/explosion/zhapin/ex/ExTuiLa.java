package icbm.explosion.zhapin.ex;

import icbm.core.ICBMConfiguration;
import icbm.core.base.ModelICBM;
import icbm.explosion.explosive.explosion.BzYaSuo;
import icbm.explosion.model.missiles.MMLa;
import icbm.explosion.model.missiles.MMTui;
import icbm.explosion.zhapin.Explosive;
import icbm.explosion.zhapin.daodan.Missile;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import universalelectricity.prefab.RecipeHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ExTuiLa extends Missile
{
	public ExTuiLa(String mingZi, int tier)
	{
		super(mingZi, tier);
		this.setYinXin(120);
	}

	@Override
	public void init()
	{
		if (this.getID() == Explosive.la.getID())
		{
			RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[] { "YY", 'Y', Explosive.yaSuo.getItemStack() }), this.getUnlocalizedName(), ICBMConfiguration.CONFIGURATION, true);
		}
		else
		{
			RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[] { "Y", "Y", 'Y', Explosive.yaSuo.getItemStack() }), this.getUnlocalizedName(), ICBMConfiguration.CONFIGURATION, true);
		}
	}

	@Override
	public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
	{
		if (this.getID() == Explosive.la.getID())
		{
			new BzYaSuo(world, entity, x, y, z, 2f).setDestroyItems().setPushType(1).explode();
		}
		else
		{
			new BzYaSuo(world, entity, x, y, z, 2f).setDestroyItems().setPushType(2).explode();

		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public ModelICBM getMissileModel()
	{
		if (this.getID() == Explosive.la.getID())
		{
			return new MMLa();
		}
		else
		{
			return new MMTui();
		}
	}
}
