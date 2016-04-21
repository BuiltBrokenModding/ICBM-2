package com.builtbroken.icbm.content.launcher.controller.remote.antenna.wireless;

import com.builtbroken.jlib.type.Pair;
import com.builtbroken.mc.lib.transform.region.Cube;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Manager for global network grids created and shared over a world
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/20/2016.
 */
public class WirelessGridManager
{
    /** Map of grid name & pass key pair, to grid objects */
    public static final HashMap<Integer, HashMap<Pair<String, Short>, WirelessGrid>> worldToGrids = new HashMap();

    /**
     * Gets all grids inside the cube range, mainly used
     * for sniffing out networks.
     *
     * @param cube - cube to check, shouldn't be null
     * @return list of grids, empty if none, never null
     */
    public static List<WirelessGrid> getGridsInArea(World world, Cube cube)
    {
        List<WirelessGrid> list = new ArrayList();
        if (cube != null && worldToGrids.containsKey(world.provider.dimensionId))
        {
            for (WirelessGrid grid : worldToGrids.get(world.provider.dimensionId).values())
            {
                if (grid.overlaps(cube) && !list.contains(grid))
                {
                    list.add(grid);
                }
            }
        }
        return list;
    }

    /**
     * Gets the grid for the name and key
     *
     * @param tile - tile who wants the grid
     * @param name - name of the grid
     * @param key  - pass key to access the grid, two part auth in other words
     * @return existing or new grid, never null
     */
    public static WirelessGrid getOrCreateGrid(World world, TileEntity tile, String name, short key)
    {
        HashMap<Pair<String, Short>, WirelessGrid> grids = worldToGrids.get(world.provider.dimensionId);
        if (grids == null)
        {
            grids = new HashMap();
        }

        Pair pair = new Pair(name, key);
        if (grids.containsKey(pair))
        {
            return grids.get(pair);
        }
        WirelessGrid grid = WirelessGrid.newGrid(tile, name, key);
        grids.put(pair, grid);
        worldToGrids.put(world.provider.dimensionId, grids);
        return grid;
    }
}
