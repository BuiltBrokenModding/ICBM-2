package com.builtbroken.icbm.content.launcher.controller.remote.connector;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.api.controller.ISiloConnectionData;
import com.builtbroken.icbm.api.controller.ISiloConnectionPoint;
import com.builtbroken.icbm.api.launcher.ILauncher;
import com.builtbroken.mc.api.items.tools.IWorldPosItem;
import com.builtbroken.mc.api.tile.IGuiTile;
import com.builtbroken.mc.api.tile.ILinkFeedback;
import com.builtbroken.mc.api.tile.ILinkable;
import com.builtbroken.mc.api.tile.IPassCode;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.network.IPacketIDReceiver;
import com.builtbroken.mc.core.network.packet.PacketTile;
import com.builtbroken.mc.core.network.packet.PacketType;
import com.builtbroken.mc.core.registry.implement.IRecipeContainer;
import com.builtbroken.mc.lib.helper.recipe.OreNames;
import com.builtbroken.mc.lib.helper.recipe.UniversalRecipe;
import com.builtbroken.mc.lib.transform.vector.Location;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.prefab.gui.ContainerDummy;
import com.builtbroken.mc.prefab.tile.Tile;
import com.builtbroken.mc.prefab.tile.TileModuleMachine;
import com.builtbroken.mc.prefab.tile.module.TileModuleInventory;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Works like the TileController linking silos together into a control group. This group is then broadcasted back to the uplink tile.
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/26/2016.
 */
public class TileCommandSiloConnector extends TileModuleMachine implements ILinkable, ISiloConnectionPoint, IPacketIDReceiver, IGuiTile, IRecipeContainer
{
    public static final int MAX_CONNECTIONS = 20;
    /** Main texture */
    public static IIcon texture;
    /** Top icon */
    public static IIcon top_texture;
    /** Max number of silos that can be linked, limit is to improve Gui handling */
    public static double MAX_LINK_DISTANCE = 20;

    /** List of all connected data */
    protected List<ISiloConnectionData> siloData = new ArrayList();
    /** Map of positions to connected data, mainly used by link code */
    protected HashMap<Pos, ISiloConnectionData> posToData = new HashMap();

    /** Name displayed in GUI */
    protected String connectorDisplayName;
    /** Group name used to fire missiles from remote detonators */
    protected String connectorGroupName;

    public TileCommandSiloConnector()
    {
        super("siloWirelessDataPoint", Material.iron);
        this.hardness = 10f;
        this.resistance = 10f;
    }

    @Override
    protected IInventory createInventory()
    {
        return new TileModuleInventory(this, 2);
    }

    @Override
    public IIcon getIcon(int side)
    {
        if (side == 1)
        {
            return top_texture;
        }
        return texture;
    }

    @Override
    public void registerIcons(IIconRegister iconRegister)
    {
        texture = iconRegister.registerIcon(ICBM.PREFIX + "commandSiloConnector");
        top_texture = iconRegister.registerIcon(ICBM.PREFIX + "commandSiloConnector_top");
    }

    @Override
    public Tile newTile()
    {
        return new TileCommandSiloConnector();
    }

    @Override
    public String getInventoryName()
    {
        return "tile.icbm:smallSiloController.container.name";
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
            if (index >= 0 && index < siloData.size())
            {
                ISiloConnectionData data = siloData.get(index);
                ILauncher launcher = data.getSilo();
                if (launcher != null)
                {
                    launcher.fireMissile();
                }
            }
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
        TileEntity tile = pos.getTileEntity(loc.world());
        if (!(tile instanceof ILauncher))
        {
            return "link.error.tile.invalid";
        }
        if (tile instanceof IPassCode && ((IPassCode) tile).getCode() != code)
        {
            return "link.error.code.match";
        }

        //Add location
        if (!posToData.containsKey(pos))
        {
            ISiloConnectionData data = new SiloConnectionData((ILauncher) tile);
            if (!siloData.contains(data))
            {
                if (siloData.size() < MAX_CONNECTIONS)
                {
                    siloData.add(data);
                    posToData.put(pos, data);
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
                data = posToData.get(pos);
                siloData.add(data);
                posToData.put(pos, data);
                ((ILinkFeedback) tile).onLinked(toLocation());
                return "link.updated";
            }
        }
        else
        {
            ISiloConnectionData data = posToData.get(pos);
            siloData.remove(data);
            posToData.remove(pos);
            return "link.removed";
        }
    }

    @Override
    public List<ISiloConnectionData> getSiloConnectionData()
    {
        return siloData;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        if (nbt.hasKey("siloData"))
        {
            siloData.clear();
            posToData.clear();
            NBTTagList list = nbt.getTagList("siloData", 10);
            for (int i = 0; i < list.tagCount(); i++)
            {
                ISiloConnectionData data = new SiloConnectionData(list.getCompoundTagAt(i));
                siloData.add(data);
                posToData.put(new Pos(data.x(), data.y(), data.z()), data);
            }
        }
        if (nbt.hasKey("displayName"))
        {
            connectorDisplayName = nbt.getString("displayName");
        }
        if (nbt.hasKey("groupName"))
        {
            connectorGroupName = nbt.getString("groupName");
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        if (siloData.size() > 0)
        {
            NBTTagList list = new NBTTagList();
            for (ISiloConnectionData data : siloData)
            {
                list.appendTag(data.save(new NBTTagCompound()));
            }
            nbt.setTag("siloData", list);
        }
        if (getConnectorDisplayName() != null && !getConnectorDisplayName().isEmpty())
        {
            nbt.setString("displayName", getConnectorDisplayName());
        }
        if (getConnectorGroupName() != null && !getConnectorGroupName().isEmpty())
        {
            nbt.setString("groupName", getConnectorGroupName());
        }
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
                    player.addChatComponentMessage(new ChatComponentText("Silos -> " + siloData.size()));
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

    public String getConnectorDisplayName()
    {
        return connectorDisplayName;
    }

    /**
     * Sets the controller's display name, only used in GUIs. Maybe waila later...
     *
     * @param connectorDisplayName
     */
    public void setConnectorDisplayName(String connectorDisplayName)
    {
        this.connectorDisplayName = connectorDisplayName;
        if (isClient())
        {
            sendPacketToServer(new PacketTile(this, 2, connectorDisplayName != null ? connectorDisplayName : ""));
        }
    }

    @Override
    public String getConnectorGroupName()
    {
        return connectorGroupName;
    }

    /**
     * Sets the tile group name, used to link and access
     * controller's remotely.
     *
     * @param connectorGroupName
     */
    public void setConnectorGroupName(String connectorGroupName)
    {
        this.connectorGroupName = connectorGroupName;
        if (isClient())
        {
            sendPacketToServer(new PacketTile(this, 3, connectorGroupName != null ? connectorGroupName : ""));
        }
    }

    @Override
    public boolean read(ByteBuf buf, int id, EntityPlayer player, PacketType type)
    {
        if (!super.read(buf, id, player, type))
        {
            if (isServer())
            {
                if (id == 2)
                {
                    setConnectorDisplayName(ByteBufUtils.readUTF8String(buf));
                    return true;
                }
                else if (id == 3)
                {
                    setConnectorGroupName(ByteBufUtils.readUTF8String(buf));
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player)
    {
        return new ContainerDummy(player, this);
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player)
    {
        return new GuiCommandSiloConnector(player, this);
    }

    @Override
    public void readDescPacket(ByteBuf buf)
    {
        super.readDescPacket(buf);
        connectorDisplayName = ByteBufUtils.readUTF8String(buf);
        connectorGroupName = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void writeDescPacket(ByteBuf buf)
    {
        super.writeDescPacket(buf);
        ByteBufUtils.writeUTF8String(buf, getConnectorDisplayName() == null ? "" : getConnectorDisplayName());
        ByteBufUtils.writeUTF8String(buf, getConnectorGroupName() == null ? "" : getConnectorGroupName());
    }

    @Override
    public void genRecipes(List<IRecipe> recipes)
    {
        recipes.add(newShapedRecipe(ICBM.blockCommandSiloConnector, "WCW", "RCR", "PCP", 'C', UniversalRecipe.CIRCUIT_T2.get(), 'W', OreNames.WIRE_IRON, 'P', OreNames.PLATE_IRON, 'R', Items.repeater));
    }
}
