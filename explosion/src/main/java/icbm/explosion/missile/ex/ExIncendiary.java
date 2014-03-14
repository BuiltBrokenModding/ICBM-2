package icbm.explosion.missile.ex;

import icbm.core.Settings;
import icbm.core.prefab.render.ModelICBM;
import icbm.explosion.explosive.blast.BlastFire;
import icbm.explosion.missile.types.Missile;
import icbm.explosion.model.missiles.ModelIncendiaryMissile;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import universalelectricity.api.vector.Vector3;
import calclavia.lib.recipe.RecipeUtility;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ExIncendiary extends Missile
{
    public ExIncendiary(String mingZi, int tier)
    {
        super(mingZi, tier);
    }

    @Override
    public void init()
    {
        RecipeUtility.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[] { "@@@", "@?@", "@!@", '@', "dustSulfur", '?', replsive.getItemStack(), '!', Item.bucketLava }), this.getUnlocalizedName(), Settings.CONFIGURATION, true);
    }

    @Override
    public void onYinZha(World worldObj, Vector3 position, int fuseTicks)
    {
        super.onYinZha(worldObj, position, fuseTicks);
        worldObj.spawnParticle("lava", position.x, position.y + 0.5D, position.z, 0.0D, 0.0D, 0.0D);
    }

    @Override
    public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
    {
        new BlastFire(world, entity, x, y, z, 14).explode();
    }

    @SideOnly(Side.CLIENT)
    public ModelICBM getMuoXing()
    {
        return new ModelIncendiaryMissile();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModelICBM getMissileModel()
    {
        return new ModelIncendiaryMissile();
    }
}
