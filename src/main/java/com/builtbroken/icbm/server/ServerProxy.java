package com.builtbroken.icbm.server;

import com.builtbroken.icbm.CommonProxy;
import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.api.ICBM_API;
import com.builtbroken.icbm.content.launcher.controller.remote.display.TileSiloInterface;

/**
 * Code that only loads on a dedicated server box
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/20/2015.
 */
public class ServerProxy extends CommonProxy
{
    @Override
    public void preInit()
    {
        super.preInit();
        //ICBM_API.blockMissileWorkstation = ICBM.INSTANCE.getManager().newBlock("SmallMissileWorkStation", TileSmallMissileWorkstation.class);
        ICBM_API.blockCommandSiloDisplay = ICBM.INSTANCE.getManager().newBlock("icbmCommandSiloDisplay", TileSiloInterface.class);
    }

    @Override
    public void registerExplosives()
    {
        ICBM.registerExplosives();
    }
}
