package com.builtbroken.icbm.content.launcher.controller.remote.display;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.api.controller.ISiloConnectionData;
import com.builtbroken.icbm.content.launcher.controller.local.TileController;
import com.builtbroken.icbm.content.launcher.controller.remote.central.TileCommandController;
import com.builtbroken.icbm.content.launcher.controller.remote.connector.SiloConnectionData;
import com.builtbroken.icbm.content.launcher.controller.remote.connector.TileCommandSiloConnector;
import com.builtbroken.mc.api.items.tools.IWorldPosItem;
import com.builtbroken.mc.api.tile.IGuiTile;
import com.builtbroken.mc.api.tile.ILinkFeedback;
import com.builtbroken.mc.api.tile.ILinkable;
import com.builtbroken.mc.api.tile.IPassCode;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.network.IPacketIDReceiver;
import com.builtbroken.mc.core.network.packet.PacketTile;
import com.builtbroken.mc.core.network.packet.PacketType;
import com.builtbroken.mc.lib.transform.vector.Location;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.prefab.gui.ContainerDummy;
import com.builtbroken.mc.prefab.tile.Tile;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Used to get info and send commands to silos
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/26/2016.
 */
public class TileSiloInterface extends Tile implements ILinkable, IGuiTile, IPacketIDReceiver
{
    private Pos commandCenterPos;
    private TileCommandController commandCenter;

    /** Silo data cache used in the GUI */
    private HashMap<Pos, List<ISiloConnectionData>> clientSiloDataCache;

    public TileSiloInterface()
    {
        super("commandSiloDisplay", Material.iron);
        this.hardness = 10f;
        this.resistance = 10f;
    }

    @Override
    public Tile newTile()
    {
        return new TileSiloInterface();
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
        if (!(tile instanceof TileCommandController))
        {
            return "link.error.tile.invalid";
        }
        if (tile instanceof IPassCode && ((IPassCode) tile).getCode() != code)
        {
            return "link.error.code.match";
        }

        //Add location
        if (commandCenter == null && !pos.equals(commandCenterPos) || tile != commandCenter)
        {
            commandCenterPos = pos;
            commandCenter = (TileCommandController) tile;
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
    public Object getServerGuiElement(int ID, EntityPlayer player)
    {
        return new ContainerDummy(player, this);
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player)
    {
        return new GuiSiloInterface(player, this);
    }

    @Override
    protected boolean onPlayerRightClick(EntityPlayer player, int side, Pos hit)
    {
        if (player.getHeldItem() != null)
        {
            if (player.getHeldItem().getItem() instanceof IWorldPosItem)
            {
                return false;
            }
            else if (player.getHeldItem().getItem() == Items.stick && Engine.runningAsDev)
            {
                if (isServer())
                {
                    player.addChatComponentMessage(new ChatComponentText("Command Center -> " + commandCenter));
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
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        if (nbt.hasKey("commandCenterPos"))
        {
            commandCenterPos = new Pos(nbt.getCompoundTag("commandCenterPos"));
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        if (commandCenterPos != null)
        {
            nbt.setTag("commandCenterPos", commandCenterPos.toNBT());
        }
    }

    public TileCommandController getCommandCenter()
    {
        return commandCenter;
    }

    @Override
    public boolean read(ByteBuf buf, int id, EntityPlayer player, PacketType type)
    {
        //From client
        if (isServer())
        {
            if (id == 1)
            {
                sendSiloData(player);
                return true;
            }
        }
        //From server
        else
        {
            if (id == 2)
            {
                clientSiloDataCache.clear();
                if (buf.readBoolean())
                {
                    int locationSize = buf.readInt();
                    for(int i = 0; i < locationSize; i++)
                    {
                        Pos pos = new Pos(buf);
                        int dataSize = buf.readInt();
                        List<ISiloConnectionData> list = new ArrayList();
                        if(dataSize > 0)
                        {
                            NBTTagCompound save = ByteBufUtils.readTag(buf);
                            NBTTagList tagList = save.getTagList("data", 10);
                            for(int s = 0; s < tagList.tagCount(); s++)
                            {
                                //TODO fix loading to use interfaces, as this will not always be a silo connection data object
                                //TODO add sanity checks to either now show or block bad data in GUI
                                list.add(new SiloConnectionData(tagList.getCompoundTagAt(s)));
                            }
                        }
                        else if(dataSize == 0)
                        {
                            //TODO show no connections
                        }
                        else if(dataSize == -1)
                        {
                            //TODO show in GUI connection was lost
                        }
                        clientSiloDataCache.put(pos, list);
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Sends silo data to the player
     *
     * @param player
     */
    public void sendSiloData(EntityPlayer player)
    {
        if (player instanceof EntityPlayerMP)
        {
            //TODO redo this to only send selective data for the page currently being viewed
            TileCommandController commandCenter = getCommandCenter();
            PacketTile packet = new PacketTile(this, 2, commandCenter == null);
            if (commandCenter != null)
            {
                packet.data().writeInt(commandCenter.siloConnectors.entrySet().size());
                for (Map.Entry<Pos, TileCommandSiloConnector> entry : commandCenter.siloConnectors.entrySet())
                {
                    entry.getKey().writeByteBuf(packet.data()); //TODO find a way to send the controller name
                    if (entry.getValue() != null)
                    {
                        List<ISiloConnectionData> list = entry.getValue().getSiloConnectionData();
                        if (list != null)
                        {
                            packet.data().writeInt(list.size());
                            NBTTagCompound save = new NBTTagCompound();
                            NBTTagList tagList = new NBTTagList();
                            for (ISiloConnectionData data : list)
                            {
                                tagList.appendTag(data.save(new NBTTagCompound()));
                            }
                            save.setTag("data", tagList);
                            ByteBufUtils.writeTag(packet.data(), save);
                        }
                        else
                        {
                            packet.data().writeInt(0); //Empty connection
                        }
                    }
                    else
                    {
                        packet.data().writeInt(-1); //No connection
                    }
                }
            }
            Engine.instance.packetHandler.sendToPlayer(packet, (EntityPlayerMP) player);
        }
    }

    /**
     * Client side only method to request data
     * from the server.
     */
    public void requestSiloData()
    {
        if (isClient())
        {
            sendPacketToServer(new PacketTile(this, 1));
        }
    }
}
