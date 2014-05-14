package icbm.explosion.ex;

import icbm.Settings;
import icbm.explosion.explosive.blast.BlastRepulsive;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import resonant.lib.recipe.RecipeUtility;

public class ExCondensed extends Ex
{
    public ExCondensed(String mingZi, int tier)
    {
        super(mingZi, tier);
        this.setYinXin(1);
        this.modelName = "missile_concussion.tcn";
    }

    @Override
    public void init()
    {
        RecipeUtility.addRecipe(new ShapedOreRecipe(this.getItemStack(3), new Object[] { "@?@", '@', Block.tnt, '?', Item.redstone }), this.getUnlocalizedName(), Settings.CONFIGURATION, true);
    }

    @Override
    public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
    {
        new BlastRepulsive(world, entity, x, y, z, 2.5f).explode();
    }
}
