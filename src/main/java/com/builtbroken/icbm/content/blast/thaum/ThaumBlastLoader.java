package com.builtbroken.icbm.content.blast.thaum;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.mc.lib.mod.loadable.AbstractLoadable;
import com.builtbroken.mc.lib.world.explosive.ExplosiveRegistry;
import com.builtbroken.mc.prefab.explosive.ExplosiveHandler;

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
        try
        {
            ExplosiveRegistry.registerOrGetExplosive(ICBM.DOMAIN, "ThaumTaint", new ExplosiveHandler("ThaumTaint", BlastTaint.class, 1));
            ExplosiveRegistry.registerOrGetExplosive(ICBM.DOMAIN, "ThaumNode", new ExplosiveHandler("ThaumNode", BlastNode.class, 1));
            ExplosiveRegistry.registerOrGetExplosive(ICBM.DOMAIN, "ThaumJar", new ExplosiveHandler("ThaumJar", BlastNodeJar.class, 1));
        }
        catch (Exception e)
        {
            ICBM.INSTANCE.logger().error("Failed to load thaumcraft support", e);
        }
    }
}
