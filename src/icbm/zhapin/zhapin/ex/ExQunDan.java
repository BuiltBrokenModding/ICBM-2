package icbm.zhapin.zhapin.ex;

import icbm.core.SheDing;
import icbm.core.base.MICBM;
import icbm.zhapin.baozha.bz.BzQunDan;
import icbm.zhapin.muoxing.daodan.MMQunDan;
import icbm.zhapin.muoxing.daodan.MMXiaoQunDan;
import icbm.zhapin.muoxing.daodan.MMZhen;
import icbm.zhapin.zhapin.ZhaPin;
import icbm.zhapin.zhapin.daodan.DaoDan;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import universalelectricity.prefab.RecipeHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ExQunDan extends DaoDan
{
	public ExQunDan(String mingZi, int tier)
	{
		super(mingZi, tier);
	}

	@Override
	public void init()
	{
		if (this.getID() == ZhaPin.xiaoQunDan.getID())
		{
			RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[] { "???", "?@?", "???", '@', tui.getItemStack(), '?', Item.arrow }), this.getUnlocalizedName(), SheDing.CONFIGURATION, true);
		}
		else if (this.getID() == ZhaPin.zhen.getID())
		{
			RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(10), new Object[] { "SSS", "SAS", "SSS", 'A', Block.anvil, 'S', ZhaPin.xiaoQunDan.getItemStack() }), this.getUnlocalizedName(), SheDing.CONFIGURATION, true);
		}
		else if (this.getID() == ZhaPin.qunDan.getID())
		{
			RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[] { " @ ", "@?@", " @ ", '?', huo.getItemStack(), '@', xiaoQunDan.getItemStack() }), this.getUnlocalizedName(), SheDing.CONFIGURATION, true);
		}
	}

	@Override
	public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
	{
		if (this.getTier() == 2)
		{
			new BzQunDan(world, entity, x, y, z, 15, true, true, false).explode();
		}
		else if (this.getID() == ZhaPin.zhen.getID())
		{
			new BzQunDan(world, entity, x, y, z, 25, false, false, true).explode();
		}
		else
		{
			new BzQunDan(world, entity, x, y, z, 30, true, false, false).explode();
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public MICBM getMissileModel()
	{
		if (this.getID() == ZhaPin.xiaoQunDan.getID())
		{
			return new MMXiaoQunDan();
		}
		else if (this.getID() == ZhaPin.zhen.getID())
		{
			return new MMZhen();
		}
		else if (this.getID() == ZhaPin.qunDan.getID())
		{
			return new MMQunDan();
		}

		return null;
	}
}
