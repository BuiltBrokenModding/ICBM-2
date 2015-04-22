package com.builtbroken.icbm.content.launcher.controller;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.content.launcher.TileAbstractLauncher;
import com.builtbroken.mc.api.ISave;
import com.builtbroken.mc.api.items.IWorldPosItem;
import com.builtbroken.mc.api.tile.IGuiTile;
import com.builtbroken.mc.api.tile.ILinkFeedback;
import com.builtbroken.mc.api.tile.ILinkable;
import com.builtbroken.mc.api.tile.IPassCode;
import com.builtbroken.mc.api.tile.node.ITileModule;
import com.builtbroken.mc.core.network.IPacketIDReceiver;
import com.builtbroken.mc.core.network.packet.AbstractPacket;
import com.builtbroken.mc.core.network.packet.PacketTile;
import com.builtbroken.mc.core.network.packet.PacketType;
import com.builtbroken.mc.lib.transform.vector.Location;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.prefab.gui.ContainerDummy;
import com.builtbroken.mc.prefab.tile.TileModuleMachine;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Used to link several launchers together to be controlled from a single terminal
 * Created by robert on 4/3/2015.
 */
public class TileController extends TileModuleMachine implements ILinkable, IPacketIDReceiver, IGuiTile
{
    public static double MAX_LINK_DISTANCE = 100;
    public static int MAX_LAUNCHER_LINK = 6; //changed to 6 due to current GUI size and no GUI paging

    //TODO auto connect to any launcher next to the controller
    //TODO default to rear connection first
    //TODO add rotation
    //TODO add model
    //TODO add OC support
    //TODO add data cables to restrict linking
    //TODO add upgrade cards (Wireless linking, Link capacity, Guidance, Missile tracking)
    protected List<Pos> launcherLocations = new ArrayList<Pos>();

    //Only used client side at the moment
    protected List<LauncherData> launcherData;

    public TileController()
    {
        super("missileController", Material.iron);
        this.addInventoryModule(2);
    }

    /**
     * Called to fire the launcher in the list of linked launchers
     *
     * @param index - # in the launcher list
     */
    protected void fireLauncher(int index)
    {
        if (isServer())
        {
            if (index >= 0 && index < launcherLocations.size())
            {
                Pos pos = launcherLocations.get(index);
                TileEntity tile = pos.getTileEntity(world());
                if (tile instanceof TileAbstractLauncher)
                {
                    ((TileAbstractLauncher) tile).fireMissile();
                }
            }
        }
        else
        {
            sendPacketToServer(new PacketTile(this, 2, index));
        }
    }

    /**
     * Loops threw the list of launchers and fires each one
     */
    protected void fireAllLaunchers()
    {
        if (isServer())
        {
            for (int i = 0; i < launcherLocations.size(); i++)
            {
                fireLauncher(i);
            }
        }
        else
        {
            sendPacketToServer(new PacketTile(this, 2, -1));
        }
    }

    @Override
    public String link(Location pos, short code)
    {
        //Validate location data
        if (pos.world != world())
            return "link.error.world.match";
        if (!pos.isAboveBedrock())
            return "link.error.pos.invalid";
        if (distance(pos) > MAX_LINK_DISTANCE)
            return "link.error.pos.distance";

        //Compare tile pass code
        TileEntity tile = pos.getTileEntity();
        if (!(tile instanceof TileAbstractLauncher))
            return "link.error.tile.invalid";
        if (((IPassCode) tile).getCode() != code)
            return "link.error.code.match";

        //Add location
        if (!launcherLocations.contains(pos))
        {
            if (launcherLocations.size() < MAX_LAUNCHER_LINK)
            {
                launcherLocations.add(pos.toVector3());
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
    public void onNeighborChanged(Pos pos)
    {
        super.onNeighborChanged(pos);
        if (!launcherLocations.contains(pos))
        {
            TileEntity tile = pos.getTileEntity(world());
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
            launcherLocations.remove(pos);
        else
            sendPacketToServer(new PacketTile(this, 1, pos));
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
            TileEntity tile = pos.getTileEntity(world());
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

                if(Minecraft.getMinecraft().currentScreen instanceof GuiController)
                {
                    Minecraft.getMinecraft().currentScreen.initGui();
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
                    fireAllLaunchers();
                else
                    fireLauncher(index);
                return true;
            }
        }
        return false;
    }

    @Override
    public void doUpdateGuiUsers()
    {
        //Only send packets to the GUI if we have data to send
        if (launcherLocations.size() > 0)
        {
            PacketTile packet;
            NBTTagCompound nbt = new NBTTagCompound();
            NBTTagList list = new NBTTagList();

            //Construct launcher data structure
            for (TileAbstractLauncher launcher : getLaunchers())
            {
                list.appendTag(new LauncherData(launcher).toNBT());
            }
            nbt.setTag("launcherData", list);

            //Create and send packet
            packet = new PacketTile(this, 0, nbt);
            sendPacketToGuiUsers(packet);
        }
    }

    @Override
    public AbstractPacket getDescPacket()
    {
        //No data is needed on load
        return null;
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player)
    {
        return new ContainerDummy(player, this);
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player)
    {
        return new GuiController(this, player);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        if(nbt.hasKey("locations"))
        {
            launcherLocations.clear();
            NBTTagList list = nbt.getTagList("locations", 10);
            for(int i =0; i < list.tagCount(); i++)
            {
                NBTTagCompound tag = list.getCompoundTagAt(i);
                launcherLocations.add(new Pos(tag.getInteger("x"), tag.getInteger("xy"), tag.getInteger("z")));
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        if(launcherLocations != null && launcherLocations.size() > 0)
        {
            NBTTagList list = new NBTTagList();
            for(Pos pos : launcherLocations)
                list.appendTag(pos.toIntNBT());
            nbt.setTag("locations", list);
        }
    }

    @Override
    protected boolean onPlayerRightClick(EntityPlayer player, int side, Pos hit)
    {
        if(player.getHeldItem() != null && player.getHeldItem().getItem() instanceof IWorldPosItem)
            return false;

        if(isServer())
            openGui(player, ICBM.INSTANCE);
        return true;
    }
}
