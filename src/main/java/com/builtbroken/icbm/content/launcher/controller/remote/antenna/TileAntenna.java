package com.builtbroken.icbm.content.launcher.controller.remote.antenna;

import com.builtbroken.jlib.helpers.MathHelper;
import com.builtbroken.mc.api.map.radio.IRadioWaveExternalReceiver;
import com.builtbroken.mc.api.map.radio.IRadioWaveReceiver;
import com.builtbroken.mc.api.map.radio.IRadioWaveSender;
import com.builtbroken.mc.api.map.radio.RadioTowerStatus;
import com.builtbroken.mc.api.map.radio.wireless.*;
import com.builtbroken.mc.api.tile.access.IGuiTile;
import com.builtbroken.mc.imp.transform.region.Cube;
import com.builtbroken.mc.lib.world.radio.RadioRegistry;
import com.builtbroken.mc.lib.world.radio.network.WirelessNetwork;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.*;

/**
 * Core logic tile for antenna structure and linking to other tiles
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/26/2016.
 */
public class TileAntenna extends TileAntennaPart implements IGuiTile, IWirelessNetworkHub, IWirelessConnector, IRadioWaveSender, IRadioWaveReceiver
{
    //TODO add TESR to show connection status, gen network, and network data when player is looking at the tile
    //TODO at distance show LED lights if not blocked by wall
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

    /** Next time, in ticks, the {@link #antennaNetwork} will be trigger to re-path the antenna frame */
    private int randomTick = 30;

    //TODO implement
    protected String displayName;
    protected String networkName;
    protected String passKey;

    @Override
    public void updateEntity()
    {
        super.updateEntity();
        //Sanity check to ensure structure is still good, roughly 2 mins with a slight random
        if (!world().isRemote)
        {
            //TODO add a debug line render to each tower connected
            //Sine we have no way to be sure of connection, update radio map every second
            if (ticks % 20 == 0)
            {
                if (towerStatus == RadioTowerStatus.ONLINE)
                {
                    RadioRegistry.add(this);
                }
                else
                {
                    RadioRegistry.remove(this);
                }
            }
            //Check to build network if required
            if (isGeneratingNetwork() && towerStatus == RadioTowerStatus.ONLINE)
            {
                buildWirelessNetwork();
            }
            //Kill network if not required
            else if (wirelessNetwork != null)
            {
                setWirelessNetwork(null);
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

    /**
     * Called to build the wireless network
     */
    protected void buildWirelessNetwork()
    {
        if (wirelessNetwork == null)
        {
            setWirelessNetwork(new WirelessNetwork(this));
        }
    }

    @Override
    protected void updateConnections()
    {
        HashMap<ForgeDirection, TileEntity> oldConnections = connections;
        connections = new HashMap();
        super.updateConnections();
        //TODO improve using an iterator that removes connections from old cache
        //Notify when tiles are removed
        for (TileEntity tile : oldConnections.values())
        {
            if (tile instanceof IWirelessNetworkObject && !connections.values().contains(tile))
            {
                for (IWirelessNetwork network : getAttachedNetworks())
                {
                    network.removeConnection(this, (IWirelessNetworkObject) tile);
                }
                if (wirelessNetwork != null)
                {
                    wirelessNetwork.removeConnection(this, (IWirelessNetworkObject) tile);
                }
            }
        }
        //Notify when tiles are added
        for (TileEntity tile : connections.values())
        {
            if (tile instanceof IWirelessNetworkObject && !oldConnections.values().contains(tile))
            {
                for (IWirelessNetwork network : getAttachedNetworks())
                {
                    network.addConnection(this, (IWirelessNetworkObject) tile);
                }
                if (wirelessNetwork != null)
                {
                    wirelessNetwork.addConnection(this, (IWirelessNetworkObject) tile);
                }
            }
        }
    }

    @Override
    protected boolean canConnect(ForgeDirection side, TileEntity tile)
    {
        return tile != null;
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
                network.removeConnector(this);
            }
        }
        RadioRegistry.remove(this);
    }

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
            antennaNetwork = new AntennaNetwork(this);
            antennaNetwork.add(this);
        }
        if (connections.isEmpty())
        {
            updateConnections();
        }

        long time = System.nanoTime();
        //ICBM.INSTANCE.logger().info(this + "  Running structure scan...");
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
        //ICBM.INSTANCE.logger().info(this + "  Finished structure scan took " + StringHelpers.formatNanoTime(time));

        antennaNetwork.updateBounds();
        RadioRegistry.addOrUpdate(this);
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
        return antennaNetwork != null ? antennaNetwork.senderRange : null;
    }

    @Override
    public Cube getRadioReceiverRange()
    {
        //Receive range is equal to size of the antenna
        return antennaNetwork != null ? antennaNetwork.receiveRange : null;
    }

    @Override
    public Cube getRadioSenderRange()
    {
        return antennaNetwork != null ? antennaNetwork.senderRange : null;
    }

    @Override
    public IWirelessNetwork getWirelessNetwork()
    {
        return wirelessNetwork;
    }

    /**
     * Sets the wireless network for the antenna
     *
     * @param wirelessNetwork
     */
    public void setWirelessNetwork(WirelessNetwork wirelessNetwork)
    {
        //setting network to null but we had a network
        if (wirelessNetwork == null && this.wirelessNetwork != null)
        {
            this.wirelessNetwork.kill();
        }
        //changing networks
        else if (wirelessNetwork != null && this.wirelessNetwork != null && wirelessNetwork != this.wirelessNetwork)
        {
            this.wirelessNetwork.kill();
        }

        this.wirelessNetwork = wirelessNetwork;
        if (this.wirelessNetwork != null)
        {
            if(this.connections.size() == 0)
            {
                this.updateConnections();
            }
            this.wirelessNetwork.addConnector(this);
        }
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
            return objects;
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
        if (network != null && network.getPrimarySender() != null && Math.abs(network.getHz() - hz) <= 0.001)
        {
            //In order to communicate the sender and receiver ranges need to match/
            //Sender's send range needs to be in receiver range
            //Sender's receiver range needs to be in our send range
            Cube receiverRangeSender = network.getPrimarySender() instanceof IRadioWaveReceiver ? ((IRadioWaveReceiver) network.getPrimarySender()).getRadioReceiverRange() : network.getPrimarySender().getWirelessCoverageArea();
            Cube senderRangeSender = network.getPrimarySender() instanceof IRadioWaveSender ? ((IRadioWaveSender) network.getPrimarySender()).getRadioSenderRange() : network.getPrimarySender().getWirelessCoverageArea();
            if (receiverRangeSender != null && senderRangeSender != null && getRadioSenderRange().doesOverlap(receiverRangeSender) && senderRangeSender.doesOverlap(getRadioReceiverRange()))
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

    /**
     * Sets the tower's radio status. If online -> anything else
     * the network will terminate itself.
     *
     * @param towerStatus - status
     */
    public void setTowerStatus(RadioTowerStatus towerStatus)
    {
        this.towerStatus = towerStatus;
        if (!world().isRemote)
        {
            if (towerStatus != RadioTowerStatus.ONLINE && wirelessNetwork != null)
            {
                setWirelessNetwork(null);
            }
            else if (towerStatus == RadioTowerStatus.ONLINE && generateNetwork && wirelessNetwork == null)
            {
                buildWirelessNetwork();
            }
        }
    }

    /**
     * Is the antenna generating a wireless network
     *
     * @return
     */
    public boolean isGeneratingNetwork()
    {
        return generateNetwork;
    }

    public void setGenerateNetwork(boolean generateNetwork)
    {
        this.generateNetwork = generateNetwork;
        if (!world().isRemote)
        {
            if (!generateNetwork && wirelessNetwork != null)
            {
                setWirelessNetwork(null);
            }
            else if (generateNetwork && towerStatus == RadioTowerStatus.ONLINE && wirelessNetwork == null)
            {
                buildWirelessNetwork();
            }
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        if (nbt.hasKey("hz"))
        {
            hz = nbt.getFloat("hz");
        }
        if (nbt.hasKey("towerStatus"))
        {
            int v = nbt.getInteger("towerStatus");
            if (v >= 0 && v < RadioTowerStatus.values().length)
            {
                towerStatus = RadioTowerStatus.values()[v];
            }
        }
        generateNetwork = nbt.getBoolean("genNet");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setFloat("hz", hz);
        nbt.setInteger("towerStatus", towerStatus.ordinal());
        nbt.setBoolean("genNet", generateNetwork);
    }

    @Override
    public String toString()
    {
        return "TileAntenna[" + world().provider.dimensionId + "d, " + xCoord + "x, " + yCoord + "y, " + zCoord + "z]@" + hashCode();
    }
}
