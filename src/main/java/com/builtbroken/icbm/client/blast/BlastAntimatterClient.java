package com.builtbroken.icbm.client.blast;

import com.builtbroken.icbm.content.blast.explosive.BlastAntimatter;
import com.builtbroken.mc.api.explosive.IExplosiveHandler;
import com.builtbroken.mc.core.Engine;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/15/2016.
 */
public class BlastAntimatterClient extends BlastAntimatter
{
    public BlastAntimatterClient(IExplosiveHandler handler)
    {
        super(handler);
    }

    @Override
    public void doStartDisplay()
    {
        if (this.size >= 2.0F)
        {
            Engine.proxy.spawnParticle("hugeexplosion", world, x, y, z, 1.0D, 0.0D, 0.0D);
        }
        else
        {
            Engine.proxy.spawnParticle("largeexplode", world, x, y, z, 1.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public void doEndDisplay()
    {
        //TODO get custom effects
        if (world.isRemote)
        {
            if (this.size >= 2.0F)
            {
                Engine.proxy.spawnParticle("hugeexplosion", world, x, y, z, 1.0D, 0.0D, 0.0D);
            }
            else
            {
                Engine.proxy.spawnParticle("largeexplode", world, x, y, z, 1.0D, 0.0D, 0.0D);
            }
        }
    }
}
