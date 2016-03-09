package com.builtbroken.icbm.server;

import com.builtbroken.icbm.CommonProxy;
import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.content.ams.TileAMS;
import com.builtbroken.icbm.content.crafting.station.small.TileSmallMissileWorkstation;
import com.builtbroken.icbm.content.launcher.fof.TileFoF;
import com.builtbroken.icbm.content.launcher.launcher.standard.TileStandardLauncher;

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
        ICBM.blockMissileWorkstation = ICBM.INSTANCE.getManager().newBlock("SmallMissileWorkStation", TileSmallMissileWorkstation.class);
        ICBM.blockStandardLauncher = ICBM.INSTANCE.getManager().newBlock("StandardMissileLauncher", TileStandardLauncher.class);
        ICBM.blockAMS = ICBM.INSTANCE.getManager().newBlock("ICBMxAMS", TileAMS.class);
        ICBM.blockFoFStation = ICBM.INSTANCE.getManager().newBlock("ICBMxFoF", TileFoF.class);
    }

    @Override
    public void registerExplosives()
    {
        ICBM.registerExplosives();
    }
}
