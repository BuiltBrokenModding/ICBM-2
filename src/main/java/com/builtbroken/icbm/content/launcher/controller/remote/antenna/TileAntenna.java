package com.builtbroken.icbm.content.launcher.controller.remote.antenna;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.jlib.helpers.MathHelper;
import com.builtbroken.jlib.lang.StringHelpers;
import com.builtbroken.mc.api.map.radio.IRadioWaveReceiver;
import com.builtbroken.mc.api.map.radio.IRadioWaveSender;
import com.builtbroken.mc.api.tile.IGuiTile;
import com.builtbroken.mc.lib.transform.region.Cube;
import com.builtbroken.mc.lib.world.radio.RadioRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Core logic tile for antenna structure and linking to other tiles
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/26/2016.
 */
public class TileAntenna extends TileAntennaPart implements IGuiTile, IRadioWaveReceiver, IRadioWaveSender
{
    private boolean hasInitScanned = false;
    private int randomTick = 30;

    /**
     * Called to scan the structure of the antenna
     */
    public void doInitScan()
    {
        //TODO multi-thread
        //TODO add pausing to allow world to continue updating, if not thread
        hasInitScanned = true;
        if (network == null)
        {
            network = new AntennaNetwork();
            network.add(this);
            network.base = this;
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
                network.add(currentNode);
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

        network.updateBounds();
        RadioRegistry.addOrUpdate(this);
    }

    @Override
    public void updateEntity()
    {
        super.updateEntity();
        //Sanity check to ensure structure is still good, roughly 2 mins with a slight random
        if (!world().isRemote)
        {
            if(ticks >= randomTick)
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
    public void invalidate()
    {
        super.invalidate();
        if(network != null)
        {
            network.kill();
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
        //TODO notify all connected machines
        sender.onMessageReceived(this, hz, messageHeader, data);
    }

    @Override
    public Cube getRadioReceiverRange()
    {
        return network != null ? network.range : null;
    }

    @Override
    public void onMessageReceived(IRadioWaveReceiver receiver, float hz, String header, Object[] data)
    {
        //Nothing really to do here
    }

    @Override
    public void sendRadioMessage(float hz, String header, Object... data)
    {
        if (network == null)
        {
            doInitScan();
        }
        RadioRegistry.popMessage(world(), this, hz, header, data);
    }

    @Override
    public Cube getRadioSenderRange()
    {
        //TODO add power check
        //TODO increase range by power input
        return network != null ? network.range : null;
    }
}
