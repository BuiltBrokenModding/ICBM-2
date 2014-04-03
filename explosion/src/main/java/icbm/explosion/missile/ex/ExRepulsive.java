package icbm.explosion.missile.ex;

import icbm.ModelICBM;
import icbm.Settings;
import icbm.explosion.explosive.Explosive;
import icbm.explosion.explosive.blast.BlastRepulsive;
import icbm.explosion.missile.types.Missile;
import icbm.explosion.model.missiles.MMLa;
import icbm.explosion.model.missiles.MMTui;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import calclavia.lib.recipe.RecipeUtility;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ExRepulsive extends Missile
{
    public ExRepulsive(String name, int tier)
    {
        super(name, tier);
        this.setYinXin(120);
        if (name.equalsIgnoreCase("attractive"))
        {
            this.modelName = "missile_attractive.tcn";
        }
        else
        {
            this.modelName = "missile_repulsion .tcn";
        }
    }

    @Override
    public void init()
    {
        if (this.getID() == Explosive.attractive.getID())
        {
            RecipeUtility.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[] { "YY", 'Y', Explosive.condensed.getItemStack() }), this.getUnlocalizedName(), Settings.CONFIGURATION, true);
        }
        else
        {
            RecipeUtility.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[] { "Y", "Y", 'Y', Explosive.condensed.getItemStack() }), this.getUnlocalizedName(), Settings.CONFIGURATION, true);
        }
    }

    @Override
    public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
    {
        if (this.getID() == Explosive.attractive.getID())
        {
            new BlastRepulsive(world, entity, x, y, z, 2f).setDestroyItems().setPushType(1).explode();
        }
        else
        {
            new BlastRepulsive(world, entity, x, y, z, 2f).setDestroyItems().setPushType(2).explode();

        }
    }
}
