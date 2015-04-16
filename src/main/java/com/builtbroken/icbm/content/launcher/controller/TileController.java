package com.builtbroken.icbm.content.launcher.controller;

import com.builtbroken.icbm.content.launcher.TileAbstractLauncher;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.prefab.tile.TileModuleMachine;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;

import java.util.ArrayList;
import java.util.List;

/** Used to link several launchers together to be controlled from a single terminal
 * Created by robert on 4/3/2015.
 */
public class TileController extends TileModuleMachine
{
    protected List<Pos> launcherLocations = new ArrayList<Pos>();

    public TileController()
    {
        super("missileController", Material.iron);
    }

    /**
     * Called to fire the launcher in the list of linked launchers
     *
     * @param index - # in the launcher list
     */
    protected void fireLauncher(int index)
    {
        if (index < launcherLocations.size())
        {
            Pos pos = launcherLocations.get(index);
            TileEntity tile = pos.getTileEntity(world());
            if (tile instanceof TileAbstractLauncher)
            {
                ((TileAbstractLauncher) tile).fireMissile();
            }
        }
    }

    /**
     * Loops threw the list of launchers and fires each one
     */
    protected void fireAllLaunchers()
    {
        for (int i = 0; i < launcherLocations.size(); i++)
        {
            fireLauncher(i);
        }
    }

    /**
     * Called to link a launcher using the launcher location
     * and its pass code
     *
     * @param pos  - location, normally stored in a tool/item
     * @param code - pass code, to prevent linking machines without
     *             using a link tool that has clicked the launcher
     */
    protected void linkLauncher(Pos pos, short code)
    {

    }

    /**
     * Called to add a launcher to the list of controlled launchers
     *
     * @param pos - location of the launcher
     */
    protected void addLauncher(Pos pos)
    {
        if (launcherLocations.size() < 10)
        {
            TileEntity tile = pos.getTileEntity(world());
            if (tile instanceof TileAbstractLauncher)
            {
                launcherLocations.add(pos);
            }
        }
    }

    /**
     * Called to remove the launcher from the list
     *
     * @param pos - location of the launcher
     */
    protected void removeLauncher(Pos pos)
    {
        launcherLocations.remove(pos);
    }

    /**
     * Grabs a list of all linked launchers
     *
     * @return ArrayList<TileAbstractLauncher>
     */
    protected List<TileAbstractLauncher> getLaunchers()
    {
        List<TileAbstractLauncher> list = new ArrayList();
        for (Pos pos : launcherLocations)
        {
            TileEntity tile = pos.getTileEntity(world());
            if (tile instanceof TileAbstractLauncher)
            {
                list.add((TileAbstractLauncher) tile);
            }
        }
        return list;
    }
}
