package icbm.explosion.zhapin.ex;

import icbm.core.SheDing;
import icbm.core.base.MICBM;
import icbm.explosion.baozha.bz.BzTuPuo;
import icbm.explosion.muoxing.daodan.MMTuPuo;
import icbm.explosion.zhapin.ZhaPin;
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
		RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(2), new Object[] { "GCG", "GCG", "GCG", 'C', ZhaPin.yaSuo.getItemStack(), 'G', Item.gunpowder }), this.getUnlocalizedName(), SheDing.CONFIGURATION, true);
	}

	@Override
	public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
	{
		new BzTuPuo(world, entity, x, y, z, 2.5f, 7).explode();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public MICBM getMissileModel()
	{
		return new MMTuPuo();
	}
}
