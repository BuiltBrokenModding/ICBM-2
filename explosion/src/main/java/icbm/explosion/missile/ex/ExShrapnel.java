package icbm.explosion.missile.ex;

import icbm.Settings;
import icbm.explosion.explosive.Explosive;
import icbm.explosion.explosive.blast.BlastShrapnel;
import icbm.explosion.missile.types.Missile;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import calclavia.lib.recipe.RecipeUtility;

public class ExShrapnel extends Missile
{
    public ExShrapnel(String name, int tier)
    {
        super(name, tier);
        if (this.getID() == Explosive.shrapnel.getID())
        {
            this.modelName = "missile_shrapnel.tcn";
        }
        else if (this.getID() == Explosive.anvil.getID())
        {
            this.modelName = "missile_anvil.tcn";
        }
        else if (this.getID() == Explosive.fragmentation.getID())
        {
            this.modelName = "missile_fragment.tcn";
        }
    }

    @Override
    public void init()
    {
        if (this.getID() == Explosive.shrapnel.getID())
        {
            RecipeUtility.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[] { "???", "?@?", "???", '@', replsive.getItemStack(), '?', Item.arrow }), this.getUnlocalizedName(), Settings.CONFIGURATION, true);
        }
        else if (this.getID() == Explosive.anvil.getID())
        {
            RecipeUtility.addRecipe(new ShapedOreRecipe(this.getItemStack(10), new Object[] { "SSS", "SAS", "SSS", 'A', Block.anvil, 'S', Explosive.shrapnel.getItemStack() }), this.getUnlocalizedName(), Settings.CONFIGURATION, true);
        }
        else if (this.getID() == Explosive.fragmentation.getID())
        {
            RecipeUtility.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[] { " @ ", "@?@", " @ ", '?', incendiary.getItemStack(), '@', shrapnel.getItemStack() }), this.getUnlocalizedName(), Settings.CONFIGURATION, true);
        }
    }

    @Override
    public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
    {
        if (this.getTier() == 2)
        {
            new BlastShrapnel(world, entity, x, y, z, 15, true, true, false).explode();
        }
        else if (this.getID() == Explosive.anvil.getID())
        {
            new BlastShrapnel(world, entity, x, y, z, 25, false, false, true).explode();
        }
        else
        {
            new BlastShrapnel(world, entity, x, y, z, 30, true, false, false).explode();
        }
    }
}
