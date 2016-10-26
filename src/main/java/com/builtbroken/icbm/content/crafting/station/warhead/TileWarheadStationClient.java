package com.builtbroken.icbm.content.crafting.station.warhead;

import com.builtbroken.mc.api.items.ISimpleItemRenderer;
import com.builtbroken.mc.client.SharedAssets;
import com.builtbroken.mc.core.network.packet.PacketTile;
import com.builtbroken.mc.core.network.packet.PacketType;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.prefab.tile.Tile;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

/**
 * Extends version of the warhead station tile class that handles all of the client side logic
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/13/2016.
 */
public class TileWarheadStationClient extends TileWarheadStation implements ISimpleItemRenderer
{
    @Override
    public Tile newTile()
    {
        return new TileWarheadStationClient();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon()
    {
        return Blocks.iron_block.getIcon(0, 0);
    }

    @Override
    public void readDescPacket(ByteBuf buf)
    {
        super.readDescPacket(buf);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderDynamic(Pos pos, float frame, int pass)
    {
        GL11.glPushMatrix();
        GL11.glTranslatef(pos.xf() + 1, pos.yf(), pos.zf() + 1);
        GL11.glRotated(90, 0, 1, 0);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(SharedAssets.TOOL_TABLE_TEXTURE);
        SharedAssets.TOOL_TABLE.renderAll(); //TODO render warhead on table, remove some tools
        GL11.glPopMatrix();
        //TODO render warhead

        //TODO render explosives
    }

    @Override
    public void renderInventoryItem(IItemRenderer.ItemRenderType type, ItemStack itemStack, Object... data)
    {
        GL11.glTranslatef(-1f, -1f, 0f);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(SharedAssets.TOOL_TABLE_TEXTURE);
        SharedAssets.TOOL_TABLE.renderAll(); //TODO render warhead on table, remove some tools
    }

    @Override
    public boolean read(ByteBuf buf, int id, EntityPlayer player, PacketType type)
    {
        super.doUpdateGuiUsers();
        if (!super.read(buf, id, player, type))
        {
            return false;
        }
        return true;
    }

    public void sendCraftingPacket()
    {
        sendPacketToServer(new PacketTile(this, 1));
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player)
    {
        return new GuiWarheadStation(player, this, ID);
    }
}
