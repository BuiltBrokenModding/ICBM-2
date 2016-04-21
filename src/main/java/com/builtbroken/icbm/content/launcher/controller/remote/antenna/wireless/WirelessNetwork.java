package com.builtbroken.icbm.content.launcher.controller.remote.antenna.wireless;

import com.builtbroken.mc.lib.transform.region.Cube;
import com.builtbroken.mc.lib.transform.vector.Pos;
import net.minecraft.tileentity.TileEntity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Network of connected radio tiles
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/20/2016.
 */
public abstract class WirelessNetwork
{
    /** Is the network a receiver network, eg only get messages doesn't sent messages */
    public final boolean receiver;

    public boolean isInvalid = false;

    /** All tiles connected to this network */
    public final List<TileEntity> connectedTiles = new ArrayList();

    /** Area we can send messages in */
    public final List<Cube> coverageArea = new ArrayList();

    public WirelessNetwork(boolean receiver)
    {
        this.receiver = receiver;
    }

    /**
     * Called to connect a tile to the network
     *
     * @param tile
     * @return true if connected
     */
    public boolean connect(TileEntity tile)
    {
        if (!connectedTiles.contains(tile))
        {
            return connectedTiles.add(tile);
        }
        return false;
    }

    /**
     * Disconnects the tile from the network
     *
     * @param tile
     * @return true if the tile was contained and removed
     */
    public boolean disconnect(TileEntity tile)
    {
        if (connectedTiles.contains(tile))
        {
            return connectedTiles.remove(tile);
        }
        return false;
    }

    /**
     * Called to merge the two networks together. {@link #overlaps(WirelessNetwork)}
     * is already called by {@link WirelessGrid} before calling this method.
     *
     * @param network - network being merged
     * @return true if the networks have merged into one network
     */
    public boolean merge(WirelessNetwork network)
    {
        for (TileEntity tile : network.connectedTiles)
        {
            connect(tile);
        }
        network.kill();
        return true;
    }

    protected void kill()
    {
        coverageArea.clear();
        connectedTiles.clear();
    }

    /**
     * Checks to see if the two networks overlap each other
     *
     * @param network
     * @return
     */
    public boolean overlaps(WirelessNetwork network)
    {
        //TODO check larger box, to improve performance
        for (Cube cube : network.coverageArea)
        {
            if (overlaps(cube))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the cube overlaps any of the coverage area's cubes
     *
     * @param cube
     * @return
     */
    public boolean overlaps(Cube cube)
    {
        //TODO check larger box, to improve performance
        return coverageArea.contains(cube);
    }

    /**
     * Checks if the point is inside the network's coverage area
     *
     * @param pos
     * @return
     */
    public boolean overlaps(Pos pos)
    {
        //TODO check larger box, to improve performance
        for (Cube cube : coverageArea)
        {
            if (cube.intersects(pos))
            {
                return true;
            }
        }
        return false;
    }

    public void doCleanUp()
    {
        Iterator<TileEntity> tiles = connectedTiles.iterator();
        while (tiles.hasNext())
        {
            TileEntity tile = tiles.next();
            if (tile.isInvalid())
            {
                tiles.remove();
            }
        }
    }

    public boolean isInvalid()
    {
        return isInvalid;
    }
}
