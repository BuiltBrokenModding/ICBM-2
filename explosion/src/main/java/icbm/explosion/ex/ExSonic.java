package icbm.explosion.ex;

import icbm.Settings;
import icbm.explosion.explosive.Explosive;
import icbm.explosion.explosive.blast.BlastSonic;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import calclavia.lib.recipe.RecipeUtility;
import calclavia.lib.recipe.UniversalRecipe;

public class ExSonic extends Ex
{
	public ExSonic(String mingZi, int tier)
	{
		super(mingZi, tier);
		if (this.getTier() == 3)
        {
            this.modelName = "missile_sonic.tcn";
        }
        else
        {
            this.modelName = "missile_ion.tcn";
        }
	}

	@Override
	public void init()
	{
		if (this.getTier() == 3)
		{
			RecipeUtility.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[] { " S ", "S S", " S ", 'S', Explosive.sonic.getItemStack() }), this.getUnlocalizedName(), Settings.CONFIGURATION, true);
		}
		else
		{
			RecipeUtility.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[] { "@?@", "?R?", "@?@", 'R', Explosive.replsive.getItemStack(), '?', Block.music, '@', UniversalRecipe.SECONDARY_METAL.get() }), this.getUnlocalizedName(), Settings.CONFIGURATION, true);
		}
	}

	@Override
	public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
	{
		if (this.getTier() == 3)
		{
			new BlastSonic(world, entity, x, y, z, 20, 35).setShockWave().explode();
		}
		else
		{
			new BlastSonic(world, entity, x, y, z, 15, 30).explode();
		}
	}

}
