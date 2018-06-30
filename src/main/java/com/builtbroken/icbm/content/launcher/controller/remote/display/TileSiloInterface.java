package com.builtbroken.icbm.content.launcher.controller.remote.display;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.api.ICBM_API;
import com.builtbroken.icbm.api.controller.ISiloConnectionData;
import com.builtbroken.icbm.api.launcher.ILauncher;
import com.builtbroken.icbm.content.launcher.controller.SiloConnectionData;
import com.builtbroken.icbm.content.launcher.controller.local.TileLocalController;
import com.builtbroken.icbm.content.launcher.controller.remote.central.TileCommandController;
import com.builtbroken.icbm.content.launcher.controller.remote.connector.TileCommandSiloConnector;
import com.builtbroken.mc.api.items.tools.IWorldPosItem;
import com.builtbroken.mc.api.map.radio.wireless.IWirelessNetwork;
import com.builtbroken.mc.api.map.radio.wireless.IWirelessNetworkObject;
import com.builtbroken.mc.api.tile.ILinkFeedback;
import com.builtbroken.mc.api.tile.ILinkable;
import com.builtbroken.mc.api.tile.IPassCode;
import com.builtbroken.mc.api.tile.access.IGuiTile;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.network.IPacketIDReceiver;
import com.builtbroken.mc.core.network.packet.PacketTile;
import com.builtbroken.mc.core.network.packet.PacketType;
import com.builtbroken.mc.core.registry.implement.IRecipeContainer;
import com.builtbroken.mc.imp.transform.vector.Location;
import com.builtbroken.mc.imp.transform.vector.Pos;
import com.builtbroken.mc.lib.helper.LanguageUtility;
import com.builtbroken.mc.lib.helper.recipe.OreNames;
import com.builtbroken.mc.lib.helper.recipe.UniversalRecipe;
import com.builtbroken.mc.prefab.gui.ContainerDummy;
import com.builtbroken.mc.prefab.tile.Tile;
import com.builtbroken.mc.prefab.tile.TileMachine;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Used to get info and send commands to silos
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/26/2016.
 */
public class TileSiloInterface extends TileMachine implements ILinkable, IGuiTile, IPacketIDReceiver, IRecipeContainer
{
    private Pos commandCenterPos;
    private TileCommandController commandCenter;

    public TileSiloInterface()
    {
        super("commandSiloDisplay", Material.iron);
        this.renderNormalBlock = false;
        this.renderTileEntity = true;
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
        if (loc.world != oldWorld())
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
        TileEntity tile = pos.getTileEntity(loc.oldWorld());
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
        return null;
    }

    @Override
    public boolean openGui(EntityPlayer player, Object currentGui, Object... data)
    {
        player.openGui(ICBM.INSTANCE, 0, oldWorld(), xi(), yi(), zi());
        return true;
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
        if (commandCenter != null && commandCenter.isInvalid())
        {
            commandCenter = null;
        }
        if (commandCenter == null && commandCenterPos != null)
        {
            if (oldWorld().blockExists(commandCenterPos.xi(), commandCenterPos.yi(), commandCenterPos.zi()))
            {
                TileEntity tile = commandCenterPos.getTileEntity(oldWorld());
                if (tile instanceof TileCommandController)
                {
                    commandCenter = (TileCommandController) tile;
                }
                else
                {
                    commandCenterPos = null;
                }
            }
        }
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
            else if (id == 2)
            {
                Pos pos = new Pos(buf);
                SiloConnectionData data = new SiloConnectionData(buf);
                openSiloGui(pos, data, player);
                return true;
            }
            else if (id == 3)
            {
                Pos pos = new Pos(buf);
                SiloConnectionData data = new SiloConnectionData(buf);
                fireSilo(pos, data, player);
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
        //TODO redo this to only send selective data for the page currently being viewed
        if (player instanceof EntityPlayerMP)
        {
            //Get out controller
            TileCommandController commandCenter = getCommandCenter();
            //Generate packet
            PacketTile packet = new PacketTile(this, 2, commandCenter != null);

            //if no controller -> no connection -> no data
            if (commandCenter != null)
            {
                List<TileCommandController> controllers = new ArrayList();

                //Add our command center
                if (commandCenter != null)
                {
                    controllers.add(commandCenter);
                }

                //Find an add networked command centers
                for (IWirelessNetwork network : commandCenter.getAttachedNetworks())
                {
                    for (IWirelessNetworkObject object : network.getAttachedObjects())
                    {
                        if (object instanceof TileCommandController && !controllers.contains(object) && !((TileCommandController) object).isInvalid())
                        {
                            controllers.add((TileCommandController) object);
                        }
                    }
                }

                //Write data
                packet.add(controllers.size());
                for (TileCommandController controller : controllers)
                {
                    writeConnectorSet(controller, packet);
                }
            }
            Engine.packetHandler.sendToPlayer(packet, (EntityPlayerMP) player);
        }
    }

    protected void writeConnectorSet(TileCommandController controller, PacketTile packet)
    {
        packet.add(controller.getControllerDisplayName() == null ? "--" : controller.getControllerDisplayName());
        packet.add(controller.siloConnectors != null ? controller.siloConnectors.entrySet().size() : 0);
        for (Map.Entry<Pos, TileCommandSiloConnector> entry : controller.siloConnectors.entrySet())
        {
            writeCommandSiloConnector(entry, packet);
        }
    }

    protected void writeCommandSiloConnector(Map.Entry<Pos, TileCommandSiloConnector> entry, PacketTile packet)
    {
        //Write location data so it can be pulled
        packet.add(entry.getKey().xi());
        packet.add(entry.getKey().yi());
        packet.add(entry.getKey().zi());
        if (entry.getValue() != null)
        {
            List<ISiloConnectionData> list = entry.getValue().getSiloConnectionData();
            if (list != null && list.size() > 0)
            {
                //Write size of data list
                packet.add(list.size());

                //Convert data to NBT as this is the easiest way to load it
                NBTTagCompound save = new NBTTagCompound();
                NBTTagList tagList = new NBTTagList();
                for (ISiloConnectionData siloData : list)
                {
                    tagList.appendTag(siloData.save(new NBTTagCompound()));
                }
                save.setTag("data", tagList);
                packet.add(save);
            }
            else
            {
                packet.add(0); //Empty connection
            }
        }
        else
        {
            packet.add(-1); //No connection
        }
    }

    public void openSiloGui(Pos pos, ISiloConnectionData iSiloConnectionData, EntityPlayer player)
    {
        TileCommandController commandCenter = getCommandCenter();
        if (commandCenter != null)
        {
            if (commandCenter.siloConnectors.containsKey(pos))
            {
                TileCommandSiloConnector controller = commandCenter.siloConnectors.get(pos);
                if (controller != null && controller.getSiloConnectionData() != null && controller.getSiloConnectionData().contains(iSiloConnectionData))
                {
                    if (iSiloConnectionData.hasSettingsGui())
                    {
                        iSiloConnectionData.openGui(player, this, controller);
                    }
                    else
                    {
                        player.addChatComponentMessage(new ChatComponentText(LanguageUtility.getLocal("info.voltzengine:tile.error.gui.none")));
                    }
                }
                else
                {
                    player.addChatComponentMessage(new ChatComponentText(LanguageUtility.getLocal("info.voltzengine:tile.error.missing.data")));
                }
            }
        }
        else
        {
            player.addChatComponentMessage(new ChatComponentText(LanguageUtility.getLocal("info.voltzengine:tile.error.missing")));
        }
    }

    /**
     * Called to fire a missile
     *
     * @param pos
     * @param iSiloConnectionData
     * @param player
     */
    public void fireSilo(Pos pos, ISiloConnectionData iSiloConnectionData, EntityPlayer player)
    {
        if (isServer())
        {
            TileCommandController commandCenter = getCommandCenter();
            if (commandCenter != null)
            {
                if (commandCenter.siloConnectors.containsKey(pos))
                {
                    TileCommandSiloConnector controller = commandCenter.siloConnectors.get(pos);
                    if (controller != null && controller.getSiloConnectionData() != null && controller.getSiloConnectionData().contains(iSiloConnectionData))
                    {
                        if (iSiloConnectionData.hasSettingsGui())
                        {
                            ILauncher launcher = iSiloConnectionData.getSilo();
                            if (launcher != null)
                            {
                                if (!launcher.fireMissile())
                                {
                                    //TODO send error if missile can not fire
                                    ICBM.INSTANCE.logger().info("TileSiloInterface: " + player + " attempted to fire a missile from " + iSiloConnectionData);
                                }
                                else
                                {
                                    //TODO confirm missile fired
                                    ICBM.INSTANCE.logger().info("TileSiloInterface: " + player + " fired a missile from " + iSiloConnectionData);
                                }
                            }
                            else
                            {
                                //TODO send error code
                            }
                        }
                        else
                        {
                            //TODO Open Alt GUI or send error
                        }
                    }
                    else
                    {
                        //TODO send error
                    }
                }
            }
            else
            {
                //TODO send error
            }
        }
        else
        {
            sendPacketToServer(new PacketTile(this, 3, pos, iSiloConnectionData));
        }
    }

    @Override
    public void genRecipes(List<IRecipe> recipes)
    {
        recipes.add(newShapedRecipe(ICBM_API.blockCommandSiloDisplay, "GPG", "CNC", "WSW", 'G', Blocks.glass_pane, 'P', OreNames.PLATE_IRON, 'C', UniversalRecipe.CIRCUIT_T2.get(), 'N', OreNames.NUGGET_GOLD, 'S', OreNames.SCREW_IRON, 'W', OreNames.WIRE_COPPER));
    }
}
