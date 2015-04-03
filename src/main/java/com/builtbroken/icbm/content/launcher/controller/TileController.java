package com.builtbroken.icbm.content.launcher.controller;

import com.builtbroken.icbm.content.launcher.TileAbstractLauncher;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.prefab.tile.TileModuleMachine;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by robert on 4/3/2015.
 */
public class TileController extends TileModuleMachine
{
    protected List<Pos> launcherLocations = new ArrayList<Pos>();

    public TileController()
    {
        super("missileController", Material.iron);
    }

    protected void fireLauncher(int index)
    {
        if(index < launcherLocations.size())
        {
            Pos pos = launcherLocations.get(index);
            TileEntity tile = pos.getTileEntity(world());
            if(tile instanceof TileAbstractLauncher)
            {
                ((TileAbstractLauncher) tile).fireMissile();
            }
        }
    }

    protected void fireAllLaunchers()
    {
        for(int i = 0; i < launcherLocations.size(); i++)
        {
            fireLauncher(i);
        }
    }

    protected void linkLauncher(Pos pos, short code)
    {

    }

    protected void addLauncher(Pos pos)
    {
        if(launcherLocations.size() < 10)
        {
            TileEntity tile = pos.getTileEntity(world());
            if(tile instanceof TileAbstractLauncher)
            {
                launcherLocations.add(pos);
            }
        }
    }

    protected void removeLauncher(Pos pos)
    {
        launcherLocations.remove(pos);
    }

    protected List<TileAbstractLauncher> getLaunchers()
    {
        List<TileAbstractLauncher> list = new ArrayList();
        for(Pos pos: launcherLocations)
        {
            TileEntity tile = pos.getTileEntity(world());
            if(tile instanceof TileAbstractLauncher)
            {
                list.add((TileAbstractLauncher)tile);
            }
        }
        return list;
    }
}
