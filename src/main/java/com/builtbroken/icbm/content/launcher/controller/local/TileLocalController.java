package com.builtbroken.icbm.content.launcher.controller.local;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.api.controller.ISiloConnectionData;
import com.builtbroken.icbm.api.controller.ISiloConnectionPoint;
import com.builtbroken.icbm.api.launcher.ILauncher;
import com.builtbroken.icbm.content.launcher.TileAbstractLauncher;
import com.builtbroken.icbm.content.launcher.controller.SiloConnectionData;
import com.builtbroken.mc.api.items.tools.IWorldPosItem;
import com.builtbroken.mc.api.tile.ILinkFeedback;
import com.builtbroken.mc.api.tile.ILinkable;
import com.builtbroken.mc.api.tile.IPassCode;
import com.builtbroken.mc.api.tile.IPlayerUsing;
import com.builtbroken.mc.api.tile.access.IGuiTile;
import com.builtbroken.mc.api.tile.access.IRotation;
import com.builtbroken.mc.framework.block.imp.IActivationListener;
import com.builtbroken.mc.api.tile.node.ITileNode;
import com.builtbroken.mc.api.tile.node.ITileNodeHost;
import com.builtbroken.mc.codegen.annotations.TileWrapped;
import com.builtbroken.mc.core.network.IPacketIDReceiver;
import com.builtbroken.mc.core.network.packet.PacketType;
import com.builtbroken.mc.imp.transform.vector.Location;
import com.builtbroken.mc.imp.transform.vector.Pos;
import com.builtbroken.mc.lib.helper.LanguageUtility;
import com.builtbroken.mc.prefab.gui.ContainerDummy;
import com.builtbroken.mc.prefab.inventory.ExternalInventory;
import com.builtbroken.mc.prefab.tile.logic.TileMachineNode;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Used to link several launchers together to be controlled from a single terminal
 * Created by robert on 4/3/2015.
 */
@TileWrapped(className = "TileWrapperLocalController")
public class TileLocalController extends TileMachineNode implements ILinkable, IPacketIDReceiver, IGuiTile, IActivationListener, IRotation, IPlayerUsing, ISiloConnectionPoint
{
    public static double MAX_LINK_DISTANCE = 100;
    public static int MAX_LAUNCHER_LINK = 6; //changed to 6 due to current GUI size and no GUI paging

    public static final int GUI_PACKET_ID = 0;
    public static final int FIRE_SILO_PACKET_ID = 2;
    public static final int OPEN_SILO_PACKET_ID = 3;
    public static final int UNLINK_PACKET_ID = 4;

    public static final int MAIN_GUI_ID = 1;

    //TODO auto connect to any launcher next to the controller
    //TODO default to rear connection first
    protected final List<Pos> launcherLocations = new ArrayList();

    //Only used client side at the moment
    protected final List<ISiloConnectionData> launcherData = new ArrayList();

    protected final List<EntityPlayer> usersWithGuiOpen = new ArrayList();

    private ForgeDirection rotationCache;

    public TileLocalController()
    {
        super("controller.local", ICBM.DOMAIN);
    }

    @Override
    protected IInventory createInventory()
    {
        return new ExternalInventory(this, 2).setInventoryName("tile.icbm:smallSiloController.container.name");
    }

    @Override
    public void update(long ticks)
    {
        super.update(ticks);
        if (isServer() && ticks % 3 == 0 && getPlayersUsing().size() > 0)
        {
            doUpdateGuiUsers();
        }
    }

    @Override
    public void doCleanupCheck()
    {
        super.doCleanupCheck();
        Iterator<EntityPlayer> it = usersWithGuiOpen.iterator();
        while (it.hasNext())
        {
            EntityPlayer player = it.next();
            if (!(player.openContainer instanceof ContainerDummy) || ((ContainerDummy) player.openContainer).tile != this)
            {
                it.remove();
            }
        }
    }

    /**
     * Called to fire the launcher in the list of linked launchers
     *
     * @param index  - # in the launcher list
     * @param player
     */
    protected void fireLauncher(int index, EntityPlayer player)
    {
        if (isServer())
        {
            if (index >= 0 && index < launcherData.size())
            {
                ISiloConnectionData data = launcherData.get(index);
                if (data != null)
                {
                    ILauncher launcher = data.getSilo();
                    if (launcher != null)
                    {
                        if (!launcher.fireMissile())
                        {
                            //TODO send error if missile can not fire
                            ICBM.INSTANCE.logger().info("TileSiloInterface: " + player + " attempted to fire a missile from " + data);
                        }
                        else
                        {
                            //TODO confirm missile fired
                            ICBM.INSTANCE.logger().info("TileSiloInterface: " + player + " fired a missile from " + data);
                        }
                    }
                }
            }
        }
        else
        {
            sendPacketToServer(getHost().getPacketForData(FIRE_SILO_PACKET_ID, index));
        }
    }

    /**
     * Loops threw the list of launchers and fires each one
     *
     * @param player
     */
    protected void fireAllLaunchers(EntityPlayer player)
    {
        if (isServer())
        {
            for (int i = 0; i < launcherData.size(); i++)
            {
                fireLauncher(i, player);
            }
        }
        else
        {
            sendPacketToServer(getHost().getPacketForData(FIRE_SILO_PACKET_ID, -1));
        }
    }

    public void openSiloGui(int index, EntityPlayer player)
    {
        if (isServer())
        {
            if (index >= 0 && index < launcherData.size())
            {
                ISiloConnectionData data = launcherData.get(index);
                if (data != null)
                {
                    if (data.hasSettingsGui())
                    {
                        data.openGui(player, this, this);
                    }
                    else
                    {
                        player.addChatComponentMessage(new ChatComponentText(LanguageUtility.getLocal("info.voltzengine:tile.error.gui.none")));
                    }
                }
            }
        }
        else
        {
            sendPacketToServer(getHost().getPacketForData(OPEN_SILO_PACKET_ID, index));
        }
    }


    public void unlink(int index, EntityPlayer player)
    {
        if (isServer())
        {
            if (index >= 0 && index < launcherData.size())
            {
                ISiloConnectionData data = launcherData.get(index);
                if (data != null)
                {
                    Pos pos = launcherLocations.get(index);
                    if (pos.xi() == data.xi() && pos.yi() == data.yi() && pos.zi() == data.zi())
                    {
                        launcherLocations.remove(index);
                    }
                    else
                    {
                        //TODO recreate launcher data as index values do not match
                    }
                }
                launcherData.remove(index);
            }
        }
        else
        {
            sendPacketToServer(getHost().getPacketForData(UNLINK_PACKET_ID, index));
        }
    }

    @Override
    public String link(Location loc, short code)
    {
        //Validate location data
        if (loc.world != oldWorld())
        {
            return "link.error.world.match";
        }

        Pos pos = loc.toPos();
        if (!pos.isAboveBedrock())
        {
            return "link.error.pos.invalid";
        }
        if (distance(pos) > MAX_LINK_DISTANCE)
        {
            return "link.error.pos.distance.max";
        }

        //Compare tile pass code
        ITileNode tile = pos.getTileNode(oldWorld());
        if (!(tile instanceof TileAbstractLauncher))
        {
            return "link.error.tile.invalid";
        }
        if (((IPassCode) tile).getCode() != code)
        {
            return "link.error.code.match";
        }

        //Add location
        if (!launcherLocations.contains(pos))
        {
            if (launcherLocations.size() < MAX_LAUNCHER_LINK)
            {
                launcherLocations.add(pos);
                ((ILinkFeedback) tile).onLinked(toLocation());
                return "";
            }
            else
            {
                return "link.error.tile.limit.max";
            }

        }
        else
        {
            return "link.error.tile.already.added";
        }
    }

    @Override
    public boolean read(ByteBuf buf, int id, EntityPlayer player, PacketType type)
    {
        if (!super.read(buf, id, player, type))
        {
            if (isClient())
            {
                //Basic GUI data
                if (id == GUI_PACKET_ID)
                {
                    NBTTagCompound tag = ByteBufUtils.readTag(buf);
                    launcherData.clear();

                    if (tag.hasKey("siloData"))
                    {
                        NBTTagList list = tag.getTagList("siloData", 10);
                        for (int i = 0; i < list.tagCount(); i++)
                        {
                            ISiloConnectionData data = new SiloConnectionData(list.getCompoundTagAt(i));
                            launcherData.add(data);
                        }
                    }

                    if (Minecraft.getMinecraft().currentScreen instanceof GuiLocalController)
                    {
                        ((GuiLocalController) Minecraft.getMinecraft().currentScreen).reloadData();
                    }

                    return true;
                }
            }
            else
            {
                //Fire button
                if (id == FIRE_SILO_PACKET_ID)
                {
                    int index = buf.readInt();
                    if (index == -1)
                    {
                        fireAllLaunchers(player);
                    }
                    else
                    {
                        fireLauncher(index, player);
                    }
                    return true;
                }
                //Open silo GUI
                else if (id == OPEN_SILO_PACKET_ID)
                {
                    int index = buf.readInt();
                    openSiloGui(index, player);
                }
                //Unlink silo
                else if (id == UNLINK_PACKET_ID)
                {
                    int index = buf.readInt();
                    unlink(index, player);
                }
            }
            return false;
        }
        return true;
    }

    public void doUpdateGuiUsers()
    {
        //Only send packets to the GUI if we have data to send
        if (isServer())
        {
            NBTTagCompound nbt = new NBTTagCompound();
            NBTTagList list = new NBTTagList();

            if (getSiloConnectionData().size() > 0)
            {
                //Construct launcher data structure
                for (ISiloConnectionData data : getSiloConnectionData())
                {
                    list.appendTag(data.save(new NBTTagCompound()));
                }
            }
            nbt.setTag("siloData", list);

            //Create and send packet
            sendPacketToGuiUsers(getHost().getPacketForData(GUI_PACKET_ID, nbt));
        }
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player)
    {
        if (ID == MAIN_GUI_ID)
        {
            return new ContainerDummy(player, this);
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player)
    {
        if (ID == MAIN_GUI_ID)
        {
            return new GuiLocalController(this, player);
        }
        return null;
    }

    @Override
    public boolean openGui(EntityPlayer player, Object currentGui, Object... data)
    {
        player.openGui(ICBM.INSTANCE, MAIN_GUI_ID, oldWorld(), xi(), yi(), zi());
        return true;
    }

    @Override
    public void load(NBTTagCompound nbt)
    {
        super.load(nbt);
        if (nbt.hasKey("locations"))
        {
            launcherLocations.clear();
            NBTTagList list = nbt.getTagList("locations", 10);
            for (int i = 0; i < list.tagCount(); i++)
            {
                NBTTagCompound tag = list.getCompoundTagAt(i);
                launcherLocations.add(new Pos(tag.getInteger("x"), tag.getInteger("y"), tag.getInteger("z")));
            }
        }
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbt)
    {
        super.save(nbt);
        if (launcherLocations != null && launcherLocations.size() > 0)
        {
            NBTTagList list = new NBTTagList();
            for (Pos pos : launcherLocations)
            {
                list.appendTag(pos.toIntNBT());
            }
            nbt.setTag("locations", list);
        }
        return nbt;
    }

    @Override
    public boolean onPlayerActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ)
    {
        if (player.getHeldItem() != null && player.getHeldItem().getItem() instanceof IWorldPosItem)
        {
            return false;
        }
        else if (isServer())
        {
            player.openGui(ICBM.INSTANCE, MAIN_GUI_ID, oldWorld(), xi(), yi(), zi());
        }
        return true;
    }

    @Override
    public ForgeDirection getDirection()
    {
        if (rotationCache == null)
        {
            rotationCache = ForgeDirection.getOrientation(getHost().getHostMeta()).getOpposite();
        }
        return rotationCache;
    }

    @Override
    public List<EntityPlayer> getPlayersUsing()
    {
        return usersWithGuiOpen;
    }

    @Override
    public List<ISiloConnectionData> getSiloConnectionData()
    {
        if (isServer() && (launcherData.isEmpty() || launcherData.size() != launcherLocations.size()))
        {
            launcherData.clear();
            for (Pos pos : launcherLocations)
            {
                TileEntity tile = pos.getTileEntity(oldWorld());
                if (tile instanceof ILauncher)
                {
                    launcherData.add(new SiloConnectionData((ILauncher) tile));
                }
                else if (tile instanceof ITileNodeHost && ((ITileNodeHost) tile).getTileNode() instanceof ILauncher)
                {
                    launcherData.add(new SiloConnectionData((ILauncher) ((ITileNodeHost) tile).getTileNode()));
                }
            }
        }
        return launcherData;
    }

    @Override
    public String getConnectorGroupName()
    {
        return "na";
    }
}