package icbm.explosion.ex;

import icbm.Settings;
import icbm.explosion.explosive.blast.BlastFire;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import resonant.lib.recipe.RecipeUtility;
import resonant.lib.transform.vector.Vector3;

public class ExIncendiary extends Explosion
{
    public ExIncendiary(String mingZi, int tier)
    {
        super(mingZi, tier);
        this.modelName = "missile_incendiary.tcn";
    }

    @Override
    public void init()
    {
        RecipeUtility.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[] { "@@@", "@?@", "@!@", '@', "dustSulfur", '?', replsive.getItemStack(), '!', Items.lava_bucket }), this.getUnlocalizedName(), Settings.CONFIGURATION, true);
    }

    @Override
    public void onYinZha(World worldObj, Vector3 position, int fuseTicks)
    {
        super.onYinZha(worldObj, position, fuseTicks);
        worldObj.spawnParticle("lava", position.x(), position.y() + 0.5D, position.z(), 0.0D, 0.0D, 0.0D);
    }

    @Override
    public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
    {
        new BlastFire(world, entity, x, y, z, 14).explode();
    }
}
