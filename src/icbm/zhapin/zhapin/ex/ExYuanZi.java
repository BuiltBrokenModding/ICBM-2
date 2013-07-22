package icbm.zhapin.zhapin.ex;

import icbm.core.MICBM;
import icbm.core.SheDing;
import icbm.core.ZhuYaoICBM;
import icbm.zhapin.baozha.bz.BzYuanZi;
import icbm.zhapin.muoxing.daodan.MMWenZha;
import icbm.zhapin.muoxing.daodan.MMYuanZi;
import icbm.zhapin.zhapin.ZhaPin;
import icbm.zhapin.zhapin.daodan.DaoDan;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.RecipeHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ExYuanZi extends DaoDan
{
	public ExYuanZi(String mingZi, int tier)
	{
		super(mingZi, tier);
	}

	@Override
	public void init()
	{
		if (this.getTier() == 3)
		{
			if (OreDictionary.getOres("ingotUranium").size() > 0)
			{
				RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[] { "UUU", "UEU", "UUU", 'E', wenYa.getItemStack(), 'U', "ingotUranium" }), this.getUnlocalizedName(), SheDing.CONFIGURATION, true);
			}
			else
			{
				RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[] { "EEE", "EEE", "EEE", 'E', wenYa.getItemStack() }), this.getUnlocalizedName(), SheDing.CONFIGURATION, true);

			}
		}
		else
		{
			RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[] { "CIC", "IRI", "CIC", 'R', ZhaPin.tui.getItemStack(), 'C', ZhaPin.duQi.getItemStack(), 'I', ZhaPin.huo.getItemStack() }), this.getUnlocalizedName(), SheDing.CONFIGURATION, true);

		}
	}

	/**
	 * Called when the explosive is on fuse and going to explode. Called only when the explosive is
	 * in it's TNT form.
	 * 
	 * @param fuseTicks - The amount of ticks this explosive is on fuse
	 */
	@Override
	public void onYinZha(World worldObj, Vector3 position, int fuseTicks)
	{
		super.onYinZha(worldObj, position, fuseTicks);

		if (this.getTier() == 3)
		{
			if (fuseTicks % 25 == 0)
			{
				worldObj.playSoundEffect((int) position.x, (int) position.y, (int) position.z, ZhuYaoICBM.PREFIX + "alarm", 4F, 1F);
			}
		}
	}

	@Override
	public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
	{
		if (this.getTier() == 3)
		{
			new BzYuanZi(world, entity, x, y, z, 50, 80).setNuclear().explode();
		}
		else
		{
			new BzYuanZi(world, entity, x, y, z, 30, 45).explode();
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public MICBM getMuoXing()
	{
		if (this.getTier() == 3)
		{
			return new MMYuanZi();
		}

		return new MMWenZha();
	}
}
