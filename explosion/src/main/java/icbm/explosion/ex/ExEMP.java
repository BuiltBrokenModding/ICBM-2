package icbm.explosion.ex;

import icbm.Settings;
import icbm.explosion.explosive.blast.BlastEMP;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import resonant.lib.recipe.RecipeUtility;
import resonant.lib.recipe.UniversalRecipe;

public class ExEMP extends Ex
{
    public ExEMP()
    {
        super("emp", 3);
        this.modelName = "missile_emp.tcn";
    }

    @Override
    public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
    {
        new BlastEMP(world, entity, x, y, z, 50).setEffectBlocks().setEffectEntities().explode();
    }

    @Override
    public void init()
    {
        RecipeUtility.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[] { "RBR", "BTB", "RBR", 'T', replsive.getItemStack(), 'R', Block.blockRedstone, 'B', UniversalRecipe.BATTERY.get() }), this.getUnlocalizedName(), Settings.CONFIGURATION, true);
    }
}
