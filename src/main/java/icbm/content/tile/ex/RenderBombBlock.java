package icbm.content.tile.ex;

import icbm.Reference;
import icbm.explosion.Explosive;
import icbm.explosion.ExplosiveRegistry;
import icbm.content.render.models.MDiLei;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import resonant.lib.render.RenderUtility;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderBombBlock extends TileEntitySpecialRenderer implements ISimpleBlockRenderingHandler
{
    public static final ResourceLocation TEXTURE_FILE = new ResourceLocation(Reference.DOMAIN, Reference.MODEL_TEXTURE_PATH + "s-mine.png");
    public static final int ID = RenderingRegistry.getNextAvailableRenderId();

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer)
    {
        if (modelID == ID)
        {
            if (metadata == Explosive.sMine.getID())
            {
                GL11.glPushMatrix();
                GL11.glTranslatef(0.0F, 1.5F, 0.0F);
                GL11.glRotatef(180f, 0f, 0f, 1f);
                FMLClientHandler.instance().getClient().renderEngine.bindTexture(TEXTURE_FILE);
                MDiLei.INSTANCE.render(0.0625F);
                GL11.glPopMatrix();
            }
            else
            {
                try
                {

                    RenderUtility.renderNormalBlockAsItem(block, metadata, renderer);
                }
                catch (Exception e)
                {
                    Reference.LOGGER.severe("ICBM Explosive Rendering Crash with: " + block + " and metadata: " + metadata);
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess iBlockAccess, int x, int y, int z, Block block, int modelID, RenderBlocks renderer)
    {
        if (modelID == ID)
        {
            TileEntity tileEntity = iBlockAccess.getTileEntity(x, y, z);

            if (tileEntity instanceof TileExplosive)
            {
                Explosive explosive = ExplosiveRegistry.get(((TileExplosive) tileEntity).explosiveID);

                if (!(explosive.getBlockModel() != null && explosive.getBlockResource() != null))
                {
                    renderer.renderStandardBlock(block, x, y, z);
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float f)
    {
        if (tileEntity instanceof TileExplosive)
        {
            Explosive explosive = ExplosiveRegistry.get(((TileExplosive) tileEntity).explosiveID);

            if (explosive != null && explosive.getBlockModel() != null && explosive.getBlockResource() != null)
            {
                GL11.glPushMatrix();
                GL11.glTranslated(x + 0.5f, y + 1.5f, z + 0.5f);
                GL11.glRotatef(180f, 0f, 0f, 1f);
                FMLClientHandler.instance().getClient().renderEngine.bindTexture(explosive.getBlockResource());
                explosive.getBlockModel().render(0.0625f);
                RenderUtility.setTerrainTexture();
                GL11.glPopMatrix();
            }
        }
    }


    @Override
    public int getRenderId()
    {
        return ID;
    }

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		// TODO Auto-generated method stub
		return true;
	}

}
