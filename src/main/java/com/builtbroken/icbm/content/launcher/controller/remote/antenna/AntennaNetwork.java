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

    /** Range of the antenna for sending data */
    Cube senderRange = new Cube();

    /** Range of the antenna for receiving data, always smaller than send range */
    Cube receiveRange = new Cube();

    boolean massAdd = false;

    public AntennaNetwork(TileAntenna base)
    {
        this.base = base;
        size = new Cube(0, 0, 0, 0, 1, 0); //By default we are one block tall
        onBoundsChange();
    }

    @Override
    public boolean add(TileAntennaPart e)
    {
        if (base != null && !this.contains(e) && super.add(e))
        {
            e.antennaNetwork = this;

            //Update bounds
            if (!massAdd)
            {
                //TODO this really needs JUnit tested
                int ux = size.max().xi();
                int uy = size.max().yi();
                int uz = size.max().zi();
                int lx = size.min().xi();
                int ly = size.min().yi();
                int lz = size.min().zi();

                int x = e.xCoord - base.xCoord;
                int y = e.yCoord - base.yCoord;
                int z = e.zCoord - base.zCoord;
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

                Cube cube = new Cube(new Pos(lx, ly, lz), new Pos(ux, uy, uz));
                if (!size.equals(cube))
                {
                    size = cube;
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
        //Get current bounds
        int ux = size.max().xi();
        int uy = size.max().yi();
        int uz = size.max().zi();
        int lx = size.min().xi();
        int ly = size.min().yi();
        int lz = size.min().zi();

        for (TileAntennaPart part : this)
        {
            int x = part.xCoord - base.xCoord;
            int y = part.yCoord - base.yCoord;
            int z = part.zCoord - base.zCoord;
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
        }
        Cube cube = new Cube(new Pos(lx, ly, lz), new Pos(ux, uy, uz));

        if (!size.equals(cube))
        {
            size = cube;
            onBoundsChange();
        }
    }

    /**
     * Called anytime the size of the array changes
     */
    protected void onBoundsChange()
    {
        if (base != null)
        {
            //TODO Record these values for GUI display
            //Range gained by size of antenna
            int heighRangeBonus = MAX_HEIGH_GAIN / Math.max(50 - (int) size.getSizeY(), 1); //TODO fix round error
            //Range gained from y level of antenna
            int yLevelRangeBonus = (size.max().yi()+ base.yCoord) < 64 ? -2000 : MAX_HEIGH_GAIN / Math.max(192 - size.max().yi() + base.yCoord, 1);
            int r = Math.max(heighRangeBonus + yLevelRangeBonus, 0);

            //Get ranges without adding neg values to ensure range doesn't go up when converted to cube
            int westRange = Math.max(size.min().xi() * RANGE_PER_PEICE + r, 0);
            int northRange = Math.max(size.min().zi() * RANGE_PER_PEICE + r, 0);
            int eastRange = Math.max(size.max().xi() * RANGE_PER_PEICE + r, 0);
            int southRange = Math.max(size.max().zi() * RANGE_PER_PEICE + r, 0);

            //Add neg to west and north, ForgeDirection
            senderRange = new Cube(-westRange, 0, -northRange, eastRange, 256, southRange);

            //Move range by center of antenna
            senderRange.add(new Pos((TileEntity)base).add(0.5));
            receiveRange = size.clone().add(new Pos((TileEntity)base).add(0.5));
        }
        else
        {
            senderRange = new Cube();
            receiveRange = new Cube();
        }
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
        if (mergePoint.antennaNetwork != null && mergePoint.antennaNetwork != this)
        {
            //Loop threw merge point's connections
            for (TileEntity tile : mergePoint.connections.values())
            {
                //If connection is part of this network add parts
                if (tile instanceof TileAntennaPart && contains(tile))
                {
                    //Loop threw other network's parts
                    for (TileAntennaPart part : ((TileAntennaPart) tile).antennaNetwork)
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
        if (this.contains(splitPoint))
        {
            TileAntenna base = this.base;
            kill(); //Simpler to just kill the network and let it rebuild
            if (splitPoint != base)
            {
                base.doInitScan();
            }
        }
    }

    public void kill()
    {
        for (TileAntennaPart tile : this)
        {
            tile.antennaNetwork = null;
        }
        this.clear();
        base = null;
    }
}
