package com.builtbroken.icbm.content.launcher.block;

import com.builtbroken.icbm.content.Assets;
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
public class ISBRLauncherFrame implements ISimpleBlockRenderingHandler
{
    public static final ISBRLauncherFrame INSTANCE = new ISBRLauncherFrame();

    public final int ID;

    private ISBRLauncherFrame()
    {
        ID = RenderingRegistry.getNextAvailableRenderId();
    }

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer)
    {
        GL11.glPushMatrix();
        GL11.glScalef(1f, 1f, 1f);
        GL11.glTranslatef(-0.5F, -0.5F, 0.5F);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.GREY_FAKE_TEXTURE);
        Assets.LAUNCHER_FRAME_BLOCK_MODEL.renderAll();
        GL11.glPopMatrix();
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
    {
        //GL11.glPushMatrix();
        //GL11.glTranslatef(x, y, z);
        //GL11.glScalef(1.5f, 1.5f, 1.5f);
        //FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.GREY_FAKE_TEXTURE);
        //((WavefrontObject) Assets.LAUNCHER_FRAME_BLOCK_MODEL).tessellateAll(Tessellator.instance);
        //GL11.glPopMatrix();
        //FMLClientHandler.instance().getClient().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
        //renderer.renderBlockAllFaces(Blocks.fence, x, y, z);

        Tessellator tess = Tessellator.instance;
        tess.draw();
        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, z + 1);
        GL11.glColor3f(1, 1, 1);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.GREY_FAKE_TEXTURE);
        Assets.LAUNCHER_FRAME_BLOCK_MODEL.renderAll();
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
