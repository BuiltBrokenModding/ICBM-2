package icbm.explosion.missile.ex;

import icbm.core.ICBMConfiguration;
import icbm.core.base.ModelICBM;
import icbm.explosion.explosive.blast.BlastRedmatter;
import icbm.explosion.missile.missile.Missile;
import icbm.explosion.model.missiles.MMHongSu;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import universalelectricity.prefab.RecipeHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ExRedMatter extends Missile
{
    public ExRedMatter(String mingZi, int tier)
    {
        super(mingZi, tier);
    }

    @Override
    public void init()
    {
        RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[] { "AAA", "AEA", "AAA", 'E', antimatter.getItemStack(), 'A', "strangeMatter" }), this.getUnlocalizedName(), ICBMConfiguration.CONFIGURATION, true);
    }

    @Override
    public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
    {
        new BlastRedmatter(world, entity, x, y, z, 35).explode();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ModelICBM getMissileModel()
    {
        return new MMHongSu();
    }

}
