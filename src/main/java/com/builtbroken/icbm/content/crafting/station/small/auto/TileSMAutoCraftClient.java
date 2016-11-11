package com.builtbroken.icbm.content.crafting.station.small.auto;

import com.builtbroken.icbm.api.modules.IMissile;
import com.builtbroken.icbm.client.Assets;
import com.builtbroken.icbm.content.crafting.station.small.TileSmallMissileWorkstationClient;
import com.builtbroken.mc.api.items.ISimpleItemRenderer;
import com.builtbroken.mc.core.network.packet.PacketTile;
import com.builtbroken.mc.core.network.packet.PacketType;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.prefab.tile.Tile;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;

/**
 * Extends version of the warhead station tile class that handles all of the client side logic
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/13/2016.
 */
public class TileSMAutoCraftClient extends TileSMAutoCraft implements ISimpleItemRenderer
{
    /** Object used to render the missile in real time */
    private IMissile renderMissile;

    @Override
    public Tile newTile()
    {
        return new TileSMAutoCraftClient();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon()
    {
        return Blocks.iron_block.getIcon(0, 0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderDynamic(Pos pos, float frame, int pass)
    {
        //Render launcher
        GL11.glPushMatrix();
        GL11.glTranslatef(pos.xf() + 0.5f, pos.yf(), pos.zf() + 0.5f);
        GL11.glRotated(90, 0, 1, 0);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.SMALL_WORKSTATION_TEXTURE2);

        if (getDirection() == ForgeDirection.EAST)
        {
            GL11.glRotated(-90, 0, 1, 0);
        }
        else if (getDirection() == ForgeDirection.WEST)
        {
            GL11.glRotated(90, 0, 1, 0);
        }
        else if (getDirection() == ForgeDirection.SOUTH)
        {
            GL11.glRotated(180, 0, 1, 0);
        }
        Assets.CART1x3.renderAll();
        GL11.glPopMatrix();

        //render missile
        if (renderMissile != null)
        {
            GL11.glPushMatrix();
            TileSmallMissileWorkstationClient.renderMissile(pos, renderMissile, ForgeDirection.UP, getDirection());
            GL11.glPopMatrix();
        }
    }

    @Override
    public void renderInventoryItem(IItemRenderer.ItemRenderType type, ItemStack itemStack, Object... data)
    {
        GL11.glTranslatef(0f, 0f, 0f);
        GL11.glRotatef(-20f, 0, 1, 0);
        GL11.glScaled(.7f, .7f, .7f);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.SMALL_WORKSTATION_TEXTURE2);
        Assets.CART1x3.renderAll();
    }

    @Override
    public boolean read(ByteBuf buf, int id, EntityPlayer player, PacketType type)
    {
        super.doUpdateGuiUsers();
        if (!super.read(buf, id, player, type))
        {
            if (id == 5)
            {
                isAutocrafting = buf.readBoolean();
                requiresWarhead = buf.readBoolean();
                requiresGuidance = buf.readBoolean();
                requiresEngine = buf.readBoolean();
                //Reload GUI
                final GuiScreen screen = Minecraft.getMinecraft().currentScreen;
                if (screen instanceof GuiSMAutoCraft)
                {
                    screen.initGui();
                }
                return true;
            }
            return false;
        }
        return true;
    }


    @Override
    public void readDescPacket(ByteBuf buf)
    {
        super.readDescPacket(buf);
        //TODO read missile
    }

    public void sendCraftingPacket()
    {
        sendPacketToServer(new PacketTile(this, 1));
    }

    public void sendGUIDataUpdate()
    {
        sendPacketToServer(new PacketTile(this, 3, isAutocrafting, requiresWarhead, requiresGuidance, requiresEngine));  //TODO
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player)
    {
        return new GuiSMAutoCraft(player, this, ID);
    }
}
