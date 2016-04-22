package com.builtbroken.icbm.content.launcher.controller.remote.central;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.content.launcher.controller.local.TileController;
import com.builtbroken.icbm.content.launcher.controller.remote.connector.TileSiloConnector;
import com.builtbroken.mc.api.map.radio.wireless.*;
import com.builtbroken.mc.api.tile.IGuiTile;
import com.builtbroken.mc.api.tile.ILinkFeedback;
import com.builtbroken.mc.api.tile.ILinkable;
import com.builtbroken.mc.api.tile.IPassCode;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.network.IPacketIDReceiver;
import com.builtbroken.mc.core.registry.implement.IPostInit;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Centralized point for link all objects into a central wireless grid.
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/26/2016.
 */
public class TileCommandController extends TileModuleMachine implements ILinkable, IPacketIDReceiver, IGuiTile, IPostInit, IPrefabInventory, IWirelessDataPoint, IWirelessDataListener
{
    /** List of linked silo connectors */
    protected final HashMap<Pos, TileSiloConnector> siloConnectors = new HashMap();

    /** List of networks connected to this antenna */
    protected List<IWirelessNetwork> connectedNetworks = new ArrayList();

    protected short siloDataPassKey = 0;

    public TileCommandController()
    {
        super("remoteController", Material.iron);
        this.hardness = 10f;
        this.resistance = 10f;
    }

    @Override
    public void update()
    {
        super.update();
        if (isServer() && ticks % 60 == 0) //every 3 seconds
        {
            //TODO build list of silo connections
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
                    if (!player.isSneaking())
                    {
                        player.addChatComponentMessage(new ChatComponentText("WirelessNetworks = " + connectedNetworks));
                    }
                    else
                    {
                        int i = 0;
                        for (IWirelessNetwork network : getAttachedNetworks())
                        {
                            player.addChatComponentMessage(new ChatComponentText("Network[" + (i++) + "] = " + network));
                        }
                    }
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
        return new GuiCommandController(player, this);
    }

    @Override
    public Map<Integer, ItemStack> getInventoryMap()
    {
        //TODO add battery slots
        return null;
    }

    @Override
    public void onConnectionAdded(IWirelessNetworkObject object)
    {
        //TODO keep track of other command controllers
    }

    @Override
    public void onConnectionRemoved(IWirelessNetworkObject object, ConnectionRemovedReason reason)
    {
        //TODO keep track of other command controllers
    }

    @Override
    public boolean hasAccessForData(String dataName, short passKey)
    {
        if ("silos".equals(dataName) && passKey == siloDataPassKey)
        {
            return true;
        }
        return false;
    }

    @Override
    public boolean addWirelessNetwork(IWirelessNetwork network)
    {
        if (!connectedNetworks.contains(network))
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
        return true;
    }

    @Override
    public List<IWirelessNetwork> getAttachedNetworks()
    {
        return connectedNetworks;
    }

    @Override
    public boolean canAcceptAntennaConnection(ForgeDirection side)
    {
        //TODO consider attaching to antenna's on only one side, rotation based
        return true;
    }
}
