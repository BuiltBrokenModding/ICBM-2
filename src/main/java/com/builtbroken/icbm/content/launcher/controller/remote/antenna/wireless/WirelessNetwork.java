package com.builtbroken.icbm.content.launcher.controller.remote.antenna.wireless;

import com.builtbroken.mc.api.map.radio.IRadioWaveReceiver;
import com.builtbroken.mc.api.map.radio.IRadioWaveSender;
import com.builtbroken.mc.lib.transform.region.Cube;
import net.minecraft.tileentity.TileEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/20/2016.
 */
public class WirelessNetwork
{
    public final String name;
    public final short key;

    /** All tiles connected to this network */
    List<TileEntity> connectedTiles = new ArrayList();
    /** All radio wave senders part of this network */
    List<IRadioWaveSender> senders = new ArrayList();
    /** All radio wave receivers part of this network */
    List<IRadioWaveReceiver> receivers = new ArrayList();
    /** All sub networks connected to this network but not shared with connections */
    List<WirelessNetwork> subNetworks = new ArrayList();
    /** All networks connected to this network, byte is used to store the state of the connection 0 = lost connection, 1 = send only, 2 = receiver only, 3 = send & receive */
    HashMap<WirelessNetwork, Byte> connectedNetworks = new HashMap();

    /** Area we can send messages in */
    List<Cube> sendArea = new ArrayList();

    /** Area we can receive messages in */
    List<Cube> receiverArea = new ArrayList();

    public WirelessNetwork(String name, short key)
    {
        this.name = name;
        this.key = key;
    }

    public void connect(WirelessNetwork network)
    {
        boolean send = isInsideSendArea(network);
        boolean receive = isInsideReceiveArea(network);
        byte b = (byte) (send && receive ? 3 : send ? 2 : receive ? 1 : 0);
        connectedNetworks.
    }

    /**
     * Checks to see if the two networks overlap each other
     *
     * @param network
     * @return
     */
    public boolean overlaps(WirelessNetwork network)
    {
        //If we have a connection stored then we should already overlap
        if (connectedNetworks.containsKey(network) || subNetworks.contains(network))
        {
            return true;
        }
        return false;
    }

    /**
     * Checks to see if the network is inside
     *
     * @param network
     * @return
     */
    public boolean isInsideSendArea(WirelessNetwork network)
    {
        //Compare send area with receiver area
        for (Cube cube : network.receiverArea)
        {
            if (isInsideSendArea(cube))
            {
                return true;
            }
        }
        return false;
    }

    public boolean isInsideReceiveArea(WirelessNetwork network)
    {
        //Compare send area with receiver area
        for (Cube cube : network.sendArea)
        {
            if (isInsideReceiveArea(cube))
            {
                return true;
            }
        }
        return false;
    }

    public boolean overlaps(Cube cube)
    {
        if (isInsideSendArea(cube) || isInsideReceiveArea(cube))
        {
            return true;
        }
        return false;
    }

    public boolean isInsideSendArea(Cube cube)
    {
        return sendArea.contains(cube);
    }

    public boolean isInsideReceiveArea(Cube cube)
    {
        return receiverArea.contains(cube);
    }
}
