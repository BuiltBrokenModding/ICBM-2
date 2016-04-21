package com.builtbroken.icbm.content.launcher.controller.remote.central;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.content.launcher.controller.local.TileController;
import com.builtbroken.icbm.content.launcher.controller.remote.antenna.wireless.IWirelessGridConnector;
import com.builtbroken.icbm.content.launcher.controller.remote.antenna.wireless.WirelessGrid;
import com.builtbroken.icbm.content.launcher.controller.remote.antenna.wireless.WirelessGridManager;
import com.builtbroken.icbm.content.launcher.controller.remote.antenna.wireless.WirelessNetwork;
import com.builtbroken.icbm.content.launcher.controller.remote.connector.TileSiloConnector;
import com.builtbroken.mc.api.map.radio.IRadioWaveExternalReceiver;
import com.builtbroken.mc.api.map.radio.IRadioWaveReceiver;
import com.builtbroken.mc.api.map.radio.IRadioWaveSender;
import com.builtbroken.mc.api.tile.IGuiTile;
import com.builtbroken.mc.api.tile.ILinkFeedback;
import com.builtbroken.mc.api.tile.ILinkable;
import com.builtbroken.mc.api.tile.IPassCode;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.network.IPacketIDReceiver;
import com.builtbroken.mc.core.registry.implement.IPostInit;
import com.builtbroken.mc.lib.transform.region.Cube;
import com.builtbroken.mc.lib.transform.vector.Location;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.prefab.inventory.IPrefabInventory;
import com.builtbroken.mc.prefab.tile.TileModuleMachine;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.HashMap;
import java.util.Map;

/**
 * Centralized point for link all objects into a central wireless grid.
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/26/2016.
 */
public class TileCommandController extends TileModuleMachine implements ILinkable, IPacketIDReceiver, IGuiTile, IPostInit, IWirelessGridConnector, IRadioWaveExternalReceiver, IPrefabInventory
{
    /** List of linked silo connectors */
    protected final HashMap<Pos, TileSiloConnector> siloConnectors = new HashMap();
    /** Map of connections per side */
    protected HashMap<ForgeDirection, TileEntity> connections = new HashMap();
    /** Wireless grid connected to */
    protected WirelessGrid grid;

    /** Name of the grid to connect to */
    protected String gridName;
    /** Pass key to gain access to the grid, different key -> different grid */
    protected short gridKey;

    public TileCommandController()
    {
        super("remoteController", Material.iron);
        this.hardness = 10f;
        this.resistance = 10f;
    }

    @Override
    public void invalidate()
    {
        connections.clear();
        if (grid != null)
        {
            grid.disconnect(this);
            grid = null;
        }
        super.invalidate();
    }

    @Override
    public void update()
    {
        super.update();
        if (ticks % 60 == 0)
        {
            //Cache old connections for update logic
            HashMap<ForgeDirection, TileEntity> oldConnections = connections;
            connections = new HashMap();

            //Log if we have a sender and receiver
            boolean sender = false;
            boolean receiver = false;

            //Update connections
            for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
            {
                TileEntity tile = toPos().add(dir).getTileEntity(world());
                if (tile != null)
                {
                    connections.put(dir, tile);
                    //Check for sender
                    if (tile instanceof IRadioWaveSender)
                    {
                        sender = true;
                    }
                    //Check for receiver, sender & receiver can be the same tile
                    if (tile instanceof IRadioWaveReceiver)
                    {
                        receiver = true;
                    }
                }
            }

            //If we have a sender and receiver then we can connect to a wireless grid
            if (sender && receiver)
            {
                if (grid == null)
                {
                    //If we had not grid, find use a grid to connect to
                    grid = WirelessGridManager.getOrCreateGrid(world(), this, gridName, gridKey);
                    for (TileEntity tile : connections.values())
                    {
                        if (tile instanceof IRadioWaveReceiver)
                        {
                            grid.connect((IRadioWaveReceiver) tile);
                        }
                        //sender & receiver can be the same tile
                        if (tile instanceof IRadioWaveSender)
                        {
                            grid.connect((IRadioWaveSender) tile);
                        }
                    }
                    grid.connect(this, null);
                }
                else
                {
                    //If connections go missing, remove them from grid
                    for (TileEntity tile : oldConnections.values())
                    {
                        if (!connections.values().contains(tile))
                        {
                            if (tile instanceof IRadioWaveReceiver)
                            {
                                grid.disconnect((IRadioWaveReceiver) tile);
                            }
                            //sender & receiver can be the same tile
                            if (tile instanceof IRadioWaveSender)
                            {
                                grid.disconnect((IRadioWaveSender) tile);
                            }
                        }
                    }
                    //If connections change, add them to grid
                    for (TileEntity tile : connections.values())
                    {
                        if (!oldConnections.values().contains(tile))
                        {
                            if (tile instanceof IRadioWaveReceiver)
                            {
                                grid.connect((IRadioWaveReceiver) tile);
                            }
                            //sender & receiver can be the same tile
                            if (tile instanceof IRadioWaveSender)
                            {
                                grid.connect((IRadioWaveSender) tile);
                            }
                        }
                    }
                    //If disconnecting previous connections causes the grid to collapse
                    //Remote this object from the grid
                    if (!grid.overlaps(new Pos(this)))
                    {
                        grid.disconnect(this);
                        //Don't remove connections, they may be used by other tiles
                    }
                }
            }
            //If not we need to drop connection
            else if (grid != null)
            {
                grid.disconnect(this);
                grid = null;
            }
        }
    }

    @Override
    protected boolean onPlayerRightClick(EntityPlayer player, int side, Pos hit)
    {
        if (player.getHeldItem() != null)
        {
            if (Engine.runningAsDev && player.getHeldItem().getItem() == Items.stick)
            {
                if (isServer())
                {
                    player.addChatComponentMessage(new ChatComponentText("WirelessGrid = " + grid));
                }
                return true;
            }
        }
        if (isServer())
        {
            openGui(player, ICBM.INSTANCE);
        }
        return true;
    }

    @Override
    public String link(Location loc, short code)
    {
        //Validate location data
        if (loc.world != world())
        {
            return "link.error.world.match";
        }

        Pos pos = loc.toPos();
        if (!pos.isAboveBedrock())
        {
            return "link.error.pos.invalid";
        }
        if (distance(pos) > TileController.MAX_LINK_DISTANCE)
        {
            return "link.error.pos.distance.max";
        }

        //Compare tile pass code
        TileEntity tile = pos.getTileEntity(loc.world());
        if (!(tile instanceof TileSiloConnector))
        {
            return "link.error.tile.invalid";
        }
        if (((IPassCode) tile).getCode() != code)
        {
            return "link.error.code.match";
        }

        //Add location
        if (!siloConnectors.containsKey(pos))
        {
            siloConnectors.put(loc.toPos(), (TileSiloConnector) tile);
            if (tile instanceof ILinkFeedback)
            {
                ((ILinkFeedback) tile).onLinked(toLocation());
            }
            return "";
        }
        else
        {
            return "link.error.tile.already.added";
        }
    }

    @Override
    public void onPostInit()
    {

    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player)
    {
        return new ContainerCommandController(player, this);
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player)
    {
        return null;
    }

    @Override
    public void onCoverageAreaChanged(WirelessNetwork network)
    {
        //TODO ensure this is not called often
        if (!grid.overlaps(new Pos(this)))
        {
            grid.disconnect(this);
        }
    }

    @Override
    public void resetNetworkCache()
    {

    }

    @Override
    public void receiveExternalRadioWave(float hz, IRadioWaveSender sender, IRadioWaveReceiver receiver, String messageHeader, Object[] data)
    {
        //TODO add support for remote detonator
    }

    @Override
    public void onRangeChange(IRadioWaveReceiver receiver, Cube range)
    {
        if (!grid.overlaps(new Pos(this)))
        {
            grid.disconnect(receiver);
        }
    }

    @Override
    public Map<Integer, ItemStack> getInventoryMap()
    {
        //TODO add battery slots
        return null;
    }
}
