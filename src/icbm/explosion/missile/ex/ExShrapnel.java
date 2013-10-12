package icbm.explosion.missile.ex;

import icbm.core.ICBMConfiguration;
import icbm.core.base.ModelICBM;
import icbm.explosion.explosive.explosion.BzQunDan;
import icbm.explosion.missile.Explosive;
import icbm.explosion.model.missiles.MMQunDan;
import icbm.explosion.model.missiles.MMXiaoQunDan;
import icbm.explosion.model.missiles.MMZhen;
import icbm.explosion.model.missiles.Missile;
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
		if (this.getID() == Explosive.shrapnel.getID())
		{
			RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[] { "???", "?@?", "???", '@', replsive.getItemStack(), '?', Item.arrow }), this.getUnlocalizedName(), ICBMConfiguration.CONFIGURATION, true);
		}
		else if (this.getID() == Explosive.anvil.getID())
		{
			RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(10), new Object[] { "SSS", "SAS", "SSS", 'A', Block.anvil, 'S', Explosive.shrapnel.getItemStack() }), this.getUnlocalizedName(), ICBMConfiguration.CONFIGURATION, true);
		}
		else if (this.getID() == Explosive.fragmentation.getID())
		{
			RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[] { " @ ", "@?@", " @ ", '?', incendiary.getItemStack(), '@', shrapnel.getItemStack() }), this.getUnlocalizedName(), ICBMConfiguration.CONFIGURATION, true);
		}
	}

	@Override
	public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
	{
		if (this.getTier() == 2)
		{
			new BzQunDan(world, entity, x, y, z, 15, true, true, false).explode();
		}
		else if (this.getID() == Explosive.anvil.getID())
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
		if (this.getID() == Explosive.shrapnel.getID())
		{
			return new MMXiaoQunDan();
		}
		else if (this.getID() == Explosive.anvil.getID())
		{
			return new MMZhen();
		}
		else if (this.getID() == Explosive.fragmentation.getID())
		{
			return new MMQunDan();
		}

		return null;
	}
}
