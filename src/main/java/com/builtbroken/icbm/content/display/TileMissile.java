package com.builtbroken.icbm.content.display;

import com.builtbroken.icbm.content.crafting.missile.casing.MissileSmall;
import com.builtbroken.jlib.data.vector.Pos3D;
import com.builtbroken.mc.lib.render.RenderUtility;
import com.builtbroken.mc.lib.transform.region.Cuboid;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.prefab.tile.Tile;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.RenderBlocks;
import org.lwjgl.opengl.GL11;

/**
 * Version of the Missile Display that is static
 * Created by robert on 1/16/2015.
 */
public class TileMissile extends Tile
{
    public TileMissile()
    {
        super("TileMissile", Material.anvil);
        this.bounds = new Cuboid(0, 0, 0, 1, .1, 1);
        this.isOpaque = false;
        this.renderNormalBlock = true;
        this.renderTileEntity = true;
    }


    @Override
    public Tile newTile()
    {
        return new TileMissile();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderDynamic(Pos pos, float frame, int pass)
    {
        GL11.glPushMatrix();
        RenderUtility.disableLighting();
        GL11.glTranslatef(pos.xf() + 0.5f, pos.yf(), pos.zf() + 0.5f);
        GL11.glScaled(.0015625f, .0015625f, .0015625f);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(MissileSmall.TEXTURE);
        MissileSmall.MODEL.renderAll();
        RenderUtility.enableLighting();e
        GL11.glPopMatrix();
    }
}
