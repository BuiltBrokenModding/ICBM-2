package icbm.explosion.zhapin.ex;

import icbm.core.ICBMConfiguration;
import icbm.core.base.ModelICBM;
import icbm.explosion.explosive.explosion.BzHongSu;
import icbm.explosion.model.missiles.MMHongSu;
import icbm.explosion.zhapin.daodan.Missile;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import universalelectricity.prefab.RecipeHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ExHongSu extends Missile
{
	public ExHongSu(String mingZi, int tier)
	{
		super(mingZi, tier);
	}

	@Override
	public void init()
	{
		RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[] { "AAA", "AEA", "AAA", 'E', fanWuSu.getItemStack(), 'A', "strangeMatter" }), this.getUnlocalizedName(), ICBMConfiguration.CONFIGURATION, true);
	}

	@Override
	public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
	{
		new BzHongSu(world, entity, x, y, z, 35).explode();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public ModelICBM getMissileModel()
	{
		return new MMHongSu();
	}

}
