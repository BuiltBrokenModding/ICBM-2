package icbm.explosion.missile.ex;

import icbm.ModelICBM;
import icbm.Settings;
import icbm.explosion.explosive.Explosive;
import icbm.explosion.explosive.blast.BlastShrapnel;
import icbm.explosion.missile.types.Missile;
import icbm.explosion.model.missiles.MMQunDan;
import icbm.explosion.model.missiles.MMXiaoQunDan;
import icbm.explosion.model.missiles.MMZhen;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import calclavia.lib.recipe.RecipeUtility;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ExShrapnel extends Missile
{
    public ExShrapnel(String mingZi, int tier)
    {
        super(mingZi, tier);
    }

    @Override
    public void init()
    {
        if (this.getID() == Explosive.shrapnel.getID())
        {
            RecipeUtility.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[] { "???", "?@?", "???", '@', replsive.getItemStack(), '?', Item.arrow }), this.getUnlocalizedName(), Settings.CONFIGURATION, true);
        }
        else if (this.getID() == Explosive.anvil.getID())
        {
            RecipeUtility.addRecipe(new ShapedOreRecipe(this.getItemStack(10), new Object[] { "SSS", "SAS", "SSS", 'A', Block.anvil, 'S', Explosive.shrapnel.getItemStack() }), this.getUnlocalizedName(), Settings.CONFIGURATION, true);
        }
        else if (this.getID() == Explosive.fragmentation.getID())
        {
            RecipeUtility.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[] { " @ ", "@?@", " @ ", '?', incendiary.getItemStack(), '@', shrapnel.getItemStack() }), this.getUnlocalizedName(), Settings.CONFIGURATION, true);
        }
    }

    @Override
    public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
    {
        if (this.getTier() == 2)
        {
            new BlastShrapnel(world, entity, x, y, z, 15, true, true, false).explode();
        }
        else if (this.getID() == Explosive.anvil.getID())
        {
            new BlastShrapnel(world, entity, x, y, z, 25, false, false, true).explode();
        }
        else
        {
            new BlastShrapnel(world, entity, x, y, z, 30, true, false, false).explode();
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModelICBM getMissileModel()
    {
        if (this.getID() == Explosive.shrapnel.getID())
        {
            return new MMXiaoQunDan();
        }
        else if (this.getID() == Explosive.anvil.getID())
        {
            return new MMZhen();
        }
        else if (this.getID() == Explosive.fragmentation.getID())
        {
            return new MMQunDan();
        }

        return null;
    }
}
