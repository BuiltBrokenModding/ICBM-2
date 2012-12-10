package icbm.renders;

import icbm.ZhuYao;
import icbm.jiqi.BJiQi.JiQi;
import icbm.models.MDiLei;
import icbm.zhapin.TZhaDan;
import icbm.zhapin.ZhaPin;
import net.minecraft.src.Block;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.RenderBlocks;
import net.minecraft.src.Tessellator;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class RHJiQi implements ISimpleBlockRenderingHandler
{
	public static final int ID = RenderingRegistry.getNextAvailableRenderId();

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer)
	{
		if (modelID == ID)
		{
			GL11.glPushMatrix();

			if (metadata < JiQi.FaSheDi.ordinal() * 3 + 3)
			{
				int tier = metadata;

				GL11.glRotatef(180f, 0f, 0f, 1f);
				GL11.glScalef(0.4f, 0.4f, 0.4f);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, FMLClientHandler.instance().getClient().renderEngine.getTexture(ZhuYao.TEXTURE_FILE_PATH + "Launcher" + tier + ".png"));

				if (tier == 0)
				{
					RFaSheDi.modelBase0.render(0.0625F);
					RFaSheDi.modelRail0.render(0.0625F);
				}
				else if (tier == 1)
				{
					RFaSheDi.modelBase1.render(0.0625F);
					RFaSheDi.modelRail1.render(0.0625F);
					GL11.glRotatef(180F, 0F, 180F, 1.0F);
					RFaSheDi.modelRail1.render(0.0625F);
				}
				else if (tier == 2)
				{
					RFaSheDi.modelBase2.render(0.0625F);
					RFaSheDi.modelRail2.render(0.0625F);
					GL11.glRotatef(180F, 0F, 180F, 1.0F);
					RFaSheDi.modelRail2.render(0.0625F);
				}
			}
			else if (metadata < JiQi.FaSheShiMuo.ordinal() * 3 + 3)
			{
				int tier = metadata - 3;
				GL11.glTranslatef(0f, 0.9f, 0f);
				GL11.glRotatef(180f, 0f, 0f, 1f);
				GL11.glRotatef(180f, 0f, 180f, 1f);

				GL11.glBindTexture(GL11.GL_TEXTURE_2D, FMLClientHandler.instance().getClient().renderEngine.getTexture(ZhuYao.TEXTURE_FILE_PATH + "Launcher" + tier + ".png"));

				if (tier == 0)
				{
					RFaSheShiMuo.model0.render(0.0625F);
				}
				else if (tier == 1)
				{
					RFaSheShiMuo.model1.render(0.0625F);
				}
				else if (tier == 2)
				{
					RFaSheShiMuo.model2.render(0.0625F);
				}
			}
			else if (metadata < JiQi.FaSheJia.ordinal() * 3 + 3)
			{
				int tier = metadata - 6;
				GL11.glTranslatef(0f, 0f, 0f);
				GL11.glRotatef(180f, 0f, 0f, 1f);
				GL11.glScalef(0.8f, 0.4f, 0.8f);

				GL11.glBindTexture(GL11.GL_TEXTURE_2D, FMLClientHandler.instance().getClient().renderEngine.getTexture(ZhuYao.TEXTURE_FILE_PATH + "Launcher0.png"));

				RFaSheJia.model.render(0.0625F);
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
