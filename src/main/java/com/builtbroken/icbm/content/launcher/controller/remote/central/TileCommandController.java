package com.builtbroken.icbm.content.launcher.controller.remote.central;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.api.controller.ISiloConnectionData;
import com.builtbroken.icbm.api.controller.ISiloConnectionPoint;
import com.builtbroken.icbm.content.items.FakeRadioSender;
import com.builtbroken.icbm.content.launcher.controller.local.TileLocalController;
import com.builtbroken.icbm.content.launcher.controller.remote.connector.TileCommandSiloConnector;
import com.builtbroken.jlib.data.vector.IPos3D;
import com.builtbroken.mc.api.items.tools.IWorldPosItem;
import com.builtbroken.mc.api.map.radio.IRadioWaveExternalReceiver;
import com.builtbroken.mc.api.map.radio.IRadioWaveReceiver;
import com.builtbroken.mc.api.map.radio.IRadioWaveSender;
import com.builtbroken.mc.api.map.radio.wireless.*;
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
import com.builtbroken.mc.lib.transform.region.Cube;
import com.builtbroken.mc.lib.transform.vector.Location;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.prefab.inventory.IPrefabInventory;
import com.builtbroken.mc.prefab.tile.Tile;
import com.builtbroken.mc.prefab.tile.TileModuleMachine;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
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
public class TileCommandController extends TileModuleMachine implements ILinkable, IPacketIDReceiver, IGuiTile, IRecipeContainer, IPrefabInventory, IWirelessDataPoint, IWirelessDataListener, ILinkFeedback, IRadioWaveExternalReceiver
{
    /** Main texture */
    public static IIcon texture;
    /** Top icon */
    public static IIcon top_texture;

    /** List of linked silo connectors */
    public final HashMap<Pos, TileCommandSiloConnector> siloConnectors = new HashMap();

    /** List of networks connected to this antenna */
    protected List<IWirelessNetwork> connectedNetworks = new ArrayList();

    protected short siloDataPassKey = 0;
    protected String displayName;

    public TileCommandController()
    {
        super("commandController", Material.iron);
        this.hardness = 10f;
        this.resistance = 10f;
    }

    @Override
    public Tile newTile()
    {
        return new TileCommandController();
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
        texture = iconRegister.registerIcon(ICBM.PREFIX + "commandController");
        top_texture = iconRegister.registerIcon(ICBM.PREFIX + "commandController_top");
    }

    @Override
    public void update()
    {
        super.update();
        if (isServer() && ticks % 60 == 0) //every 3 seconds
        {
            List<Pos> remove = new ArrayList();
            for (Pos pos : siloConnectors.keySet())
            {
                TileCommandSiloConnector controller = siloConnectors.get(pos);
                if (controller == null)
                {
                    if (worldObj.blockExists(pos.xi(), pos.yi(), pos.zi())) //Ensure the chunk is loaded
                    {
                        TileEntity tile = pos.getTileEntity(world());
                        if (tile instanceof TileCommandSiloConnector)
                        {
                            siloConnectors.put(pos, (TileCommandSiloConnector) tile);
                        }
                        else
                        {
                            remove.add(pos); //Its no longer a connector so remove
                        }
                    }
                }
                else if (controller.isInvalid())
                {
                    siloConnectors.put(pos, null); //Keep pos
                }
            }
            //Actually remove positions, prevents concurrent mod
            remove.forEach(siloConnectors::remove);
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
            else if (Engine.runningAsDev)
            {
                 if (player.getHeldItem().getItem() == Items.stick)
                {
                    if (isServer())
                    {
                        if (getAttachedNetworks().size() > 0)
                        {
                            int i = 0;
                            for (IWirelessNetwork network : getAttachedNetworks())
                            {
                                player.addChatComponentMessage(new ChatComponentText("Network[" + (i++) + "] = " + network));
                            }
                        }
                        else
                        {
                            player.addChatComponentMessage(new ChatComponentText("No networks attached"));
                        }
                    }
                    return true;
                }
                else if (player.getHeldItem().getItem() == Items.blaze_rod)
                {
                    if (isServer())
                    {
                        player.addChatComponentMessage(new ChatComponentText("SiloConnector: " + siloConnectors));
                    }
                    return true;
                }
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
        if (distance(pos) > TileLocalController.MAX_LINK_DISTANCE)
        {
            return "link.error.pos.distance.max";
        }

        //Compare tile pass code
        TileEntity tile = pos.getTileEntity(loc.world());
        if (!(tile instanceof TileCommandSiloConnector))
        {
            return "link.error.tile.invalid";
        }
        if (tile instanceof IPassCode && ((IPassCode) tile).getCode() != code)
        {
            return "link.error.code.match";
        }

        //Add location
        if (!siloConnectors.containsKey(pos))
        {
            siloConnectors.put(loc.toPos(), (TileCommandSiloConnector) tile);
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
    public void onLinked(Location location)
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

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        if (nbt.hasKey("siloPosData"))
        {
            siloConnectors.clear();
            NBTTagList list = nbt.getTagList("siloPosData", 10);
            for (int i = 0; i < list.tagCount(); i++)
            {
                siloConnectors.put(new Pos(list.getCompoundTagAt(i)), null);
            }
        }
        if (nbt.hasKey("displayName"))
        {
            displayName = nbt.getString("displayName");
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        if (!siloConnectors.isEmpty())
        {
            NBTTagList list = new NBTTagList();
            for (Pos pos : siloConnectors.keySet())
            {
                list.appendTag(pos.toNBT());
            }
            nbt.setTag("siloPosData", list);
        }
        if (displayName != null && !displayName.isEmpty())
        {
            nbt.setString("displayName", displayName);
        }
    }

    @Override
    public void receiveExternalRadioWave(float hz, IRadioWaveSender sender, IRadioWaveReceiver receiver, String messageHeader, Object[] data)
    {
        try
        {
            if (isServer())
            {
                for (IWirelessNetwork network : getAttachedNetworks())
                {
                    if (Math.abs(hz - network.getHz()) <= 0.001)
                    {
                        sender.onMessageReceived(receiver, hz, messageHeader, data);

                        //Fire single missile in group by name
                        if ("fireMissile1".equals(messageHeader) || "fireMissileAtTarget".equals(messageHeader))
                        {
                            short pass = (short) data[0];
                            String group = (String) data[1];
                            String siloName = (String) data[2];
                            if (group != null && siloName != null)
                            {
                                boolean fired = false;
                                boolean foundGroup = false;
                                boolean siloFound = false;
                                for (ISiloConnectionPoint connector : siloConnectors.values())
                                {
                                    if (group.equals(connector.getConnectorGroupName()))
                                    {
                                        foundGroup = true;
                                        for (ISiloConnectionData launcher : connector.getSiloConnectionData())
                                        {
                                            if (launcher.getSiloName() != null && siloName.equals(launcher.getSiloName()))
                                            {
                                                siloFound = true;
                                                switch (launcher.getSiloStatus())
                                                {
                                                    case OFFLINE:
                                                        if (sender instanceof FakeRadioSender)
                                                        {
                                                            ((FakeRadioSender) sender).player.addChatComponentMessage(new ChatComponentText("Silo[" + siloName + "] is offline and can not receive the launch data!"));
                                                        }
                                                        break;
                                                    case ONLINE:
                                                        if (launcher instanceof IPassCode && ((IPassCode) launcher).getCode() != pass)
                                                        {
                                                            if (sender instanceof FakeRadioSender)
                                                            {
                                                                //TODO add boolean for reply mode, if auth fails it may be some trying to hijack the missile, if mode == false do not reply to invalid data
                                                                ((FakeRadioSender) sender).player.addChatComponentMessage(new ChatComponentText("Silo[" + siloName + "] auth code is not valid!"));
                                                            }
                                                        }
                                                        else if (launcher.getSilo() != null)
                                                        {
                                                            //Cache travel time before firing, as the time is based on the missile.. which is gone after firing
                                                            int travelTime = launcher.getSilo().getTravelTimeTo(launcher.getSilo().getTarget());

                                                            if ("fireMissileAtTarget".equals(messageHeader) && launcher.getSilo().fireMissile((IPos3D) data[3]) || "fireMissile1".equals(messageHeader) && launcher.getSilo().fireMissile())
                                                            {
                                                                fired = true;
                                                                if (sender instanceof FakeRadioSender)
                                                                {
                                                                    ((FakeRadioSender) sender).player.addChatComponentMessage(new ChatComponentText("Silo[" + siloName + "]: Missile fire at target " + launcher.getSilo().getTarget()));
                                                                    if (travelTime > 0)
                                                                    {
                                                                        ((FakeRadioSender) sender).player.addChatComponentMessage(new ChatComponentText("Silo[" + siloName + "]: Eta " + travelTime + " seconds"));
                                                                    }
                                                                }
                                                            }
                                                            else
                                                            {
                                                                if (sender instanceof FakeRadioSender)
                                                                {
                                                                    ((FakeRadioSender) sender).player.addChatComponentMessage(new ChatComponentText("Silo[" + siloName + "]: Missile failed to fire! Check target data!"));
                                                                }
                                                            }
                                                        }
                                                        else if (sender instanceof FakeRadioSender)
                                                        {
                                                            //TODO add boolean for reply mode, if auth fails it may be some trying to hijack the missile, if mode == false do not reply to invalid data
                                                            ((FakeRadioSender) sender).player.addChatComponentMessage(new ChatComponentText("Silo[" + siloName + "] error passing data to silo! Code: Iv33"));
                                                        }
                                                        break;
                                                    case NO_CONNECTION:
                                                        if (sender instanceof FakeRadioSender)
                                                        {
                                                            ((FakeRadioSender) sender).player.addChatComponentMessage(new ChatComponentText("Silo[" + siloName + "] can not be contacted, it may be out of radio range!"));
                                                        }
                                                        break;
                                                }
                                                break;
                                            }
                                        }
                                        break;
                                    }
                                }
                                if (sender instanceof FakeRadioSender)
                                {
                                    if (fired)
                                    {
                                        ((FakeRadioSender) sender).player.addChatComponentMessage(new ChatComponentText("Missing fired, stand clear of target!"));
                                    }
                                    else if (!foundGroup)
                                    {
                                        ((FakeRadioSender) sender).player.addChatComponentMessage(new ChatComponentText("Unknown Group: " + group));
                                    }
                                    else if (!siloFound)
                                    {
                                        ((FakeRadioSender) sender).player.addChatComponentMessage(new ChatComponentText("Unknown SiloName: " + siloName));
                                    }
                                }
                            }
                            else
                            {
                                if (sender instanceof FakeRadioSender)
                                {
                                    ((FakeRadioSender) sender).player.addChatComponentMessage(new ChatComponentText("Error: silo name or group name is invalid! Check encoded data!"));
                                }
                                ICBM.INSTANCE.logger().error(this + "  Received bad message from " + sender + ".\n Header = " + messageHeader + "\n Data = " + data + "\n Error: Group name or silo name is null");
                            }
                        }
                        //Fire first loaded missile in group
                        else if ("fireMissile2".equals(messageHeader))
                        {
                            short pass = (short) data[0];
                            String group = (String) data[1];
                        }
                        else
                        {
                            ICBM.INSTANCE.logger().error(this + "  Received bad message from " + sender + ".\n Header = " + messageHeader + "\n Data = " + data + "\n Error: Group name or silo name is null");
                        }
                    }
                }
            }
        }
        //It may fail due to cast exception...
        catch (Exception e)
        {
            ICBM.INSTANCE.logger().error(this + "  Failed to receive message from " + sender + ".\n Header = " + messageHeader + "\n Data = " + data, e);
        }
    }

    @Override
    public void onRangeChange(IRadioWaveReceiver receiver, Cube range)
    {

    }

    public String getControllerDisplayName()
    {
        return displayName;
    }

    public void setConnectorDisplayName(String name)
    {
        this.displayName = name;
        if (isClient())
        {
            sendPacketToServer(new PacketTile(this, 2, name != null ? name : ""));
        }
    }

    @Override
    public void readDescPacket(ByteBuf buf)
    {
        super.readDescPacket(buf);
        displayName = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void writeDescPacket(ByteBuf buf)
    {
        super.writeDescPacket(buf);
        ByteBufUtils.writeUTF8String(buf, getControllerDisplayName() == null ? "" : getControllerDisplayName());
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
            }
            return false;
        }
        return true;
    }

    @Override
    public void genRecipes(List<IRecipe> recipes)
    {
        recipes.add(newShapedRecipe(ICBM.blockCommandCentral, "WWW", "GPR", "CVC", 'W', OreNames.WIRE_GOLD, 'G', Blocks.glass, 'P', OreNames.PLATE_IRON, 'C', UniversalRecipe.CIRCUIT_T1.get(), 'V', UniversalRecipe.CIRCUIT_T3.get(), 'R', OreNames.DIAMOND));
    }
}
