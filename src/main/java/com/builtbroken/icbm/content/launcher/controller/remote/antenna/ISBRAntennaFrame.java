package com.builtbroken.icbm.content.launcher.controller.remote.antenna;

import com.builtbroken.icbm.client.Assets;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/2/2015.
 */
public final class ISBRAntennaFrame implements ISimpleBlockRenderingHandler
{
    public static final ISBRAntennaFrame INSTANCE = new ISBRAntennaFrame();

    public final int ID;

    private ISBRAntennaFrame()
    {
        ID = RenderingRegistry.getNextAvailableRenderId();
    }

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer)
    {
        GL11.glPushMatrix();
        GL11.glScalef(1f, 1f, 1f);
        GL11.glTranslatef(-0.5F, -0.5F, 0.5F);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.ANTENNA_TEXTURE);
        Assets.ANTENNA_TOWER_MODEL.renderAll();
        GL11.glPopMatrix();
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
    {
        int meta = world.getBlockMetadata(x, y, z);
        Tessellator tess = Tessellator.instance;
        tess.draw();
        GL11.glPushMatrix();
        GL11.glTranslatef(x + 0.5f, y, z + 0.5f);

        if (meta == 0 || meta == 1)
        {
            //TODO if meta == 1 change texture to say its connected to a base
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.ANTENNA_TEXTURE);
            Assets.ANTENNA_TOWER_MODEL.renderAll();
        }
        else if(meta == 2)
        {
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.ANTENNA_TEXTURE);
            Assets.ANTENNA_BASE_MODEL.renderAll();
        }
        else if(meta == 3)
        {
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.ANTENNA_TEXTURE);
            Assets.ANTENNA_INTERSECTION_MODEL.renderAll();
        }
        else if(meta == 4)
        {
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.ANTENNA_TEXTURE);
            Assets.ANTENNA_NOTCH_MODEL.renderAll();
        }
        else if(meta == 5)
        {
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.ANTENNA_TEXTURE);
            Assets.ANTENNA_PIKE_MODEL.renderAll();
        }
        else if(meta == 6)
        {
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.ANTENNA_TEXTURE);
            Assets.ANTENNA_ARM_MODEL.renderAll();
        }


        GL11.glPopMatrix();
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
        tess.startDrawingQuads();
        return true;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId)
    {
        return true;
    }

    @Override
    public int getRenderId()
    {
        return ID;
    }
}
