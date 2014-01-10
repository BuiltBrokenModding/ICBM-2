package icbm.explosion.missile.ex;

import icbm.core.ICBMConfiguration;
import icbm.core.prefab.render.ModelICBM;
import icbm.explosion.explosive.blast.BlastSky;
import icbm.explosion.missile.missile.Missile;
import icbm.explosion.model.missiles.MMBingDan;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ExEndothermic extends Missile
{
    public ExEndothermic(String mingZi, int tier)
    {
        super(mingZi, tier);
    }

    @Override
    public void init()
    {
        RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[] { "?!?", "!@!", "?!?", '@', attractive.getItemStack(), '?', Block.ice, '!', Block.blockSnow }), this.getUnlocalizedName(), ICBMConfiguration.CONFIGURATION, true);
    }

    @Override
    public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
    {
        new BlastSky(world, entity, x, y, z, 50).explode();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ModelICBM getMissileModel()
    {
        return new MMBingDan();
    }
}
