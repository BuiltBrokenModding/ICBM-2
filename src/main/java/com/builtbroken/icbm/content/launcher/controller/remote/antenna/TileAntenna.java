package com.builtbroken.icbm.content.launcher.controller.remote.antenna;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.jlib.helpers.MathHelper;
import com.builtbroken.jlib.lang.StringHelpers;
import com.builtbroken.mc.api.map.radio.IRadioWaveExternalReceiver;
import com.builtbroken.mc.api.map.radio.IRadioWaveSender;
import com.builtbroken.mc.api.map.radio.RadioTowerStatus;
import com.builtbroken.mc.api.map.radio.wireless.*;
import com.builtbroken.mc.api.tile.IGuiTile;
import com.builtbroken.mc.lib.transform.region.Cube;
import com.builtbroken.mc.lib.world.radio.RadioRegistry;
import com.builtbroken.mc.lib.world.radio.network.WirelessNetwork;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.*;

/**
 * Core logic tile for antenna structure and linking to other tiles
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/26/2016.
 */
public class TileAntenna extends TileAntennaPart implements IGuiTile, IWirelessNetworkHub, IWirelessConnector
{
    /** Connection status of the network */
    protected RadioTowerStatus towerStatus = RadioTowerStatus.OFFLINE;
    /** Network being created by this network */
    protected WirelessNetwork wirelessNetwork;

    /** List of networks connected to this antenna */
    protected List<IWirelessNetwork> connectedNetworks = new ArrayList();

    /** Frequency of the  {@link #wirelessNetwork} */
    protected float hz = 0f;
    /** Should we generate a wireless network to attach to other antennas */
    protected boolean generateNetwork = false;

    /** Next time the {@link #antennaNetwork} does a remap */
    private int randomTick = 30;


    /**
     * Called to scan the structure of the antenna
     */
    public void doInitScan()
    {
        //TODO delay scan if player is placing and breaking blocks near the antenna
        //TODO multi-thread
        //TODO add pausing to allow world to continue updating, if not thread
        if (antennaNetwork == null)
        {
            antennaNetwork = new AntennaNetwork();
            antennaNetwork.add(this);
            antennaNetwork.base = this;
        }
        if (connections.isEmpty())
        {
            updateConnections();
        }

        long time = System.nanoTime();
        ICBM.INSTANCE.logger().info(this + "  Running structure scan...");
        //Start iterative pathfinder
        List<TileAntennaPart> pathedTiles = new ArrayList();
        Queue<TileAntennaPart> stack = new LinkedList();
        //Create stack to store current path nodes
        stack.offer(this);

        while (!stack.isEmpty())
        {
            //Pop a node off the stack each iteration
            TileAntennaPart currentNode = stack.poll();
            pathedTiles.add(currentNode); //Add tile to prevent repath

            //If not base then add to network
            if (currentNode != this)
            {
                antennaNetwork.add(currentNode);
            }

            //In rare cases, if connections are not mapped -> force an update
            if (currentNode.connections.size() == 0)
            {
                currentNode.updateConnections();
            }

            //One connection means we are connected only to currentNode
            if (currentNode.connections.size() > 1)
            {
                //Path all connections from the node
                for (TileEntity tile : currentNode.connections.values())
                {
                    //If an antenna part and not pathed add to stack
                    if (tile instanceof TileAntennaPart && !pathedTiles.contains(tile))
                    {
                        stack.offer((TileAntennaPart) tile);
                    }
                }
            }
        }
        time = System.nanoTime() - time;
        ICBM.INSTANCE.logger().info(this + "  Finished structure scan took " + StringHelpers.formatNanoTime(time));

        antennaNetwork.updateBounds();
        RadioRegistry.addOrUpdate(this);
    }

    @Override
    public void updateEntity()
    {
        super.updateEntity();
        //Sanity check to ensure structure is still good, roughly 2 mins with a slight random
        if (!world().isRemote)
        {
            if (generateNetwork && towerStatus == RadioTowerStatus.ONLINE && wirelessNetwork == null)
            {
                wirelessNetwork = new WirelessNetwork(hz, this);
            }
            else if (wirelessNetwork != null)
            {
                wirelessNetwork = null;
            }
            if (ticks % 100 == 0)
            {
                //TODO multi-thread
                if (wirelessNetwork != null)
                {
                    wirelessNetwork.updateConnections();
                }
            }
            if (ticks >= randomTick)
            {
                int addition = MathHelper.rand.nextInt(200) + 1000;
                if (addition + ticks < 0) //stack underflow
                {
                    randomTick = addition - (Integer.MAX_VALUE - ticks);
                }
                else
                {
                    randomTick = ticks + addition;
                }
                doInitScan();
            }
        }
    }

    @Override
    protected void updateConnections()
    {
        HashMap<ForgeDirection, TileEntity> oldConnections = connections;
        connections = new HashMap();
        super.updateConnections();
        if (getAttachedNetworks() != null && getAttachedNetworks().size() > 0)
        {
            //TODO improve using an iterator that removes connections from old cache
            //Notify when tiles are removed
            for (TileEntity tile : oldConnections.values())
            {
                if (tile instanceof IWirelessNetworkObject && !connections.values().contains(tile))
                {
                    for (IWirelessNetwork network : getAttachedNetworks())
                    {
                        network.onConnectionRemoved(this, (IWirelessNetworkObject) tile);
                    }
                    wirelessNetwork.onConnectionRemoved(this, (IWirelessNetworkObject) tile);
                }
            }
            //Notify when tiles are added
            for (TileEntity tile : connections.values())
            {
                if (tile instanceof IWirelessNetworkObject && !oldConnections.values().contains(tile))
                {
                    for (IWirelessNetwork network : getAttachedNetworks())
                    {
                        network.onConnectionAdded(this, (IWirelessNetworkObject) tile);
                    }
                    wirelessNetwork.onConnectionAdded(this, (IWirelessNetworkObject) tile);
                }
            }
        }
    }

    @Override
    public void invalidate()
    {
        super.invalidate();
        if (antennaNetwork != null)
        {
            antennaNetwork.kill();
        }
        if (wirelessNetwork != null)
        {
            wirelessNetwork.kill();
        }
        if (getAttachedNetworks() != null && getAttachedNetworks().size() > 0)
        {
            for (IWirelessNetwork network : getAttachedNetworks())
            {
                network.onConnectionRemoved(this);
            }
        }
        RadioRegistry.remove(this);
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player)
    {
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player)
    {
        return null;
    }

    @Override
    public void updateRenderState()
    {
        //Meta is always 2
    }

    @Override
    public void receiveRadioWave(float hz, IRadioWaveSender sender, String messageHeader, Object[] data)
    {
        if (sender != this) //Ensure we don't inf loop on messages received
        {
            //Send data to all connected receivers
            for (TileEntity tile : connections.values())
            {
                if (tile instanceof IRadioWaveExternalReceiver)
                {
                    ((IRadioWaveExternalReceiver) tile).receiveExternalRadioWave(hz, sender, this, messageHeader, data);
                }
            }
            //Notify sender that we received data
            sender.onMessageReceived(this, hz, messageHeader, data);
        }
    }

    @Override
    public Cube getWirelessCoverageArea()
    {
        return antennaNetwork != null ? antennaNetwork.range : null;
    }

    @Override
    public Cube getRadioReceiverRange()
    {
        //Receive range is equal to size of the antenna
        return antennaNetwork != null ? antennaNetwork.size : null;
    }

    @Override
    public IWirelessNetwork getWirelessNetwork()
    {
        return wirelessNetwork;
    }

    @Override
    public float getBroadCastFrequency()
    {
        return hz;
    }

    @Override
    public void sendRadioMessage(float hz, String header, Object... data)
    {
        if (antennaNetwork == null)
        {
            doInitScan();
        }
        RadioRegistry.popMessage(world(), this, hz, header, data);
    }

    @Override
    public List<IWirelessNetworkObject> getWirelessNetworkObjects()
    {
        if (towerStatus == RadioTowerStatus.ONLINE && connections.size() > 0)
        {
            List<IWirelessNetworkObject> objects = new ArrayList();
            for (Map.Entry<ForgeDirection, TileEntity> entry : connections.entrySet())
            {
                TileEntity tile = entry.getValue();
                if (tile instanceof IWirelessNetworkObject && !tile.isInvalid() && ((IWirelessNetworkObject) tile).canAcceptAntennaConnection(entry.getKey()))
                {
                    objects.add((IWirelessNetworkObject) tile);
                }
            }
        }
        return new ArrayList();
    }

    @Override
    public boolean addWirelessNetwork(IWirelessNetwork network)
    {
        if (towerStatus == RadioTowerStatus.ONLINE && !connectedNetworks.contains(network))
        {
            return connectedNetworks.add(network);
        }
        return false;
    }

    @Override
    public boolean removeWirelessNetwork(IWirelessNetwork network, ConnectionRemovedReason reason)
    {
        if (connectedNetworks.contains(network))
        {
            return connectedNetworks.remove(network);
        }
        return false;
    }

    @Override
    public boolean canConnectToNetwork(IWirelessNetwork network)
    {
        //Hz values need to match
        if (network.getPrimarySender() != null && Math.abs(network.getPrimarySender().getBroadCastFrequency() - hz) <= 0.001)
        {
            //In order to communicate the sender and receiver ranges need to match/
            //Sender's send range needs to be in receiver range
            //Sender's receiver range needs to be in our send range
            Cube receiverRangeSender = network.getPrimarySender().getRadioReceiverRange();
            Cube senderRangeSender = network.getPrimarySender().getRadioSenderRange();
            if (receiverRangeSender.doesOverlap(getRadioSenderRange()) && senderRangeSender.doesOverlap(getRadioReceiverRange()))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<IWirelessNetwork> getAttachedNetworks()
    {
        return connectedNetworks;
    }

    @Override
    public boolean canAcceptAntennaConnection(ForgeDirection side)
    {
        return false; // We are an antenna so we do not accept connections
    }

    @Override
    public String toString()
    {
        return "TileAntenna[" + world().provider.dimensionId + "d, " + xCoord + "x, " + yCoord + "y, " + zCoord + "z]@" + hashCode();
    }
}
