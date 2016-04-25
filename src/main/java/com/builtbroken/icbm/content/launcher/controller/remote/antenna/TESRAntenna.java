package com.builtbroken.icbm.content.launcher.controller.remote.antenna;

import com.builtbroken.icbm.client.Assets;
import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.GL11;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/25/2016.
 */
public class TESRAntenna extends TileEntitySpecialRenderer
{
    @Override
    public void renderTileEntityAt(TileEntity tile, double xx, double yy, double zz, float f)
    {
        GL11.glPushMatrix();
        GL11.glTranslatef((float) xx + 0.5f, (float) yy, (float) zz + 0.5f);
        int meta = tile.getBlockMetadata();
        if (meta == 0 || meta == 1)
        {
            //TODO if meta == 1 change texture to say its connected to a base
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.ANTENNA_TEXTURE);
            Assets.ANTENNA_TOWER_MODEL.renderAll();
        }
        else if (meta == 2)
        {
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.ANTENNA_TEXTURE);
            Assets.ANTENNA_BASE_MODEL.renderAll();
        }
        else if (meta == 3)
        {
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.ANTENNA_TEXTURE);
            Assets.ANTENNA_INTERSECTION_MODEL.renderAll();
        }
        else if (meta == 4)
        {
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.ANTENNA_TEXTURE);
            Assets.ANTENNA_NOTCH_MODEL.renderAll();
        }
        else if (meta == 5)
        {
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.ANTENNA_TEXTURE);
            Assets.ANTENNA_PIKE_MODEL.renderAll();
        }
        else if (meta == 6) //East West
        {
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.ANTENNA_TEXTURE);
            Assets.ANTENNA_ARM2_MODEL.renderAll();
        }
        else if (meta == 7) //North South
        {
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.ANTENNA_TEXTURE);
            Assets.ANTENNA_ARM_MODEL.renderAll();
        }
        GL11.glPopMatrix();
    }
}
