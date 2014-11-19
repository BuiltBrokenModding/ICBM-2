package icbm.content.render;

import icbm.ICBM;
import icbm.content.tile.assembler.RenderMissileAssembler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BlockRenderHandler implements ISimpleBlockRenderingHandler
{
    public static final int ID = RenderingRegistry.getNextAvailableRenderId();

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer)
    {
        if (modelID == ID)
        {
            GL11.glPushMatrix();

            if (block == ICBM.blockMissileAssembler)
            {
                GL11.glTranslatef(0f, 0.5f, 0f);
                GL11.glScalef(0.5f, 0.5f, 0.5f);
                GL11.glRotatef(180f, 0f, 0f, 1f);
                GL11.glRotatef(180f, 0f, 1f, 0f);
                FMLClientHandler.instance().getClient().renderEngine.bindTexture(RenderMissileAssembler.TEXTURE_FILE);
                RenderMissileAssembler.MODEL_PANEL.render(0.0625F);
                RenderMissileAssembler.MODEL_CLAW1.render(0.0625F);
                RenderMissileAssembler.MODEL_CLAW2.render(0.0625F);
                RenderMissileAssembler.MODEL_CLAW3.render(0.0625F);
            }

            GL11.glPopMatrix();
        }
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess iBlockAccess, int x, int y, int z, Block block, int modelID, RenderBlocks renderer)
    {
        return false;
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
