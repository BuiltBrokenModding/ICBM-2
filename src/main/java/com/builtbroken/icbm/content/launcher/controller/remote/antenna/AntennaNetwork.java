package com.builtbroken.icbm.content.launcher.controller.remote.antenna;

import com.builtbroken.mc.lib.transform.region.Cube;
import com.builtbroken.mc.lib.transform.vector.Pos;
import net.minecraft.tileentity.TileEntity;

import java.util.ArrayList;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/18/2016.
 */
public class AntennaNetwork extends ArrayList<TileAntennaPart>
{
    public static int MAX_HEIGH_GAIN = 1000;
    public static int RANGE_PER_PEICE = 100;

    TileAntenna base;

    /** Bound size of the antenna array */
    Cube size = new Cube();

    int northPartCount;
    int southPartCount;
    int eastPartCount;
    int westPartCount;

    /** Range of the antenna */
    Cube range = new Cube();

    boolean massAdd = false;

    @Override
    public boolean add(TileAntennaPart e)
    {
        if (super.add(e))
        {
            e.network = this;

            //Update bounds
            if (!massAdd)
            {
                int ux = size.max().xi();
                int uy = size.max().yi();
                int uz = size.max().zi();
                int lx = size.min().xi();
                int ly = size.min().yi();
                int lz = size.min().zi();

                int x = e.xCoord;
                int y = e.yCoord;
                int z = e.zCoord;
                //Check x limits
                if (x > ux)
                {
                    ux = x;
                }
                else if (x < lx)
                {
                    lx = x;
                }
                //Check y limits
                if (y > uy)
                {
                    uy = y;
                }
                else if (y < ly)
                {
                    ly = y;
                }
                //Check z limits
                if (z > uz)
                {
                    uz = z;
                }
                else if (z < lz)
                {
                    lz = z;
                }

                Pos upper = new Pos(ux, uy, uz);
                Pos lower = new Pos(lx, ly, lz);

                if (upper != size.max() || lower != size.min())
                {
                    size = new Cube(lower, upper);
                    onBoundsChange();
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Called to update the size of the array
     */
    public void updateBounds()
    {
        //Rest part count
        eastPartCount = westPartCount = southPartCount = northPartCount = 0;

        //Get current bounds
        int ux = size.max().xi();
        int uy = size.max().yi();
        int uz = size.max().zi();
        int lx = size.min().xi();
        int ly = size.min().yi();
        int lz = size.min().zi();

        for (TileAntennaPart part : this)
        {
            int x = part.xCoord;
            int y = part.yCoord;
            int z = part.zCoord;
            //Check x limits
            if (x > ux)
            {
                ux = x;
            }
            else if (x < lx)
            {
                lx = x;
            }
            //Check y limits
            if (y > uy)
            {
                uy = y;
            }
            else if (y < ly)
            {
                ly = y;
            }
            //Check z limits
            if (z > uz)
            {
                uz = z;
            }
            else if (z < lz)
            {
                lz = z;
            }
            //Base should almost never be null, but just in case
            if (base != null)
            {
                if (x > base.xCoord)
                {
                    eastPartCount++;
                }
                else if (x < base.xCoord)
                {
                    westPartCount++;
                }
                if (z > base.xCoord)
                {
                    southPartCount++;
                }
                else if (z < base.xCoord)
                {
                    northPartCount++;
                }
            }
        }

        Pos upper = new Pos(ux, uy, uz);
        Pos lower = new Pos(lx, ly, lz);

        if (upper != size.max() || lower != size.min())
        {
            size = new Cube(lower, upper);
            onBoundsChange();
        }
    }

    /**
     * Called anytime the size of the array changes
     */
    protected void onBoundsChange()
    {
        //Range gained by size of antenna
        int heighRangeBonus = MAX_HEIGH_GAIN / Math.max(50 - (size.max().yi() - size.min().yi()), 1);
        //Range gained from y level of antenna
        int yLevelRangeBonus = size.max().yi() < 64 ? -2000 : MAX_HEIGH_GAIN / Math.max(256 - 64 - size.max().yi(), 1);

        range = new Cube(westPartCount * RANGE_PER_PEICE, 0, northPartCount * RANGE_PER_PEICE, eastPartCount * RANGE_PER_PEICE, 256, southPartCount * RANGE_PER_PEICE);
        range.add(heighRangeBonus, heighRangeBonus, heighRangeBonus);
        range.add(yLevelRangeBonus, yLevelRangeBonus, yLevelRangeBonus);
    }

    /**
     * Joins two networks together
     *
     * @param mergePoint - tile being merged, mainly used to access network
     */
    public void merge(TileAntennaPart mergePoint)
    {
        //Ensure we don't do too many logic updates
        massAdd = true;

        //Merge point needs to have a network to merge
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
                    updateBounds();
                    break;
                }
            }
        }

        //Revert mass add check
        massAdd = false;
    }

    /**
     * Called to split a network when a tile is removed
     *
     * @param splitPoint
     */
    public void split(TileAntennaPart splitPoint)
    {
        TileAntenna base = this.base;
        kill(); //Simpler to just kill the network and let it rebuild
        if (splitPoint != base)
        {
            base.doInitScan();
        }
    }

    public void kill()
    {
        for (TileAntennaPart tile : this)
        {
            tile.network = null;
        }
        this.clear();
        base = null;
    }
}
