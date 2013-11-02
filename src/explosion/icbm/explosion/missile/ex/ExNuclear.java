package icbm.explosion.missile.ex;

import icbm.core.ICBMConfiguration;
import icbm.core.ICBMCore;
import icbm.core.base.ModelICBM;
import icbm.explosion.explosive.blast.BlastNuclear;
import icbm.explosion.missile.Explosive;
import icbm.explosion.missile.missile.Missile;
import icbm.explosion.model.missiles.MMWenZha;
import icbm.explosion.model.missiles.MMYuanZi;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.RecipeHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ExNuclear extends Missile
{
	public ExNuclear(String mingZi, int tier)
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
				RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[] { "UUU", "UEU", "UUU", 'E', thermobaric.getItemStack(), 'U', "ingotUranium" }), this.getUnlocalizedName(), ICBMConfiguration.CONFIGURATION, true);
			}
			else
			{
				RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[] { "EEE", "EEE", "EEE", 'E', thermobaric.getItemStack() }), this.getUnlocalizedName(), ICBMConfiguration.CONFIGURATION, true);

			}
		}
		else
		{
			RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[] { "CIC", "IRI", "CIC", 'R', Explosive.replsive.getItemStack(), 'C', Explosive.chemical.getItemStack(), 'I', Explosive.incendiary.getItemStack() }), this.getUnlocalizedName(), ICBMConfiguration.CONFIGURATION, true);

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
				worldObj.playSoundEffect((int) position.x, (int) position.y, (int) position.z, ICBMCore.PREFIX + "alarm", 4F, 1F);
			}
		}
	}

	@Override
	public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
	{
		if (this.getTier() == 3)
		{
			new BlastNuclear(world, entity, x, y, z, 50, 80).setNuclear().explode();
		}
		else
		{
			new BlastNuclear(world, entity, x, y, z, 30, 45).explode();
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public ModelICBM getMissileModel()
	{
		if (this.getTier() == 3)
		{
			return new MMYuanZi();
		}

		return new MMWenZha();
	}
}
