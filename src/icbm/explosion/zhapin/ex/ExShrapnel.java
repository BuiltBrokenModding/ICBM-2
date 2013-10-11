package icbm.explosion.zhapin.ex;

import icbm.core.ICBMConfiguration;
import icbm.core.base.ModelICBM;
import icbm.explosion.explosive.explosion.BzQunDan;
import icbm.explosion.model.missiles.MMQunDan;
import icbm.explosion.model.missiles.MMXiaoQunDan;
import icbm.explosion.model.missiles.MMZhen;
import icbm.explosion.zhapin.Explosive;
import icbm.explosion.zhapin.daodan.Missile;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import universalelectricity.prefab.RecipeHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ExShrapnel extends Missile
{
	public ExShrapnel(String mingZi, int tier)
	{
		super(mingZi, tier);
	}

	@Override
	public void init()
	{
		if (this.getID() == Explosive.xiaoQunDan.getID())
		{
			RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[] { "???", "?@?", "???", '@', tui.getItemStack(), '?', Item.arrow }), this.getUnlocalizedName(), ICBMConfiguration.CONFIGURATION, true);
		}
		else if (this.getID() == Explosive.zhen.getID())
		{
			RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(10), new Object[] { "SSS", "SAS", "SSS", 'A', Block.anvil, 'S', Explosive.xiaoQunDan.getItemStack() }), this.getUnlocalizedName(), ICBMConfiguration.CONFIGURATION, true);
		}
		else if (this.getID() == Explosive.qunDan.getID())
		{
			RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[] { " @ ", "@?@", " @ ", '?', huo.getItemStack(), '@', xiaoQunDan.getItemStack() }), this.getUnlocalizedName(), ICBMConfiguration.CONFIGURATION, true);
		}
	}

	@Override
	public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
	{
		if (this.getTier() == 2)
		{
			new BzQunDan(world, entity, x, y, z, 15, true, true, false).explode();
		}
		else if (this.getID() == Explosive.zhen.getID())
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
	public ModelICBM getMissileModel()
	{
		if (this.getID() == Explosive.xiaoQunDan.getID())
		{
			return new MMXiaoQunDan();
		}
		else if (this.getID() == Explosive.zhen.getID())
		{
			return new MMZhen();
		}
		else if (this.getID() == Explosive.qunDan.getID())
		{
			return new MMQunDan();
		}

		return null;
	}
}
