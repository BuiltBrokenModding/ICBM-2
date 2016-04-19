package com.builtbroken.icbm.content.launcher.block;

import com.builtbroken.icbm.client.Assets;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.obj.GroupObject;
import org.lwjgl.opengl.GL11;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/2/2015.
 */
public final class ISBRLauncherFrame implements ISimpleBlockRenderingHandler
{
    public static final ISBRLauncherFrame INSTANCE = new ISBRLauncherFrame();

    public final int ID;

    public GroupObject north;
    public GroupObject south;
    public GroupObject east;
    public GroupObject west;

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
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.LAUNCHER_FRAME_TEXTURE);
        Assets.LAUNCHER_FRAME_BLOCK_MODEL.renderAll();
        GL11.glPopMatrix();
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
    {
        int meta = world.getBlockMetadata(x, y, z);
        Tessellator tess = Tessellator.instance;
        tess.draw();
        GL11.glPushMatrix();
        //GL11.glEnable(GL11.GL_LIGHTING);
        //GL11.glColor3f(1, 1, 1);

        if (meta == 0)
        {
            GL11.glTranslatef(x, y, z + 1);
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.LAUNCHER_FRAME_TEXTURE);
            Assets.LAUNCHER_FRAME_BLOCK_MODEL.renderAll();
        }
        else if (meta == 1 || meta == 2 || meta == 3 || meta == 4)
        {
            try
            {
                if (west == null)
                {
                    for (GroupObject object : Assets.LAUNCHER_FRAME_BLOCK_TOP_MODEL.groupObjects)
                    {
                        String name = object.name;
                        if (name.equals("SmallSilo.001_SmallSilo.000"))
                        {
                            west = object;
                        }
                        else if (name.equals("SmallSilo.002"))
                        {
                            south = object;
                        }
                        else if (name.equals("SmallSilo.003"))
                        {
                            north = object;
                        }
                        else if(name.equals("SmallSilo_SmallSilo.001"))
                        {
                            east = object;
                        }
                    }
                }
                GL11.glTranslatef(x + 0.5f, y + 0.4f, z + 0.5f);
                FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.LAUNCHER_FRAME_TOP_TEXTURE);
                final float moveValue = .09f;
                switch (meta)
                {
                    //north
                    case 1:
                        GL11.glTranslatef(0, 0, -moveValue + 0.01f);
                        north.render();
                        break;
                    //south
                    case 2:
                        GL11.glTranslatef(0, 0, moveValue);
                        south.render();
                        break;
                    //east
                    case 3:
                        GL11.glTranslatef(moveValue, 0, 0);
                        east.render();
                        break;
                    //west
                    case 4:
                        GL11.glTranslatef(-moveValue, 0, 0);
                        west.render();
                        break;
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        //GL11.glDisable(GL11.GL_LIGHTING);
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
