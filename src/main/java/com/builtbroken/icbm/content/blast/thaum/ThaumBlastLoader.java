package com.builtbroken.icbm.content.blast.thaum;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.content.crafting.missile.MissileModuleBuilder;
import com.builtbroken.icbm.content.crafting.missile.warhead.WarheadCasings;
import com.builtbroken.icbm.content.warhead.TileWarhead;
import com.builtbroken.icbm.content.warhead.WarheadRecipe;
import com.builtbroken.mc.lib.mod.loadable.AbstractLoadable;
import com.builtbroken.mc.lib.world.explosive.ExplosiveRegistry;
import com.builtbroken.mc.prefab.explosive.ExplosiveHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.ItemStack;
import thaumcraft.common.config.ConfigItems;

/**
 * Loads thaumcraft support
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/15/2015.
 */
public class ThaumBlastLoader extends AbstractLoadable
{
    @Override
    public void init()
    {
        super.init();
        try
        {
            ExplosiveRegistry.registerOrGetExplosive(ICBM.DOMAIN, "ThaumTaint", new ExplosiveHandler("ThaumTaint", BlastTaint.class, 2));
            ExplosiveRegistry.registerOrGetExplosive(ICBM.DOMAIN, "ThaumTaintBottle", new ExplosiveHandler("ThaumTaintBottle", BlastTaintBottle.class, 5));
            ExplosiveRegistry.registerOrGetExplosive(ICBM.DOMAIN, "ThaumNode", new ExplosiveHandlerNode());
            ExplosiveRegistry.registerOrGetExplosive(ICBM.DOMAIN, "ThaumJar", new ExplosiveHandler("ThaumJar", BlastNodeJar.class, 2));
        }
        catch (Exception e)
        {
            ICBM.INSTANCE.logger().error("Failed to load thaumcraft support", e);
        }
    }

    @Override
    public void postInit()
    {
        super.postInit();
        ItemStack small_warhead_empty = MissileModuleBuilder.INSTANCE.buildWarhead(WarheadCasings.EXPLOSIVE_SMALL, null).toStack();

        TileWarhead.addMicroWarheadRecipe("ThaumTaint", new ItemStack(ConfigItems.itemResource, 1, 11), new ItemStack(ConfigItems.itemResource, 1, 11));
        GameRegistry.addRecipe(new WarheadRecipe(WarheadCasings.EXPLOSIVE_SMALL, "ThaumTaint", new ItemStack(ConfigItems.itemResource, 1, 11), new ItemStack(ConfigItems.itemResource, 1, 11), new ItemStack(ConfigItems.itemResource, 1, 11), new ItemStack(ConfigItems.itemResource, 1, 11), small_warhead_empty));

        TileWarhead.addMicroWarheadRecipe("ThaumTaintBottle", ConfigItems.itemBottleTaint, ConfigItems.itemBottleTaint);
        GameRegistry.addRecipe(new WarheadRecipe(WarheadCasings.EXPLOSIVE_SMALL, "ThaumTaintBottle",ConfigItems.itemBottleTaint, ConfigItems.itemBottleTaint, ConfigItems.itemBottleTaint, ConfigItems.itemBottleTaint, small_warhead_empty));

    }
}
