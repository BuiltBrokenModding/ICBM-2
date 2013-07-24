package icbm.zhapin.zhapin.ex;

import icbm.core.SheDing;
import icbm.core.base.MICBM;
import icbm.zhapin.baozha.bz.BzHongSu;
import icbm.zhapin.muoxing.daodan.MMHongSu;
import icbm.zhapin.zhapin.daodan.DaoDan;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import universalelectricity.prefab.RecipeHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ExHongSu extends DaoDan
{
	public ExHongSu(String mingZi, int tier)
	{
		super(mingZi, tier);
	}

	@Override
	public void init()
	{
		RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[] { "AAA", "AEA", "AAA", 'E', fanWuSu.getItemStack(), 'A', "strangeMatter" }), this.getUnlocalizedName(), SheDing.CONFIGURATION, true);
	}

	@Override
	public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
	{
		new BzHongSu(world, entity, x, y, z, 35).explode();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public MICBM getMissileModel()
	{
		return new MMHongSu();
	}

}
