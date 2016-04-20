package com.builtbroken.icbm.content.launcher.controller.remote.antenna.wireless;

import com.builtbroken.jlib.type.Pair;
import com.builtbroken.mc.lib.transform.region.Cube;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/20/2016.
 */
public class WirelessGridManager
{
    /** Map of grid name & pass key pair, to grid objects */
    public static final HashMap<Pair<String, Short>, WirelessGrid> grids = new HashMap();

    public static List<WirelessGrid> getGridsInArea(Cube cube)
    {
        List<WirelessGrid> list = new ArrayList();
        for (WirelessGrid grid : grids.values())
        {
            if (grid.overlaps(cube) && !list.contains(grid))
            {
                list.add(grid);
            }
        }
        return list;
    }

    public static WirelessGrid getOrCreateGrid(String name, short key, WirelessNetwork network)
    {
        Pair pair = new Pair(name, key);
        if (grids.containsKey(pair))
        {
            return grids.get(pair);
        }
        WirelessGrid grid = new WirelessGrid();
        grid.connect(network);
        grids.put(pair, grid);
        return grid;
    }
}
