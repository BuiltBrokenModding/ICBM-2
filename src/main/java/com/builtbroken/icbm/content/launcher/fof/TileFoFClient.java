package com.builtbroken.icbm.content.launcher.fof;

import com.builtbroken.icbm.client.Assets;
import com.builtbroken.mc.api.items.ISimpleItemRenderer;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.prefab.tile.Tile;
import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
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
        GL11.glTranslatef(pos.xf() + 0.5f, pos.yf() + 1.5f, pos.zf() + 0.5f);
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
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.LAUNCHER_CONTROLLER_TEXTURE);
        Assets.LAUNCHER_CONTROLLER_MODEL.renderOnly("screen");
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.WEAPON_CASE_TEXTURE);
        Assets.LAUNCHER_CONTROLLER_MODEL.renderAllExcept("screen");
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
}
