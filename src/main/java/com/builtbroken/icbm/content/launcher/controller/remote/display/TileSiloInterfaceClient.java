package com.builtbroken.icbm.content.launcher.controller.remote.display;

import com.builtbroken.icbm.api.controller.ISiloConnectionData;
import com.builtbroken.icbm.client.Assets;
import com.builtbroken.icbm.content.launcher.controller.SiloConnectionData;
import com.builtbroken.mc.api.items.ISimpleItemRenderer;
import com.builtbroken.mc.core.network.packet.PacketTile;
import com.builtbroken.mc.core.network.packet.PacketType;
import com.builtbroken.mc.imp.transform.region.Cube;
import com.builtbroken.mc.imp.transform.vector.Pos;
import com.builtbroken.mc.prefab.tile.Tile;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/26/2016.
 */
public class TileSiloInterfaceClient extends TileSiloInterface implements ISimpleItemRenderer
{
    /** Silo data cache used in the GUI */
    protected HashMap<Pos, List<ISiloConnectionData>> clientSiloDataCache;

    /** Locations of connected locations to controller */
    protected String[] controllers;
    protected Pos[][] controllerData;


    @Override
    public Tile newTile()
    {
        return new TileSiloInterfaceClient();
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player)
    {
        return new GuiSiloInterface(player, this);
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

    @Override
    public boolean read(ByteBuf buf, int id, EntityPlayer player, PacketType type)
    {
        if (!super.read(buf, id, player, type))
        {
            if (id == 2)
            {
                //Clear up / init
                if (clientSiloDataCache == null)
                {
                    clientSiloDataCache = new HashMap();
                }

                controllerData = null;
                controllers = null;
                clientSiloDataCache.clear();

                //Call to read
                readConnectorSet(buf);

                //Refresh GUI
                GuiScreen screen = Minecraft.getMinecraft().currentScreen;
                if (screen instanceof GuiSiloInterface)
                {
                    screen.initGui();
                }
                return true;
            }
            return false;
        }
        return true;
    }

    protected void readCommandSiloConnector(int controllerIndexValue, ByteBuf buf)
    {
        int locationSize = buf.readInt();
        Pos[] posData = new Pos[locationSize];
        for (int i = 0; i < locationSize; i++)
        {
            Pos pos = new Pos(buf.readInt(), buf.readInt(), buf.readInt());
            int dataSize = buf.readInt();
            List<ISiloConnectionData> list = new ArrayList();
            if (dataSize > 0)
            {
                NBTTagCompound save = ByteBufUtils.readTag(buf);
                if (save != null)
                {
                    NBTTagList tagList = save.getTagList("data", 10);
                    for (int s = 0; s < tagList.tagCount(); s++)
                    {
                        //TODO fix loading to use interfaces, as this will not always be a silo connection data object
                        //TODO add sanity checks to either now show or block bad data in GUI
                        SiloConnectionData data = new SiloConnectionData(tagList.getCompoundTagAt(s));

                        //TODO find a way around this so cross world support is possible
                        if (data.dim == Minecraft.getMinecraft().theWorld.provider.dimensionId)
                        {
                            data.world = Minecraft.getMinecraft().theWorld;
                            list.add(data);
                        }
                    }
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
            posData[i] = pos;
            clientSiloDataCache.put(pos, list);
        }
        this.controllerData[controllerIndexValue] = posData;
    }

    protected void readConnectorSet(ByteBuf buf)
    {
        //Do we have data
        if (buf.readBoolean())
        {
            //Number of controllers we are about to read
            int controllers = buf.readInt();
            if (controllers > 0)
            {
                this.controllers = new String[controllers];
                this.controllerData = new Pos[controllers][];
                for (int c = 0; c < controllers; c++)
                {
                    String name = ByteBufUtils.readUTF8String(buf);
                    name = (name.isEmpty() || name.equals("--")) ? "c" + c : name;
                    this.controllers[c] = name;
                    readCommandSiloConnector(c, buf);
                }
            }
            else
            {
                //TODO show error saying no connection is provided
            }
        }
    }

    @Override
    public void openSiloGui(Pos pos, ISiloConnectionData iSiloConnectionData, EntityPlayer player)
    {
        if (isServer())
        {
            super.openSiloGui(pos, iSiloConnectionData, player);
        }
        else
        {
            sendPacketToServer(new PacketTile(this, 2, pos, iSiloConnectionData));
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
