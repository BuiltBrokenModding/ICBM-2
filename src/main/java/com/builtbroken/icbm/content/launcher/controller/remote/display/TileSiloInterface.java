package com.builtbroken.icbm.content.launcher.controller.remote.display;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.api.controller.ISiloConnectionData;
import com.builtbroken.icbm.api.launcher.ILauncher;
import com.builtbroken.icbm.client.Assets;
import com.builtbroken.icbm.content.launcher.controller.local.TileLocalController;
import com.builtbroken.icbm.content.launcher.controller.remote.central.TileCommandController;
import com.builtbroken.icbm.content.launcher.controller.remote.connector.SiloConnectionData;
import com.builtbroken.icbm.content.launcher.controller.remote.connector.TileCommandSiloConnector;
import com.builtbroken.mc.api.items.ISimpleItemRenderer;
import com.builtbroken.mc.api.items.tools.IWorldPosItem;
import com.builtbroken.mc.api.tile.IGuiTile;
import com.builtbroken.mc.api.tile.ILinkFeedback;
import com.builtbroken.mc.api.tile.ILinkable;
import com.builtbroken.mc.api.tile.IPassCode;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.network.IPacketIDReceiver;
import com.builtbroken.mc.core.network.packet.PacketTile;
import com.builtbroken.mc.core.network.packet.PacketType;
import com.builtbroken.mc.lib.transform.region.Cube;
import com.builtbroken.mc.lib.transform.vector.Location;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.prefab.gui.ContainerDummy;
import com.builtbroken.mc.prefab.tile.Tile;
import com.builtbroken.mc.prefab.tile.TileMachine;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

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
public class TileSiloInterface extends TileMachine implements ILinkable, IGuiTile, IPacketIDReceiver, ISimpleItemRenderer
{
    private Pos commandCenterPos;
    private TileCommandController commandCenter;

    /** Silo data cache used in the GUI */
    protected HashMap<Pos, List<ISiloConnectionData>> clientSiloDataCache;
    /** Locations of connected use din GUI */
    protected List<Pos> siloConnectorPositionCache;

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
        if (commandCenter != null && commandCenter.isInvalid())
        {
            commandCenter = null;
        }
        if (commandCenter == null && commandCenterPos != null)
        {
            if (world().blockExists(commandCenterPos.xi(), commandCenterPos.yi(), commandCenterPos.zi()))
            {
                TileEntity tile = commandCenterPos.getTileEntity(world());
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
            else if(id == 3)
            {
                Pos pos = new Pos(buf);
                SiloConnectionData data = new SiloConnectionData(buf);
                fireSilo(pos, data, player);
                return true;
            }
        }
        //From server
        else
        {
            if (id == 2)
            {
                if (clientSiloDataCache == null)
                {
                    clientSiloDataCache = new HashMap();
                }
                if (siloConnectorPositionCache == null)
                {
                    siloConnectorPositionCache = new ArrayList();
                }
                siloConnectorPositionCache.clear();
                clientSiloDataCache.clear();
                if (buf.readBoolean())
                {
                    int locationSize = buf.readInt();
                    for (int i = 0; i < locationSize; i++)
                    {
                        Pos pos = new Pos(buf);
                        int dataSize = buf.readInt();
                        List<ISiloConnectionData> list = new ArrayList();
                        if (dataSize > 0)
                        {
                            NBTTagCompound save = ByteBufUtils.readTag(buf);
                            NBTTagList tagList = save.getTagList("data", 10);
                            for (int s = 0; s < tagList.tagCount(); s++)
                            {
                                //TODO fix loading to use interfaces, as this will not always be a silo connection data object
                                //TODO add sanity checks to either now show or block bad data in GUI
                                list.add(new SiloConnectionData(tagList.getCompoundTagAt(s)));
                            }
                        }
                        else if (dataSize == 0)
                        {
                            //TODO show no connections
                        }
                        else if (dataSize == -1)
                        {
                            //TODO show in GUI connection was lost
                        }
                        siloConnectorPositionCache.add(pos);
                        clientSiloDataCache.put(pos, list);
                    }
                }
                //Refresh GUI
                GuiScreen screen = Minecraft.getMinecraft().currentScreen;
                if (screen instanceof GuiSiloInterface)
                {
                    screen.initGui();
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
            PacketTile packet = new PacketTile(this, 2, commandCenter != null);
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

    public void openSiloGui(Pos pos, ISiloConnectionData iSiloConnectionData, EntityPlayer player)
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
                            iSiloConnectionData.openGui(player, toLocation());
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
            sendPacketToServer(new PacketTile(this, 2, pos, iSiloConnectionData));
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
    public void renderInventoryItem(IItemRenderer.ItemRenderType type, ItemStack itemStack, Object... data)
    {
        if (type == IItemRenderer.ItemRenderType.INVENTORY)
        {
            GL11.glTranslatef(-0.5f, -1.4f, -0.5f);
        }
        else if (type == IItemRenderer.ItemRenderType.EQUIPPED_FIRST_PERSON)
        {
            GL11.glRotatef(150f, 0, 1, 0);
        }
        else if (type == IItemRenderer.ItemRenderType.EQUIPPED)
        {
            GL11.glRotatef(150f, 0, 1, 0);
            GL11.glTranslatef(-0.5f, -0.1f, -0.5f);
        }
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.FoF_STATION_TEXTURE);
        Assets.FoF_STATION_MODEL.renderOnly("Group_006", "Group_007");
    }

    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
        return new Cube(0, 0, 0, 1, 2, 1).add(x(), y(), z()).toAABB();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderDynamic(Pos pos, float frame, int pass)
    {
        //Render launcher
        GL11.glPushMatrix();
        GL11.glTranslatef(pos.xf() + 0.5f, pos.yf() - 0.561f, pos.zf() + 0.5f);
        switch (getDirection())
        {
            case EAST:
                break;
            case WEST:
                GL11.glRotatef(180f, 0, 1f, 0);
                break;
            case SOUTH:
                GL11.glRotatef(-90f, 0, 1f, 0);
                break;
            default:
                GL11.glRotatef(90f, 0, 1f, 0);
                break;
        }
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.FoF_STATION_TEXTURE);
        Assets.FoF_STATION_MODEL.renderOnly("Group_006", "Group_007");
        GL11.glPopMatrix();
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister)
    {

    }

    @Override
    public IIcon getIcon()
    {
        return Blocks.gravel.getIcon(0, 0);
    }
}
