package com.builtbroken.icbm.client;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.lib.mod.ModCreativeTab;
import net.minecraft.block.Block;
import net.minecraft.item.Item;

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
        add(list, ICBM.blockAntenna);
        add(list, ICBM.blockCommandCentral);
        if (Engine.runningAsDev)
        {
            add(list, ICBM.blockStandardSilo);
        }
        add(list, ICBM.blockSiloController);
        add(list, ICBM.blockSmallPortableLauncher);
        add(list, ICBM.blockMissileWorkstation);
        add(list, ICBM.itemLinkTool);
        add(list, ICBM.itemGPSTool);
        add(list, ICBM.itemRocketLauncher);
        add(list, ICBM.blockLauncherParts);
        add(list, ICBM.itemMissile);
        add(list, ICBM.blockWarhead);
        add(list, ICBM.itemExplosive);
        add(list, ICBM.itemEngineModules);
        add(list, ICBM.itemGuidanceModules);
        add(list, ICBM.itemMissileParts);
        add(list, ICBM.itemExplosivePart);
    }

    @Override
    public boolean hasSearchBar()
    {
        return true;
    }

    private void add(List list, Item item)
    {
        item.getSubItems(item, this, list);
    }

    private void add(List list, Block block)
    {
        block.getSubBlocks(Item.getItemFromBlock(block), this, list);
    }
}
