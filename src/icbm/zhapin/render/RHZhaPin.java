package icbm.zhapin.render;

import icbm.core.ZhuYao;
import icbm.zhapin.muoxing.jiqi.MDiLei;
import icbm.zhapin.zhapin.TZhaDan;
import icbm.zhapin.zhapin.ZhaPin;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RHZhaPin implements ISimpleBlockRenderingHandler
{
	public static final int ID = RenderingRegistry.getNextAvailableRenderId();

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer)
	{
		if (modelID == ID)
		{
			if (metadata == ZhaPin.diLei.getID())
			{
				GL11.glPushMatrix();
				GL11.glTranslatef(0.0F, 1.5F, 0.0F);
				GL11.glRotatef(180f, 0f, 0f, 1f);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, FMLClientHandler.instance().getClient().renderEngine.getTexture(ZhuYao.SMINE_TEXTURE));
				MDiLei.INSTANCE.render(0.0625F);
				GL11.glPopMatrix();
			}
			else
			{
				try
				{
					Tessellator var4 = Tessellator.instance;
					block.setBlockBoundsForItemRender();
					renderer.setRenderBoundsFromBlock(block);
					GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
					GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
					var4.startDrawingQuads();
					var4.setNormal(0.0F, -1.0F, 0.0F);
					renderer.renderBottomFace(block, 0.0D, 0.0D, 0.0D, block.getIcon(0, metadata));
					var4.draw();

					var4.startDrawingQuads();
					var4.setNormal(0.0F, 1.0F, 0.0F);
					renderer.renderTopFace(block, 0.0D, 0.0D, 0.0D, block.getIcon(1, metadata));
					var4.draw();

					var4.startDrawingQuads();
					var4.setNormal(0.0F, 0.0F, -1.0F);
					renderer.renderEastFace(block, 0.0D, 0.0D, 0.0D, block.getIcon(2, metadata));
					var4.draw();
					var4.startDrawingQuads();
					var4.setNormal(0.0F, 0.0F, 1.0F);
					renderer.renderWestFace(block, 0.0D, 0.0D, 0.0D, block.getIcon(3, metadata));
					var4.draw();
					var4.startDrawingQuads();
					var4.setNormal(-1.0F, 0.0F, 0.0F);
					renderer.renderNorthFace(block, 0.0D, 0.0D, 0.0D, block.getIcon(4, metadata));
					var4.draw();
					var4.startDrawingQuads();
					var4.setNormal(1.0F, 0.0F, 0.0F);
					renderer.renderSouthFace(block, 0.0D, 0.0D, 0.0D, block.getIcon(5, metadata));
					var4.draw();
					GL11.glTranslatef(0.5F, 0.5F, 0.5F);
				}
				catch (Exception e)
				{
					ZhuYao.LOGGER.severe("ICBM Explosive Rendering Crash with: " + block + " and metadata: " + metadata);
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
			if (((TZhaDan) iBlockAccess.getBlockTileEntity(x, y, z)).haoMa == ZhaPin.diLei.getID())
			{
				return false;
			}
			else
			{
				renderer.renderStandardBlock(block, x, y, z);
				return true;
			}
		}

		return false;
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
