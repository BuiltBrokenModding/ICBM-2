package icbm.explosion.missile.ex;

import icbm.core.ICBMConfiguration;
import icbm.core.ICBMCore;
import icbm.core.base.ModelICBM;
import icbm.explosion.explosive.blast.BlastAntimatter;
import icbm.explosion.missile.Explosive;
import icbm.explosion.model.missiles.MMFanWuSu;
import icbm.explosion.model.missiles.Missile;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.RecipeHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ExAntimatter extends Missile
{
    public ExAntimatter(String mingZi, int tier)
    {
        super(mingZi, tier);
        this.setYinXin(300);
    }

    /** Called when the explosive is on fuse and going to explode. Called only when the explosive is
     * in it's TNT form.
     * 
     * @param fuseTicks - The amount of ticks this explosive is on fuse */
    @Override
    public void onYinZha(World worldObj, Vector3 position, int fuseTicks)
    {
        super.onYinZha(worldObj, position, fuseTicks);

        if (fuseTicks % 25 == 0)
        {
            worldObj.playSoundEffect(position.x, position.y, position.z, ICBMCore.PREFIX + "alarm", 4F, 1F);
        }
    }

    @Override
    public void init()
    {
        RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[] { "AAA", "AEA", "AAA", 'E', Explosive.nuclear.getItemStack(), 'A', "antimatterGram" }), this.getUnlocalizedName(), ICBMConfiguration.CONFIGURATION, true);
    }

    @Override
    public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
    {
        new BlastAntimatter(world, entity, x, y, z, ICBMConfiguration.ANTIMATTER_SIZE, ICBMConfiguration.DESTROY_BEDROCK).explode();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ModelICBM getMissileModel()
    {
        return new MMFanWuSu();
    }
}
