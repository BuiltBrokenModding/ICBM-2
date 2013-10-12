package icbm.explosion.missile.ex;

import icbm.core.ICBMConfiguration;
import icbm.core.base.ModelICBM;
import icbm.explosion.explosive.blast.BzShengBuo;
import icbm.explosion.missile.Explosive;
import icbm.explosion.model.missiles.MMChaoShengBuo;
import icbm.explosion.model.missiles.MMShengBuo;
import icbm.explosion.model.missiles.Missile;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import universalelectricity.prefab.RecipeHelper;
import calclavia.lib.UniversalRecipes;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ExSonic extends Missile
{
    public ExSonic(String mingZi, int tier)
    {
        super(mingZi, tier);
    }

    @Override
    public void init()
    {
        if (this.getTier() == 3)
        {
            RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[] { " S ", "S S", " S ", 'S', Explosive.sonic.getItemStack() }), this.getUnlocalizedName(), ICBMConfiguration.CONFIGURATION, true);
        }
        else
        {
            RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[] { "@?@", "?R?", "@?@", 'R', Explosive.replsive.getItemStack(), '?', Block.music, '@', UniversalRecipes.SECONDARY_METAL }), this.getUnlocalizedName(), ICBMConfiguration.CONFIGURATION, true);
        }
    }

    @Override
    public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
    {
        if (this.getTier() == 3)
        {
            new BzShengBuo(world, entity, x, y, z, 15, 30).setShockWave().explode();
        }
        else
        {
            new BzShengBuo(world, entity, x, y, z, 10, 25).explode();
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ModelICBM getMissileModel()
    {
        if (this.getTier() == 3)
        {
            return new MMChaoShengBuo();
        }
        else
        {
            return new MMShengBuo();
        }
    }

}
