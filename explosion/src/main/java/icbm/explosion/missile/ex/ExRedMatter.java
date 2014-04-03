package icbm.explosion.missile.ex;

import icbm.Settings;
import icbm.explosion.explosive.blast.BlastRedmatter;
import icbm.explosion.missile.types.Missile;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import calclavia.lib.recipe.RecipeUtility;

public class ExRedMatter extends Missile
{
    public ExRedMatter()
    {
        super("redMatter", 4);
        this.modelName = "missile_redmatter.tcn";
    }

    @Override
    public void init()
    {
        RecipeUtility.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[] { "AAA", "AEA", "AAA", 'E', antimatter.getItemStack(), 'A', "strangeMatter" }), this.getUnlocalizedName(), Settings.CONFIGURATION, true);
    }

    @Override
    public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
    {
        new BlastRedmatter(world, entity, x, y, z, 35).explode();
    }

}
