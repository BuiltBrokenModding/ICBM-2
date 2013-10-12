package icbm.explosion.missile.ex;

import icbm.core.ICBMConfiguration;
import icbm.core.base.ModelICBM;
import icbm.explosion.explosive.blast.BzTaiYang;
import icbm.explosion.missile.Explosive;
import icbm.explosion.model.missiles.MMTaiYang;
import icbm.explosion.model.missiles.Missile;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.oredict.ShapedOreRecipe;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.RecipeHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ExExothermic extends Missile
{
    public boolean createNetherrack = true;

    public ExExothermic(String mingZi, int tier)
    {
        super(mingZi, tier);
        this.createNetherrack = ICBMConfiguration.CONFIGURATION.get(Configuration.CATEGORY_GENERAL, "Exothermic Create Netherrack", createNetherrack).getBoolean(createNetherrack);
    }

    @Override
    public void onYinZha(World worldObj, Vector3 position, int fuseTicks)
    {
        super.onYinZha(worldObj, position, fuseTicks);
        worldObj.spawnParticle("lava", position.x, position.y + 0.5D, position.z, 0.0D, 0.0D, 0.0D);
    }

    @Override
    public void init()
    {
        RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[] { "!!!", "!@!", "!!!", '@', Block.glass, '!', Explosive.incendiary.getItemStack() }), this.getUnlocalizedName(), ICBMConfiguration.CONFIGURATION, true);
    }

    @Override
    public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
    {
        new BzTaiYang(world, entity, x, y, z, 50).explode();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ModelICBM getMissileModel()
    {
        return new MMTaiYang();
    }
}
