package icbm.zhapin.zhapin.ex;

import icbm.core.MICBM;
import icbm.core.SheDing;
import icbm.zhapin.baozha.bz.BzTuPuo;
import icbm.zhapin.muoxing.daodan.MMTuPuo;
import icbm.zhapin.zhapin.daodan.DaoDan;
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
		RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(2), new Object[] { "GCG", "GCG", "GCG", 'C', yaSuo.getItemStack(), 'G', Item.gunpowder }), this.getUnlocalizedName(), SheDing.CONFIGURATION, true);
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
