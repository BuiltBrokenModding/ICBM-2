package icbm.explosion.missile.ex;

import icbm.Settings;
import icbm.explosion.explosive.blast.BlastAntiGravitational;
import icbm.explosion.missile.types.Missile;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import calclavia.lib.recipe.RecipeUtility;

public class ExAntiGravitational extends Missile
{
    public ExAntiGravitational()
    {
        super("antiGravitational", 3);
        this.modelName = "missile_antigrav.tcn";
    }

    @Override
    public void init()
    {
        RecipeUtility.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[] { "EEE", "ETE", "EEE", 'T', replsive.getItemStack(), 'E', Item.eyeOfEnder }), this.getUnlocalizedName(), Settings.CONFIGURATION, true);
    }

    @Override
    public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
    {
        new BlastAntiGravitational(world, entity, x, y, z, 30).explode();
    }
}
