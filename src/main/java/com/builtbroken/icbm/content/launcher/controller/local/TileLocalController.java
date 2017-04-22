package com.builtbroken.icbm.content.launcher.controller.local;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.content.launcher.TileAbstractLauncher;
import com.builtbroken.icbm.content.launcher.controller.LauncherData;
import com.builtbroken.mc.api.items.tools.IWorldPosItem;
import com.builtbroken.mc.api.tile.ILinkFeedback;
import com.builtbroken.mc.api.tile.ILinkable;
import com.builtbroken.mc.api.tile.IPassCode;
import com.builtbroken.mc.api.tile.IPlayerUsing;
import com.builtbroken.mc.api.tile.access.IGuiTile;
import com.builtbroken.mc.api.tile.access.IRotation;
import com.builtbroken.mc.api.tile.listeners.IActivationListener;
import com.builtbroken.mc.api.tile.node.ITileNode;
import com.builtbroken.mc.codegen.annotations.TileWrapped;
import com.builtbroken.mc.core.network.IPacketIDReceiver;
import com.builtbroken.mc.core.network.packet.PacketType;
import com.builtbroken.mc.imp.transform.vector.Location;
import com.builtbroken.mc.imp.transform.vector.Pos;
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
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Used to link several launchers together to be controlled from a single terminal
 * Created by robert on 4/3/2015.
 */
@TileWrapped(className = "TileWrapperLocalController")
public class TileLocalController extends TileMachineNode implements ILinkable, IPacketIDReceiver, IGuiTile, IActivationListener, IRotation, IPlayerUsing
{
    public static double MAX_LINK_DISTANCE = 100;
    public static int MAX_LAUNCHER_LINK = 6; //changed to 6 due to current GUI size and no GUI paging

    //TODO auto connect to any launcher next to the controller
    //TODO default to rear connection first
    //TODO add OC support
    protected List<Pos> launcherLocations = new ArrayList<Pos>();

    //Only used client side at the moment
    protected List<LauncherData> launcherData;

    protected List<EntityPlayer> usersWithGuiOpen = new ArrayList();

    private ForgeDirection rotationCache;

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
            if (index >= 0 && index < launcherLocations.size())
            {
                Pos pos = launcherLocations.get(index);
                ITileNode tile = pos.getTileNode(world());
                if (tile instanceof TileAbstractLauncher)
                {
                    if (((TileAbstractLauncher) tile).fireMissile())
                    {
                        ICBM.INSTANCE.logger().info("TileSiloInterface: " + player + " fired a missile from " + tile);
                    }
                    else
                    {
                        ICBM.INSTANCE.logger().info("TileSiloInterface: " + player + " attempted to fire a missile from " + tile);
                    }
                }
            }
        }
        else
        {
            sendPacketToServer(getHost().getPacketForData(2, index));
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
            for (int i = 0; i < launcherLocations.size(); i++)
            {
                fireLauncher(i, player);
            }
        }
        else
        {
            sendPacketToServer(getHost().getPacketForData(2, -1));
        }
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
        if (distance(pos) > MAX_LINK_DISTANCE)
        {
            return "link.error.pos.distance.max";
        }

        //Compare tile pass code
        ITileNode tile = pos.getTileNode(world());
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


    public void onNeighborChanged(Pos pos)
    {
        //super.onNeighborChanged(pos);
        if (!launcherLocations.contains(pos))
        {
            ITileNode tile = pos.getTileNode(world());
            if (tile instanceof TileAbstractLauncher)
            {
                launcherLocations.add(pos);
                ((ILinkFeedback) tile).onLinked(toLocation());
            }
        }
    }

    /**
     * Called to remove the launcher from the list
     *
     * @param pos - location of the launcher
     */
    protected void removeLauncher(Pos pos)
    {
        if (isServer())
        {
            launcherLocations.remove(pos);
        }
        else
        {
            sendPacketToServer(getHost().getPacketForData(1, pos));
        }
    }

    /**
     * Grabs a list of all linked launchers
     *
     * @return ArrayList<TileAbstractLauncher>
     */
    protected List<TileAbstractLauncher> getLaunchers()
    {
        List<TileAbstractLauncher> list = new ArrayList();
        for (Pos pos : launcherLocations)
        {
            ITileNode tile = pos.getTileNode(world());
            if (tile instanceof TileAbstractLauncher)
            {
                list.add((TileAbstractLauncher) tile);
            }
        }
        return list;
    }

    @Override
    public boolean read(ByteBuf buf, int id, EntityPlayer player, PacketType type)
    {
        if (!super.read(buf, id, player, type))
        {
            if (isClient())
            {
                //Basic GUI data
                if (id == 0)
                {
                    NBTTagCompound tag = ByteBufUtils.readTag(buf);
                    launcherData = new ArrayList();

                    if (tag.hasKey("launcherData"))
                    {
                        NBTTagList list = tag.getTagList("launcherData", 10);
                        for (int i = 0; i < list.tagCount(); i++)
                        {
                            launcherData.add(new LauncherData(list.getCompoundTagAt(i)));
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
                //Remove launcher, input from GUI
                if (id == 1)
                {
                    removeLauncher(new Pos(buf));
                    return true;
                }
                else if (id == 2)
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
            }
            return false;
        }
        return true;
    }

    public void doUpdateGuiUsers()
    {
        //Only send packets to the GUI if we have data to send
        if (isServer() && launcherLocations.size() > 0)
        {
            NBTTagCompound nbt = new NBTTagCompound();
            NBTTagList list = new NBTTagList();

            //Construct launcher data structure
            for (TileAbstractLauncher launcher : getLaunchers())
            {
                if (launcher != null && !launcher.isInvalid() && launcher.world() == world())
                {
                    list.appendTag(new LauncherData(launcher).toNBT());
                }
            }
            nbt.setTag("launcherData", list);

            //Create and send packet
            sendPacketToGuiUsers(getHost().getPacketForData(0, nbt));
        }
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player)
    {
        if (ID == 1)
        {
            return new ContainerDummy(player, this);
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player)
    {
        if (ID == 1)
        {
            return new GuiLocalController(this, player);
        }
        return null;
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
            player.openGui(ICBM.INSTANCE, 1, world(), xi(), yi(), zi());
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
}
