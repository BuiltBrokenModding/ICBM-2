package com.builtbroken.icbm.content.crafting.station.small.auto;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles linking of station together for smoother animations and operations
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/11/2016.
 */
public class MissileAssemblyLine
{
    private List<TileSMAutoCraft> stations;

    public MissileAssemblyLine()
    {
        stations = new ArrayList();
    }

    /**
     * Called to merge another line into this line
     *
     * @param line - assembly line
     */
    public void merge(MissileAssemblyLine line)
    {
        if (!line.stations.isEmpty())
        {
            line.stations.stream().forEach(s -> add(s));
            line.kill();
        }
    }

    /**
     * Called to add a station to this line.
     * <p>
     * Will search for lines to merge into this one
     *
     * @param station
     */
    public void add(TileSMAutoCraft station)
    {
        //TODO add tile if not duplicate
        //TODO check for merge conditions
    }

    public void kill()
    {
        //TODO tell each station is no longer has a line object
        stations.clear();
    }
}
