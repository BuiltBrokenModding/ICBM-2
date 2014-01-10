package icbm.explosion.missile.ex;

import icbm.core.ICBMConfiguration;
import icbm.core.prefab.render.ModelICBM;
import icbm.explosion.explosive.blast.BlastRegen;
import icbm.explosion.missile.missile.Missile;
import icbm.explosion.model.missiles.MMHuanYuan;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ExRejuvenation extends Missile
{
    public ExRejuvenation(String mingZi, int tier)
    {
        super(mingZi, tier);
    }

    @Override
    public void init()
    {
        RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[] { "ICI", "CDC", "ICI", 'D', Block.blockDiamond, 'C', Item.pocketSundial, 'I', Block.blockIron }), this.getUnlocalizedName(), ICBMConfiguration.CONFIGURATION, true);
    }

    @Override
    public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
    {
        new BlastRegen(world, entity, x, y, z, 16).doExplode();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ModelICBM getMissileModel()
    {
        return new MMHuanYuan();
    }
}
