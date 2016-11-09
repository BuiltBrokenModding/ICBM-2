package com.builtbroken.icbm.client;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.lib.mod.ModCreativeTab;

import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/28/2016.
 */
public class ICBMCreativeTab extends ModCreativeTab
{
    public ICBMCreativeTab()
    {
        super("ICBM");
    }

    @Override
    public void displayAllReleventItems(List list)
    {
        //Put most used items at top
        add(list, ICBM.blockAMS);
        add(list, ICBM.blockFoFStation);
        add(list, ICBM.blockLauncherFrame);
        add(list, ICBM.blockSmallSilo);

        add(list, ICBM.itemLinkTool);
        add(list, ICBM.itemGPSTool);

        add(list, ICBM.blockAntenna);
        add(list, ICBM.blockCommandCentral);
        add(list, ICBM.blockCommandSiloConnector);
        add(list, ICBM.blockCommandSiloDisplay);
        add(list, ICBM.itemRemoteDetonator);
        add(list, ICBM.itemRadarGun);
        add(list, ICBM.itemLaserDet);

        if (Engine.runningAsDev)
        {
            add(list, ICBM.blockStandardSilo);
        }

        add(list, ICBM.blockSiloController);
        add(list, ICBM.blockSmallPortableLauncher);

        add(list, ICBM.blockMissileWorkstation);
        add(list, ICBM.blockWarheadWorkstation);

        add(list, ICBM.itemMissileCart);
        add(list, ICBM.blockSmallMissileMag);

        add(list, ICBM.itemRocketLauncher);
        add(list, ICBM.blockLauncherParts);
        //add(list, ICBM.itemMissile);
        //add(list, ICBM.blockWarhead);
        //add(list, ICBM.itemExplosive);
        add(list, ICBM.itemEngineModules);
        add(list, ICBM.itemGuidanceModules);
        add(list, ICBM.itemMissileParts);
        add(list, ICBM.itemTrigger);
        add(list, ICBM.blockLaunchPad);
    }
}
