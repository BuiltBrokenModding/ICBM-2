package icbm.explosion.missile.ex;

import icbm.core.ICBMConfiguration;
import icbm.core.prefab.render.ModelICBM;
import icbm.explosion.explosive.blast.BlastBreech;
import icbm.explosion.missile.Explosive;
import icbm.explosion.missile.missile.Missile;
import icbm.explosion.model.missiles.MMTuPuo;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ExBreaching extends Missile
{
    public ExBreaching(String mingZi, int tier)
    {
        super(mingZi, tier);
        this.setYinXin(40);
    }

    @Override
    public void init()
    {
        RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(2), new Object[] { "GCG", "GCG", "GCG", 'C', Explosive.condensed.getItemStack(), 'G', Item.gunpowder }), this.getUnlocalizedName(), ICBMConfiguration.CONFIGURATION, true);
    }

    @Override
    public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
    {
        new BlastBreech(world, entity, x, y, z, 2.5f, 7).explode();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ModelICBM getMissileModel()
    {
        return new MMTuPuo();
    }
}
