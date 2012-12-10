package icbm.renders;

import icbm.TYinXing;
import icbm.ZhuYao;
import net.minecraft.src.Block;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.RenderBlocks;
import net.minecraft.src.Tessellator;
import net.minecraft.src.TileEntity;
import net.minecraftforge.client.ForgeHooksClient;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class RHYinXing implements ISimpleBlockRenderingHandler
{
	public static final int ID = RenderingRegistry.getNextAvailableRenderId();

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer)
	{
		if (modelID == ID)
		{
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, FMLClientHandler.instance().getClient().renderEngine.getTexture(ZhuYao.BLOCK_TEXTURE_FILE));

			this.renderNormalBlock(block, metadata, renderer);
		}
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess iBlockAccess, int x, int y, int z, Block block, int modelID, RenderBlocks renderer)
	{
		if (modelID == ID)
		{
			TileEntity t = iBlockAccess.getBlockTileEntity(x, y, z);

			if (t != null)
			{
				if (t instanceof TYinXing)
				{
					TYinXing tileEntity = (TYinXing) t;
					Block jiaBlock = Block.blocksList[tileEntity.getJiaHaoMa()];

					if (jiaBlock != null)
					{
						if (jiaBlock.isDefaultTexture)
						{
							renderer.renderStandardBlock(block, x, y, z);
						}
						else
						{
							ForgeHooksClient.bindTexture(jiaBlock.getTextureFile(), 0);
							renderer.renderStandardBlock(block, x, y, z);
							ForgeHooksClient.unbindTexture();
						}

					}
					else
					{
						ForgeHooksClient.bindTexture(ZhuYao.BLOCK_TEXTURE_FILE, 0);
						renderer.renderStandardBlock(block, x, y, z);
						ForgeHooksClient.unbindTexture();
					}
				}
			}

			return true;
		}

		return false;
	}

	public void renderNormalBlock(Block block, int metadata, RenderBlocks renderer)
	{
		Tessellator var4 = Tessellator.instance;

		block.setBlockBoundsForItemRender();
		renderer.func_83018_a(block);
		GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		var4.startDrawingQuads();
		var4.setNormal(0.0F, -1.0F, 0.0F);
		renderer.renderBottomFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(0, metadata));
		var4.draw();

		var4.startDrawingQuads();
		var4.setNormal(0.0F, 1.0F, 0.0F);
		renderer.renderTopFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(1, metadata));
		var4.draw();

		var4.startDrawingQuads();
		var4.setNormal(0.0F, 0.0F, -1.0F);
		renderer.renderEastFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(2, metadata));
		var4.draw();
		var4.startDrawingQuads();
		var4.setNormal(0.0F, 0.0F, 1.0F);
		renderer.renderWestFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(3, metadata));
		var4.draw();
		var4.startDrawingQuads();
		var4.setNormal(-1.0F, 0.0F, 0.0F);
		renderer.renderNorthFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(4, metadata));
		var4.draw();
		var4.startDrawingQuads();
		var4.setNormal(1.0F, 0.0F, 0.0F);
		renderer.renderSouthFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(5, metadata));
		var4.draw();
		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
	}

	@Override
	public boolean shouldRender3DInInventory()
	{
		return true;
	}

	@Override
	public int getRenderId()
	{
		return ID;
	}

}
