package icbm.explosion.missile.ex;

import icbm.core.ICBMConfiguration;
import icbm.core.base.ModelICBM;
import icbm.explosion.explosive.blast.BzYaSuo;
import icbm.explosion.model.missiles.MMYaSuo;
import icbm.explosion.model.missiles.Missile;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import universalelectricity.prefab.RecipeHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ExCondensed extends Missile
{
    public ExCondensed(String mingZi, int tier)
    {
        super(mingZi, tier);
        this.setYinXin(1);
    }

    @Override
    public void init()
    {
        RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(3), new Object[] { "@?@", '@', Block.tnt, '?', Item.redstone }), this.getUnlocalizedName(), ICBMConfiguration.CONFIGURATION, true);
    }

    @Override
    public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
    {
        new BzYaSuo(world, entity, x, y, z, 2.5f).explode();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModelICBM getMissileModel()
    {
        return new MMYaSuo();
    }
}
