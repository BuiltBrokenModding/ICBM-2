package com.builtbroken.icbm.client;

import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.framework.mod.ModCreativeTab;

import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/28/2016.
 */
public class ICBMCreativeTab extends ModCreativeTab //TODO move to JSON
{
    public ICBMCreativeTab()
    {
        super("ICBM");
    }

    @Override
    public void displayAllReleventItems(List list)
    {
        //Put most used items at top
        add(list, "icbm:icbmLauncherFrame");
        add(list, "icbm:icbmLauncherParts");
        add(list, "icbm:smallsilo");

        add(list, "icbm:siloLinker");
        add(list, "icbm:gpsFlag");

        add(list, "icbm:icbmAntenna");
        add(list, "icbm:icbmCommandCentral");
        add(list, "icbm:icbmCommandSiloController");
        add(list, "icbm:icbmCommandSiloDisplay");
        add(list, "icbm:icbmRemoteDet");
        add(list, "icbm:laserDet");
        add(list, "icbm:radarGun");

        add(list, "icbm:standardsilo");
        add(list, "icbm:silodoor");

        add(list, "icbm:silocontroller");

        add(list, "icbm:SmallMissileWorkStation");
        add(list, "icbm:icbmWarheadWorkstation");
        add(list, "icbm:icbmSMAuto");

        add(list, "icbm:icbmMissileCart");
        add(list, "icbm:smallMissileMag");

        add(list, "icbm:rocketLauncher");

        add(list, "icbm:engineModules");
        add(list, "icbm:guidanceModules");
        add(list, "icbm:missileParts");
        add(list, "icbm:icbmTriggers");
        add(list, "icbm:icbmDecorLaunchPad");
        add(list, "icbm:TileMissile");

        if(Engine.runningAsDev)
        {
            add(list, "icbm:cowSpawnerItem");
        }
    }
}
