package icbm.zhapin.zhapin.ex;

import icbm.core.SheDing;
import icbm.core.base.MICBM;
import icbm.zhapin.ZhuYaoZhaPin;
import icbm.zhapin.baozha.bz.BzDianCi;
import icbm.zhapin.muoxing.daodan.MMDianCi;
import icbm.zhapin.zhapin.daodan.DaoDan;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.RecipeHelper;
import calclavia.lib.UniversalRecipes;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ExDianCi extends DaoDan
{
	public ExDianCi(String mingZi, int tier)
	{
		super(mingZi, tier);
	}

	@Override
	public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
	{
		new BzDianCi(world, entity, x, y, z, 50).setEffectBlocks().setEffectEntities().explode();
	}

	@Override
	public void init()
	{
		RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[] { "RBR", "BTB", "RBR", 'T', tui.getItemStack(), 'R', Block.blockRedstone, 'B', UniversalRecipes.BATTERY }), this.getUnlocalizedName(), SheDing.CONFIGURATION, true);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public MICBM getMissileModel()
	{
		return new MMDianCi();
	}
}
