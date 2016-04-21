package com.builtbroken.icbm.content.launcher.controller.remote.antenna.wireless;

import com.builtbroken.mc.api.map.radio.IRadioWaveReceiver;
import com.builtbroken.mc.api.map.radio.IRadioWaveSender;
import com.builtbroken.mc.lib.transform.region.Cube;
import com.builtbroken.mc.lib.transform.vector.Pos;
import net.minecraft.tileentity.TileEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Wireless communication grid.
 * <p>
 * Has two layers, receiver layer and sender layer. Each layer is separated to make it easier
 * to understand where data can be sent and where data can be received.
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/20/2016.
 */
public class WirelessGrid
{
    //TODO code custom sub list iterator to pretend two lists are the same list
    public final String name;
    public final short key;
    /** List of networks, in theory there should only ever be one network unless the networks are split */
    public List<WirelessNetworkReceive> receiverNetworks = new ArrayList();
    /** List of networks, in theory there should only ever be one network unless the networks are split */
    public List<WirelessNetworkSend> senderNetworks = new ArrayList();

    public WirelessGrid(String name, short key)
    {
        if (name == null)
        {
            throw new IllegalArgumentException("Grid name can not be null");
        }
        this.name = name;
        this.key = key;
    }

    /**
     * Creates a new network grid
     *
     * @param start - tile that is creating the network
     * @param name  - name of the network
     * @param key   - pass key for the network
     * @return new network grid
     */
    public static WirelessGrid newGrid(TileEntity start, String name, short key)
    {
        WirelessGrid grid = new WirelessGrid(name, key);
        grid.receiverNetworks.add(new WirelessNetworkReceive());
        grid.senderNetworks.add(new WirelessNetworkSend());
        grid.connect(start, null);
        return grid;

    }

    /**
     * Connects a tile to the grid
     *
     * @param tileEntity - tile to connect
     * @param cube       - area to check for connection, if outside of
     *                   area no connection will be made. If null it
     *                   will connect to all networks. Avoid connecting
     *                   to all networks as this will change behavior.
     */
    public boolean connect(TileEntity tileEntity, Cube cube)
    {
        boolean connected = false;
        if (cube != null)
        {
            for (WirelessNetwork network : receiverNetworks)
            {
                if (network.overlaps(cube) && network.connect(tileEntity))
                {
                    connected = true;
                }
            }
            for (WirelessNetwork network : senderNetworks)
            {
                if (network.overlaps(cube) && network.connect(tileEntity))
                {
                    connected = true;
                }
            }
        }
        else
        {
            Pos pos = new Pos(tileEntity);
            for (WirelessNetwork network : receiverNetworks)
            {
                if (network.overlaps(pos) && network.connect(tileEntity))
                {
                    connected = true;
                }
            }
            for (WirelessNetwork network : senderNetworks)
            {
                if (network.overlaps(pos) && network.connect(tileEntity))
                {
                    connected = true;
                }
            }
        }
        return connected;
    }

    /**
     * Connects a wireless network to this grid. If the network
     * overlaps another network they will merge with each other.
     * To avoid merging consider making sub networks instead of
     * connecting to the main grid.
     *
     * @param network - network to connect/merge
     */
    public boolean connect(WirelessNetwork network)
    {
        if (network instanceof WirelessNetworkReceive)
        {
            for (WirelessNetwork net : receiverNetworks)
            {
                if (net.overlaps(network))
                {
                    if (net.merge(network))
                    {
                        //Notify tiles of coverage range change
                        for (TileEntity t : network.connectedTiles)
                        {
                            if (t instanceof IWirelessGridConnector)
                            {
                                ((IWirelessGridConnector) t).onCoverageAreaChanged(network);
                            }
                        }
                        return true;
                    }
                }
            }
            return receiverNetworks.add((WirelessNetworkReceive) network);
        }
        else if (network instanceof WirelessNetworkSend)
        {
            for (WirelessNetwork net : senderNetworks)
            {
                if (net.overlaps(network))
                {
                    if (net.merge(network))
                    {
                        //Notify tiles of coverage range change
                        for (TileEntity t : network.connectedTiles)
                        {
                            if (t instanceof IWirelessGridConnector)
                            {
                                ((IWirelessGridConnector) t).onCoverageAreaChanged(network);
                            }
                        }
                        return true;
                    }
                }
            }
            return senderNetworks.add((WirelessNetworkSend) network);
        }
        return false;
    }

    /**
     * Connects a receive to the grid
     *
     * @param tile
     * @return
     */
    public boolean connect(IRadioWaveReceiver tile)
    {
        boolean connected = false;
        Cube cube = tile.getRadioReceiverRange();
        if (cube != null)
        {
            for (WirelessNetworkReceive network : receiverNetworks)
            {
                if (network.overlaps(cube) && network.addReceiver(tile))
                {
                    connected = true;
                    //Update connected tiles with network range change
                    for (TileEntity t : network.connectedTiles)
                    {
                        if (t != tile && t instanceof IWirelessGridConnector)
                        {
                            ((IWirelessGridConnector) t).onCoverageAreaChanged(network);
                        }
                    }
                }
            }
            if (!connected)
            {
                WirelessNetworkReceive network = new WirelessNetworkReceive();
                network.addReceiver(tile);
                return connect(network);
            }
        }
        return connected;
    }

    /**
     * Connects a sender to the grid
     *
     * @param tile
     * @return
     */
    public boolean connect(IRadioWaveSender tile)
    {
        boolean connected = false;
        Cube cube = tile.getRadioSenderRange();
        if (cube != null)
        {
            for (WirelessNetworkSend network : senderNetworks)
            {
                if (network.overlaps(cube) && network.addSender(tile))
                {
                    connected = true;
                    //Update connected tiles with network range change
                    for (TileEntity t : network.connectedTiles)
                    {
                        if (t != tile && t instanceof IWirelessGridConnector)
                        {
                            ((IWirelessGridConnector) t).onCoverageAreaChanged(network);
                        }
                    }
                }
            }
            if (!connected)
            {
                WirelessNetworkSend network = new WirelessNetworkSend();
                network.addSender(tile);
                return connect(network);
            }
        }
        return connected;
    }

    /**
     * Checks to see if a cube over laps either the receiver
     * or sender side of the grid
     *
     * @param cube
     * @return
     */
    public boolean overlaps(Cube cube)
    {
        for (WirelessNetwork network : receiverNetworks)
        {
            if (network.overlaps(cube))
            {
                return true;
            }
        }
        for (WirelessNetwork network : senderNetworks)
        {
            if (network.overlaps(cube))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Removes a tile from the grid
     *
     * @param tile
     */
    public void disconnect(TileEntity tile)
    {
        for (WirelessNetwork network : receiverNetworks)
        {
            network.disconnect(tile);
        }
        for (WirelessNetwork network : senderNetworks)
        {
            network.disconnect(tile);
        }
    }

    public void disconnect(IRadioWaveReceiver tile)
    {
        for (WirelessNetworkReceive network : receiverNetworks)
        {
            if (network.removeReceiver(tile))
            {
                for (TileEntity t : network.connectedTiles)
                {
                    if (t != tile && t instanceof IWirelessGridConnector)
                    {
                        ((IWirelessGridConnector) t).onCoverageAreaChanged(network);
                    }
                }
            }
        }
    }

    public void disconnect(IRadioWaveSender tile)
    {
        for (WirelessNetworkSend network : senderNetworks)
        {
            if (network.removeSender(tile))
            {
                for (TileEntity t : network.connectedTiles)
                {
                    if (t != tile && t instanceof IWirelessGridConnector)
                    {
                        ((IWirelessGridConnector) t).onCoverageAreaChanged(network);
                    }
                }
            }
        }
    }

    /**
     * Checks to see if the grid contains the point
     *
     * @param pos
     * @return true if any network overlaps the point
     */
    public boolean overlaps(Pos pos)
    {
        for (WirelessNetwork network : receiverNetworks)
        {
            if (network.overlaps(pos))
            {
                return true;
            }
        }
        for (WirelessNetwork network : senderNetworks)
        {
            if (network.overlaps(pos))
            {
                return true;
            }
        }
        return false;
    }

    public boolean equals(Object object)
    {
        if (object == this)
        {
            return true;
        }
        if (object instanceof WirelessGrid)
        {
            return ((WirelessGrid) object).name.equals(name) && ((WirelessGrid) object).key == key;
        }
        return false;
    }

    @Override
    public String toString()
    {
        return "WirelessGrid[" + name + ", " + key + ", " + receiverNetworks.size() + "R, " + senderNetworks.size() + "S]";
    }
}
