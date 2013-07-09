package icbm.zhapin.render.tile;

import icbm.core.ZhuYaoICBM;
import icbm.zhapin.muoxing.jiqi.MDiLei;
import icbm.zhapin.zhapin.TZhaDan;
import icbm.zhapin.zhapin.ZhaPin;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import calclavia.lib.render.CalclaviaRenderHelper;
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
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, FMLClientHandler.instance().getClient().renderEngine.getTexture(ZhuYaoICBM.SMINE_TEXTURE));
				MDiLei.INSTANCE.render(0.0625F);
				GL11.glPopMatrix();
			}
			else
			{
				try
				{

					CalclaviaRenderHelper.renderNormalBlockAsItem(block, metadata, renderer);
				}
				catch (Exception e)
				{
					ZhuYaoICBM.LOGGER.severe("ICBM Explosive Rendering Crash with: " + block + " and metadata: " + metadata);
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
