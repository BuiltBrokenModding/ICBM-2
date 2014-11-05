package icbm.explosion.ex;

import icbm.Settings;
import icbm.explosion.explosive.blast.BlastAntiGravitational;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import resonant.lib.recipe.RecipeUtility;

public class ExAntiGravitational extends Explosion
{
    public ExAntiGravitational()
    {
        super("antiGravitational", 3);
        this.modelName = "missile_antigrav.tcn";
    }

    @Override
    public void init()
    {
        RecipeUtility.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[] { "EEE", "ETE", "EEE", 'T', replsive.getItemStack(), 'E', Items.ender_eye }), this.getUnlocalizedName(), Settings.CONFIGURATION, true);
    }

    @Override
    public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
    {
        new BlastAntiGravitational(world, entity, x, y, z, 30).explode();
    }
}
