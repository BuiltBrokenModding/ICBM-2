package icbm.zhapin.zhapin.ex;

import icbm.core.SheDing;
import icbm.core.di.MICBM;
import icbm.zhapin.baozha.bz.BzQi;
import icbm.zhapin.muoxing.daodan.MMWuQi;
import icbm.zhapin.zhapin.ZhaPin;
import icbm.zhapin.zhapin.daodan.DaoDan;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import universalelectricity.prefab.RecipeHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ExWuQi extends DaoDan
{
	public ExWuQi(String mingZi, int tier)
	{
		super(mingZi, tier);
	}

	@Override
	public void init()
	{
		RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(3), new Object[] { "SSS", "WRW", "SSS", 'R', ZhaPin.tui.getItemStack(), 'W', Item.bucketWater, 'S', "dustSulfur" }), this.getUnlocalizedName(), SheDing.CONFIGURATION, true);
	}

	@Override
	public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
	{
		new BzQi(world, entity, x, y, z, 20, 20 * 30, false).setConfuse().explode();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public MICBM getMuoXing()
	{
		return new MMWuQi();
	}
}
