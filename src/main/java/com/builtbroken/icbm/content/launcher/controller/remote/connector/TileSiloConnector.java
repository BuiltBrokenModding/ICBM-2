package com.builtbroken.icbm.content.launcher.controller.remote.connector;

import com.builtbroken.mc.prefab.tile.Tile;

/**
 * Works like the TileController linking silos together into a control group. This group is then broadcasted back to the uplink tile.
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/26/2016.
 */
public class TileSiloConnector extends Tile
{
    @Override
    public Tile newTile()
    {
        return new TileSiloConnector();
    }
}
