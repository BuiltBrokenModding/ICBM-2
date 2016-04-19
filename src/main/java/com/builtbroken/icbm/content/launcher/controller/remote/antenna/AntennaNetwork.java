package com.builtbroken.icbm.content.launcher.controller.remote.antenna;

import net.minecraft.tileentity.TileEntity;

import java.util.ArrayList;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/18/2016.
 */
public class AntennaNetwork extends ArrayList<TileAntennaPart>
{
    TileAntenna base;

    @Override
    public boolean add(TileAntennaPart e)
    {
        if (super.add(e))
        {
            e.network = this;
            return true;
        }
        return false;
    }

    /**
     * Joins two networks together
     *
     * @param mergePoint - tile being merged, mainly used to access network
     */
    public void merge(TileAntennaPart mergePoint)
    {
        if (mergePoint.network != null)
        {
            //Loop threw merge point's connections
            for (TileEntity tile : mergePoint.connections.values())
            {
                //If connection is part of this network add parts
                if (tile instanceof TileAntennaPart && contains(tile))
                {
                    //Loop threw other network's parts
                    for (TileAntennaPart part : ((TileAntennaPart) tile).network)
                    {
                        if (!contains(part))
                        {
                            add(part);
                        }
                    }
                    break;
                }
            }
        }
    }

    /**
     * Called to split a network when a tile is removed
     *
     * @param splitPoint
     */
    public void split(TileAntennaPart splitPoint)
    {
        if (splitPoint.connections.size() > 1)
        {
            for (TileAntennaPart tile : this)
            {
                tile.network = null;
            }
            this.clear();
            base.doInitScan();
            base = null;
        }
        else
        {
            remove(splitPoint);
            splitPoint.network = null;
            if (splitPoint == base)
            {
                base = null;
            }
        }
    }
}
