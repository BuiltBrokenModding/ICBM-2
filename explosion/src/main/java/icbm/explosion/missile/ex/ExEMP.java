package icbm.explosion.missile.ex;

import icbm.core.Settings;
import icbm.core.prefab.render.ModelICBM;
import icbm.explosion.explosive.blast.BlastEMP;
import icbm.explosion.missile.types.Missile;
import icbm.explosion.model.missiles.ModelEMPMissile;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import calclavia.lib.recipe.RecipeUtility;
import calclavia.lib.recipe.UniversalRecipe;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ExEMP extends Missile
{
    public ExEMP(String mingZi, int tier)
    {
        super(mingZi, tier);
    }

    @Override
    public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
    {
        new BlastEMP(world, entity, x, y, z, 50).setEffectBlocks().setEffectEntities().explode();
    }

    @Override
    public void init()
    {
        RecipeUtility.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[] { "RBR", "BTB", "RBR", 'T', replsive.getItemStack(), 'R', Block.blockRedstone, 'B', UniversalRecipe.BATTERY.get() }), this.getUnlocalizedName(), Settings.CONFIGURATION, true);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ModelICBM getMissileModel()
    {
        return new ModelEMPMissile();
    }
}
