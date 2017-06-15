package com.builtbroken.icbm.client;

import com.builtbroken.icbm.api.ICBM_API;
import com.builtbroken.mc.lib.mod.ModCreativeTab;
import com.builtbroken.mc.prefab.inventory.InventoryUtility;

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
        add(list, ICBM_API.blockLauncherFrame);
        add(list, InventoryUtility.getBlock("icbm:icbmLauncherParts"));
        add(list, InventoryUtility.getBlock("icbm:smallsilo"));

        add(list, InventoryUtility.getItem("icbm:siloLinker"));
        add(list, InventoryUtility.getItem("icbm:gpsFlag"));

        add(list, ICBM_API.blockAntenna);
        add(list, ICBM_API.blockCommandCentral);
        add(list, ICBM_API.blockCommandSiloConnector);
        add(list, ICBM_API.blockCommandSiloDisplay);
        add(list, InventoryUtility.getItem("icbm:icbmRemoteDet"));
        add(list, InventoryUtility.getItem("icbm:laserDet"));
        add(list, InventoryUtility.getItem("icbm:radarGun"));

        add(list, InventoryUtility.getBlock("icbm:standardsilo"));

        add(list, InventoryUtility.getBlock("icbm:silocontroller"));

        add(list, ICBM_API.blockMissileWorkstation);
        add(list, InventoryUtility.getBlock("icbm:icbmWarheadWorkstation"));
        add(list, InventoryUtility.getBlock("icbm:icbmSMAuto"));

        add(list, ICBM_API.itemMissileCart);
        add(list, InventoryUtility.getBlock("icbm:smallMissileMag"));

        add(list, ICBM_API.itemRocketLauncher);

        add(list, ICBM_API.itemEngineModules);
        add(list, ICBM_API.itemGuidanceModules);
        add(list, ICBM_API.itemMissileParts);
        add(list, ICBM_API.itemTrigger);
        add(list, InventoryUtility.getBlock("icbm:icbmDecorLaunchPad"));
        add(list, InventoryUtility.getBlock("icbm:TileMissile"));
    }
}
