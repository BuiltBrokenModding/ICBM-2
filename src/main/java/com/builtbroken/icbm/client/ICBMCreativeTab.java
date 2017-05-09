package com.builtbroken.icbm.client;

import com.builtbroken.icbm.ICBM;
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
        add(list, ICBM.blockLauncherFrame);
        add(list, InventoryUtility.getBlock("icbm:icbmLauncherParts"));
        add(list, InventoryUtility.getBlock("icbm:smallsilo"));

        add(list, ICBM.itemLinkTool);
        add(list, InventoryUtility.getItem("icbm:gpsFlag"));

        add(list, ICBM.blockAntenna);
        add(list, ICBM.blockCommandCentral);
        add(list, ICBM.blockCommandSiloConnector);
        add(list, ICBM.blockCommandSiloDisplay);
        add(list, ICBM.itemRemoteDetonator);
        add(list, ICBM.itemRadarGun);
        add(list, ICBM.itemLaserDet);

        add(list, InventoryUtility.getBlock("icbm:standardsilo"));

        add(list, InventoryUtility.getBlock("icbm:silocontroller"));

        add(list, ICBM.blockMissileWorkstation);
        add(list, InventoryUtility.getBlock("icbm:icbmWarheadWorkstation"));
        add(list, InventoryUtility.getBlock("icbm:icbmSMAuto"));

        add(list, ICBM.itemMissileCart);
        add(list, InventoryUtility.getBlock("icbm:smallMissileMag"));

        add(list, ICBM.itemRocketLauncher);

        add(list, ICBM.itemEngineModules);
        add(list, ICBM.itemGuidanceModules);
        add(list, ICBM.itemMissileParts);
        add(list, ICBM.itemTrigger);
        add(list, InventoryUtility.getBlock("icbm:icbmDecorLaunchPad"));
        add(list, InventoryUtility.getBlock("icbm:TileMissile"));
    }
}
