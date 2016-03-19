package com.builtbroken.icbm.content.fof;

import com.builtbroken.icbm.client.Assets;
import com.builtbroken.icbm.content.fof.gui.GuiFoF;
import com.builtbroken.icbm.content.fof.gui.GuiSettings;
import com.builtbroken.mc.api.items.ISimpleItemRenderer;
import com.builtbroken.mc.core.network.packet.PacketTile;
import com.builtbroken.mc.core.network.packet.PacketType;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.prefab.tile.Tile;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

/**
 * Client side implementation for FoF system
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/9/2016.
 */
public class TileFoFClient extends TileFoF implements ISimpleItemRenderer
{
    public boolean hasProfile = false;
    public AxisAlignedBB renderBounds;

    protected void sendFoFIDChange(String change, boolean archive)
    {
        sendPacket(new PacketTile(this, 2, change != null ? change : "", archive));
    }

    protected void sendEnablePermissions(boolean b)
    {
        sendPacket(new PacketTile(this, 3, b));
    }

    @Override
    public boolean read(ByteBuf buf, int id, EntityPlayer player, PacketType type)
    {
        if (!super.read(buf, id, player, type))
        {
            if (isClient())
            {
                GuiScreen screen = Minecraft.getMinecraft().currentScreen;
                if (screen instanceof GuiFoF)
                {
                    GuiFoF gui = (GuiFoF) screen;
                    if (id == 1)
                    {
                        gui.message = ByteBufUtils.readUTF8String(buf);
                        if (gui.message == null)
                        {
                            gui.message = "";
                        }
                        return true;
                    }
                    else if (id == 3)
                    {
                        gui.updateFoFIDField(ByteBufUtils.readUTF8String(buf));
                        return true;
                    }
                }
                else if (screen instanceof GuiSettings)
                {
                    GuiSettings gui = (GuiSettings) screen;
                    if (id == 1)
                    {
                        String message = ByteBufUtils.readUTF8String(buf);
                        String s = message.contains("[") ? message.substring(message.indexOf("["), message.indexOf("]") + 1) : null;
                        int pos = 0;
                        String renderString = message;
                        if (s != null)
                        {
                            renderString = renderString.replace(s, "");
                            s = s.substring(1, 2);
                            try
                            {
                                pos = Integer.parseInt(s);
                            }
                            catch (NumberFormatException e)
                            {

                            }
                        }
                        gui.setMessage(renderString);
                        gui.pos = pos;
                        if (pos == 1 && message.contains("confirm"))
                        {
                            gui.initGui();
                        }
                        return true;
                    }
                }
            }
            return false;
        }
        return true;
    }

    @Override
    public void readDescPacket(ByteBuf buf)
    {
        super.readDescPacket(buf);
        this.hasProfile = buf.readBoolean();
    }

    @Override
    public IIcon getIcon()
    {
        return Blocks.iron_block.getIcon(0, 0);
    }

    @Override
    public void renderDynamic(Pos pos, float frame, int pass)
    {
        //Render launcher
        GL11.glPushMatrix();
        GL11.glTranslatef(pos.xf() + 0.5f, pos.yf() + 0.5f, pos.zf() + 0.5f);
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
        Assets.FoF_STATION_MODEL.renderAll();
        GL11.glPopMatrix();
    }

    @Override
    public void renderInventoryItem(IItemRenderer.ItemRenderType type, ItemStack itemStack, Object... data)
    {
        GL11.glTranslatef(-0.5f, -0.5f, -0.5f);
        GL11.glScaled(.8f, .8f, .8f);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.WEAPON_CASE_TEXTURE);
        Assets.WEAPON_CASE_MODEL.renderAll();
    }

    @Override
    public Tile newTile()
    {
        return new TileFoFClient();
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player)
    {
        return new GuiFoF(this, player);
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox()
    {
        if (renderBounds == null)
        {
            renderBounds = AxisAlignedBB.getBoundingBox(x(), y(), z(), x() + 1, y() + 2, z() + 1);
        }
        return renderBounds;
    }
}
