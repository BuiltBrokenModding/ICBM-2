package icbm.explosion.missile.ex;

import calclavia.lib.prefab.RecipeHelper;
import icbm.core.ICBMConfiguration;
import icbm.core.base.ModelICBM;
import icbm.explosion.explosive.blast.BlastRepulsive;
import icbm.explosion.missile.Explosive;
import icbm.explosion.missile.missile.Missile;
import icbm.explosion.model.missiles.MMLa;
import icbm.explosion.model.missiles.MMTui;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ExRepulsive extends Missile
{
    public ExRepulsive(String mingZi, int tier)
    {
        super(mingZi, tier);
        this.setYinXin(120);
    }

    @Override
    public void init()
    {
        if (this.getID() == Explosive.attractive.getID())
        {
            RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[] { "YY", 'Y', Explosive.condensed.getItemStack() }), this.getUnlocalizedName(), ICBMConfiguration.CONFIGURATION, true);
        }
        else
        {
            RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[] { "Y", "Y", 'Y', Explosive.condensed.getItemStack() }), this.getUnlocalizedName(), ICBMConfiguration.CONFIGURATION, true);
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

    @SideOnly(Side.CLIENT)
    @Override
    public ModelICBM getMissileModel()
    {
        if (this.getID() == Explosive.attractive.getID())
        {
            return new MMLa();
        }
        else
        {
            return new MMTui();
        }
    }
}
