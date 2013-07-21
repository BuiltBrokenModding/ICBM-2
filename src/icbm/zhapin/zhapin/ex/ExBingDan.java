package icbm.zhapin.zhapin.ex;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import icbm.core.MICBM;
import icbm.core.SheDing;
import icbm.zhapin.baozha.bz.BzBingDan;
import icbm.zhapin.muoxing.daodan.MMBingDan;
import icbm.zhapin.zhapin.daodan.DaoDan;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import universalelectricity.prefab.RecipeHelper;

public class ExBingDan extends DaoDan
{
	public ExBingDan(String mingZi, int tier)
	{
		super(mingZi, tier);
	}

	@Override
	public void init()
	{
		RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[] { "?!?", "!@!", "?!?", '@', la.getItemStack(), '?', Block.ice, '!', Block.blockSnow }), this.getUnlocalizedName(), SheDing.CONFIGURATION, true);
	}

	@Override
	public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
	{
		new BzBingDan(world, entity, x, y, z, 50).explode();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public MICBM getMuoXing()
	{
		return new MMBingDan();
	}
}
