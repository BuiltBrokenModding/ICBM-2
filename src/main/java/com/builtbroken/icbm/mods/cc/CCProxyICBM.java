package com.builtbroken.icbm.mods.cc;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.api.ICBM_API;
import com.builtbroken.mc.lib.mod.Mods;
import com.builtbroken.mc.lib.mod.loadable.AbstractLoadable;

/**
 * Proxy used to laod content for computer craft
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/26/2016.
 */
public class CCProxyICBM extends AbstractLoadable
{
    @Override
    public void preInit()
    {
        //OC has priority until the two can be combined
        if (!Mods.OC.isLoaded())
        {
            ICBM_API.blockDirectSiloController = ICBM.INSTANCE.getManager().newBlock("icbmDirectSiloConnector", TileSiloControllerCC.class);
        }
    }
}
