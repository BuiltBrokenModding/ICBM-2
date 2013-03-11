package icbm.wanyi.render;

import icbm.core.ZhuYao;
import icbm.wanyi.BYinXing;
import icbm.wanyi.TYinXing;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.common.ForgeDirection;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RBYinXing implements ISimpleBlockRenderingHandler
{
	public static final int ID = RenderingRegistry.getNextAvailableRenderId();

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer)
	{
		if (modelID == ID)
		{
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, FMLClientHandler.instance().getClient().renderEngine.getTexture(ZhuYao.BLOCK_PATH));

			this.renderNormalBlock(block, metadata, renderer);
		}
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess iBlockAccess, int x, int y, int z, Block block, int modelID, RenderBlocks renderer)
	{
		if (modelID == ID)
		{
			this.renderStandardBlock(renderer, block, x, y, z);
			return true;
		}

		return false;
	}

	public void renderNormalBlock(Block block, int metadata, RenderBlocks renderer)
	{
		Tessellator var4 = Tessellator.instance;

		block.setBlockBoundsForItemRender();
		renderer.setRenderBoundsFromBlock(block);
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

	public boolean renderStandardBlock(RenderBlocks renderBlocks, Block par1Block, int x, int y, int z)
	{
		int var5 = par1Block.colorMultiplier(renderBlocks.blockAccess, x, y, z);
		float var6 = (float) (var5 >> 16 & 255) / 255.0F;
		float var7 = (float) (var5 >> 8 & 255) / 255.0F;
		float var8 = (float) (var5 & 255) / 255.0F;

		if (EntityRenderer.anaglyphEnable)
		{
			float var9 = (var6 * 30.0F + var7 * 59.0F + var8 * 11.0F) / 100.0F;
			float var10 = (var6 * 30.0F + var7 * 70.0F) / 100.0F;
			float var11 = (var6 * 30.0F + var8 * 70.0F) / 100.0F;
			var6 = var9;
			var7 = var10;
			var8 = var11;
		}

		return this.renderStandardBlockWithAmbientOcclusion(renderBlocks, par1Block, x, y, z, var6, var7, var8);
		// return Minecraft.isAmbientOcclusionEnabled() &&
		// Block.lightValue[par1Block.blockID] == 0
		// ? this.renderStandardBlockWithAmbientOcclusion(renderBlocks,
		// par1Block, x, y, z, var6,
		// var7, var8) :
		// this.renderStandardBlockWithColorMultiplier(renderBlocks, par1Block,
		// x, y,
		// z, var6, var7, var8);
	}

	/**
	 * Used to easily do render call backs to make code more efficient.
	 * 
	 * @author Calclavia
	 * 
	 */
	private interface IRenderCallBack
	{
		public void renderFace(RenderBlocks renderBlocks, Block block, double x, double y, double z, int texture);
	}

	public void renderSpecial(RenderBlocks renderBlocks, ForgeDirection direction, Block block, double x, double y, double z, int texture, IRenderCallBack callBack)
	{
		if (block instanceof BYinXing)
		{
			TileEntity tileEntity = renderBlocks.blockAccess.getBlockTileEntity((int) x, (int) y, (int) z);

			if (tileEntity != null)
			{
				if (tileEntity instanceof TYinXing)
				{
					if (((TYinXing) tileEntity).getQing(direction))
					{
						ForgeHooksClient.bindTexture(Block.glass.getTextureFile(), 0);
						callBack.renderFace(renderBlocks, block, x, y, z, texture);
					}
					else
					{
						Block jiaBlock = Block.blocksList[((TYinXing) tileEntity).getJiaHaoMa()];

						if (jiaBlock != null)
						{
							ForgeHooksClient.bindTexture(jiaBlock.getTextureFile(), 0);
							callBack.renderFace(renderBlocks, block, x, y, z, texture);
						}
						else
						{
							ForgeHooksClient.bindTexture(ZhuYao.BLOCK_PATH, 0);
							callBack.renderFace(renderBlocks, block, x, y, z, texture);
						}
					}

					ForgeHooksClient.unbindTexture();

					return;
				}
			}
		}

		callBack.renderFace(renderBlocks, block, x, y, z, texture);
	}

	public void renderTopFace(RenderBlocks renderBlocks, Block block, double x, double y, double z, int texture)
	{
		this.renderSpecial(renderBlocks, ForgeDirection.UP, block, x, y, z, texture, new IRenderCallBack()
		{
			public void renderFace(RenderBlocks renderBlocks, Block block, double x, double y, double z, int texture)
			{
				renderBlocks.renderTopFace(block, x, y, z, texture);
			}
		});
	}

	public void renderBottomFace(RenderBlocks renderBlocks, Block block, double x, double y, double z, int texture)
	{
		this.renderSpecial(renderBlocks, ForgeDirection.DOWN, block, x, y, z, texture, new IRenderCallBack()
		{
			public void renderFace(RenderBlocks renderBlocks, Block block, double x, double y, double z, int texture)
			{
				renderBlocks.renderBottomFace(block, x, y, z, texture);
			}
		});
	}

	public void renderNorthFace(RenderBlocks renderBlocks, Block block, double x, double y, double z, int texture)
	{
		this.renderSpecial(renderBlocks, ForgeDirection.WEST, block, x, y, z, texture, new IRenderCallBack()
		{
			public void renderFace(RenderBlocks renderBlocks, Block block, double x, double y, double z, int texture)
			{
				renderBlocks.renderNorthFace(block, x, y, z, texture);
			}
		});
	}

	public void renderSouthFace(RenderBlocks renderBlocks, Block block, double x, double y, double z, int texture)
	{
		this.renderSpecial(renderBlocks, ForgeDirection.EAST, block, x, y, z, texture, new IRenderCallBack()
		{
			public void renderFace(RenderBlocks renderBlocks, Block block, double x, double y, double z, int texture)
			{
				renderBlocks.renderSouthFace(block, x, y, z, texture);
			}
		});
	}

	public void renderWestFace(RenderBlocks renderBlocks, Block block, double x, double y, double z, int texture)
	{
		this.renderSpecial(renderBlocks, ForgeDirection.SOUTH, block, x, y, z, texture, new IRenderCallBack()
		{
			public void renderFace(RenderBlocks renderBlocks, Block block, double x, double y, double z, int texture)
			{
				renderBlocks.renderWestFace(block, x, y, z, texture);
			}
		});
	}

	public void renderEastFace(RenderBlocks renderBlocks, Block block, double x, double y, double z, int texture)
	{
		this.renderSpecial(renderBlocks, ForgeDirection.NORTH, block, x, y, z, texture, new IRenderCallBack()
		{
			public void renderFace(RenderBlocks renderBlocks, Block block, double x, double y, double z, int texture)
			{
				renderBlocks.renderEastFace(block, x, y, z, texture);
			}
		});
	}

	public boolean renderStandardBlockWithAmbientOcclusion(RenderBlocks renderBlocks, Block par1Block, int x, int y, int z, float par5, float par6, float par7)
	{
		renderBlocks.enableAO = true;
		boolean var8 = false;
		float var9 = renderBlocks.lightValueOwn;
		float var10 = renderBlocks.lightValueOwn;
		float var11 = renderBlocks.lightValueOwn;
		float var12 = renderBlocks.lightValueOwn;
		boolean var13 = true;
		boolean var14 = true;
		boolean var15 = true;
		boolean var16 = true;
		boolean var17 = true;
		boolean var18 = true;
		renderBlocks.lightValueOwn = par1Block.getAmbientOcclusionLightValue(renderBlocks.blockAccess, x, y, z);
		renderBlocks.aoLightValueXNeg = par1Block.getAmbientOcclusionLightValue(renderBlocks.blockAccess, x - 1, y, z);
		renderBlocks.aoLightValueYNeg = par1Block.getAmbientOcclusionLightValue(renderBlocks.blockAccess, x, y - 1, z);
		renderBlocks.aoLightValueZNeg = par1Block.getAmbientOcclusionLightValue(renderBlocks.blockAccess, x, y, z - 1);
		renderBlocks.aoLightValueXPos = par1Block.getAmbientOcclusionLightValue(renderBlocks.blockAccess, x + 1, y, z);
		renderBlocks.aoLightValueYPos = par1Block.getAmbientOcclusionLightValue(renderBlocks.blockAccess, x, y + 1, z);
		renderBlocks.aoLightValueZPos = par1Block.getAmbientOcclusionLightValue(renderBlocks.blockAccess, x, y, z + 1);
		int var19 = par1Block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x, y, z);
		int var20 = var19;
		int var21 = var19;
		int var22 = var19;
		int var23 = var19;
		int var24 = var19;
		int var25 = var19;

		if (renderBlocks.renderMinY <= 0.0D)
		{
			var21 = par1Block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x, y - 1, z);
		}

		if (renderBlocks.renderMaxY >= 1.0D)
		{
			var24 = par1Block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x, y + 1, z);
		}

		if (renderBlocks.renderMinX <= 0.0D)
		{
			var20 = par1Block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x - 1, y, z);
		}

		if (renderBlocks.renderMaxX >= 1.0D)
		{
			var23 = par1Block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x + 1, y, z);
		}

		if (renderBlocks.renderMinZ <= 0.0D)
		{
			var22 = par1Block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x, y, z - 1);
		}

		if (renderBlocks.renderMaxZ >= 1.0D)
		{
			var25 = par1Block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x, y, z + 1);
		}

		Tessellator var26 = Tessellator.instance;
		var26.setBrightness(983055);
		renderBlocks.aoGrassXYZPPC = Block.canBlockGrass[renderBlocks.blockAccess.getBlockId(x + 1, y + 1, z)];
		renderBlocks.aoGrassXYZPNC = Block.canBlockGrass[renderBlocks.blockAccess.getBlockId(x + 1, y - 1, z)];
		renderBlocks.aoGrassXYZPCP = Block.canBlockGrass[renderBlocks.blockAccess.getBlockId(x + 1, y, z + 1)];
		renderBlocks.aoGrassXYZPCN = Block.canBlockGrass[renderBlocks.blockAccess.getBlockId(x + 1, y, z - 1)];
		renderBlocks.aoGrassXYZNPC = Block.canBlockGrass[renderBlocks.blockAccess.getBlockId(x - 1, y + 1, z)];
		renderBlocks.aoGrassXYZNNC = Block.canBlockGrass[renderBlocks.blockAccess.getBlockId(x - 1, y - 1, z)];
		renderBlocks.aoGrassXYZNCN = Block.canBlockGrass[renderBlocks.blockAccess.getBlockId(x - 1, y, z - 1)];
		renderBlocks.aoGrassXYZNCP = Block.canBlockGrass[renderBlocks.blockAccess.getBlockId(x - 1, y, z + 1)];
		renderBlocks.aoGrassXYZCPP = Block.canBlockGrass[renderBlocks.blockAccess.getBlockId(x, y + 1, z + 1)];
		renderBlocks.aoGrassXYZCPN = Block.canBlockGrass[renderBlocks.blockAccess.getBlockId(x, y + 1, z - 1)];
		renderBlocks.aoGrassXYZCNP = Block.canBlockGrass[renderBlocks.blockAccess.getBlockId(x, y - 1, z + 1)];
		renderBlocks.aoGrassXYZCNN = Block.canBlockGrass[renderBlocks.blockAccess.getBlockId(x, y - 1, z - 1)];

		if (par1Block.blockIndexInTexture == 3)
		{
			var18 = false;
			var17 = false;
			var16 = false;
			var15 = false;
			var13 = false;
		}

		if (renderBlocks.overrideBlockTexture >= 0)
		{
			var18 = false;
			var17 = false;
			var16 = false;
			var15 = false;
			var13 = false;
		}

		if (renderBlocks.renderAllFaces || par1Block.shouldSideBeRendered(renderBlocks.blockAccess, x, y - 1, z, 0))
		{
			if (renderBlocks.aoType > 0)
			{
				if (renderBlocks.renderMinY <= 0.0D)
				{
					--y;
				}

				renderBlocks.aoBrightnessXYNN = par1Block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x - 1, y, z);
				renderBlocks.aoBrightnessYZNN = par1Block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x, y, z - 1);
				renderBlocks.aoBrightnessYZNP = par1Block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x, y, z + 1);
				renderBlocks.aoBrightnessXYPN = par1Block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x + 1, y, z);
				renderBlocks.aoLightValueScratchXYNN = par1Block.getAmbientOcclusionLightValue(renderBlocks.blockAccess, x - 1, y, z);
				renderBlocks.aoLightValueScratchYZNN = par1Block.getAmbientOcclusionLightValue(renderBlocks.blockAccess, x, y, z - 1);
				renderBlocks.aoLightValueScratchYZNP = par1Block.getAmbientOcclusionLightValue(renderBlocks.blockAccess, x, y, z + 1);
				renderBlocks.aoLightValueScratchXYPN = par1Block.getAmbientOcclusionLightValue(renderBlocks.blockAccess, x + 1, y, z);

				if (!renderBlocks.aoGrassXYZCNN && !renderBlocks.aoGrassXYZNNC)
				{
					renderBlocks.aoLightValueScratchXYZNNN = renderBlocks.aoLightValueScratchXYNN;
					renderBlocks.aoBrightnessXYZNNN = renderBlocks.aoBrightnessXYNN;
				}
				else
				{
					renderBlocks.aoLightValueScratchXYZNNN = par1Block.getAmbientOcclusionLightValue(renderBlocks.blockAccess, x - 1, y, z - 1);
					renderBlocks.aoBrightnessXYZNNN = par1Block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x - 1, y, z - 1);
				}

				if (!renderBlocks.aoGrassXYZCNP && !renderBlocks.aoGrassXYZNNC)
				{
					renderBlocks.aoLightValueScratchXYZNNP = renderBlocks.aoLightValueScratchXYNN;
					renderBlocks.aoBrightnessXYZNNP = renderBlocks.aoBrightnessXYNN;
				}
				else
				{
					renderBlocks.aoLightValueScratchXYZNNP = par1Block.getAmbientOcclusionLightValue(renderBlocks.blockAccess, x - 1, y, z + 1);
					renderBlocks.aoBrightnessXYZNNP = par1Block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x - 1, y, z + 1);
				}

				if (!renderBlocks.aoGrassXYZCNN && !renderBlocks.aoGrassXYZPNC)
				{
					renderBlocks.aoLightValueScratchXYZPNN = renderBlocks.aoLightValueScratchXYPN;
					renderBlocks.aoBrightnessXYZPNN = renderBlocks.aoBrightnessXYPN;
				}
				else
				{
					renderBlocks.aoLightValueScratchXYZPNN = par1Block.getAmbientOcclusionLightValue(renderBlocks.blockAccess, x + 1, y, z - 1);
					renderBlocks.aoBrightnessXYZPNN = par1Block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x + 1, y, z - 1);
				}

				if (!renderBlocks.aoGrassXYZCNP && !renderBlocks.aoGrassXYZPNC)
				{
					renderBlocks.aoLightValueScratchXYZPNP = renderBlocks.aoLightValueScratchXYPN;
					renderBlocks.aoBrightnessXYZPNP = renderBlocks.aoBrightnessXYPN;
				}
				else
				{
					renderBlocks.aoLightValueScratchXYZPNP = par1Block.getAmbientOcclusionLightValue(renderBlocks.blockAccess, x + 1, y, z + 1);
					renderBlocks.aoBrightnessXYZPNP = par1Block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x + 1, y, z + 1);
				}

				if (renderBlocks.renderMinY <= 0.0D)
				{
					++y;
				}

				var9 = (renderBlocks.aoLightValueScratchXYZNNP + renderBlocks.aoLightValueScratchXYNN + renderBlocks.aoLightValueScratchYZNP + renderBlocks.aoLightValueYNeg) / 4.0F;
				var12 = (renderBlocks.aoLightValueScratchYZNP + renderBlocks.aoLightValueYNeg + renderBlocks.aoLightValueScratchXYZPNP + renderBlocks.aoLightValueScratchXYPN) / 4.0F;
				var11 = (renderBlocks.aoLightValueYNeg + renderBlocks.aoLightValueScratchYZNN + renderBlocks.aoLightValueScratchXYPN + renderBlocks.aoLightValueScratchXYZPNN) / 4.0F;
				var10 = (renderBlocks.aoLightValueScratchXYNN + renderBlocks.aoLightValueScratchXYZNNN + renderBlocks.aoLightValueYNeg + renderBlocks.aoLightValueScratchYZNN) / 4.0F;
				renderBlocks.brightnessTopLeft = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessXYZNNP, renderBlocks.aoBrightnessXYNN, renderBlocks.aoBrightnessYZNP, var21);
				renderBlocks.brightnessTopRight = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessYZNP, renderBlocks.aoBrightnessXYZPNP, renderBlocks.aoBrightnessXYPN, var21);
				renderBlocks.brightnessBottomRight = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessYZNN, renderBlocks.aoBrightnessXYPN, renderBlocks.aoBrightnessXYZPNN, var21);
				renderBlocks.brightnessBottomLeft = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessXYNN, renderBlocks.aoBrightnessXYZNNN, renderBlocks.aoBrightnessYZNN, var21);
			}
			else
			{
				var12 = renderBlocks.aoLightValueYNeg;
				var11 = renderBlocks.aoLightValueYNeg;
				var10 = renderBlocks.aoLightValueYNeg;
				var9 = renderBlocks.aoLightValueYNeg;
				renderBlocks.brightnessTopLeft = renderBlocks.brightnessBottomLeft = renderBlocks.brightnessBottomRight = renderBlocks.brightnessTopRight = renderBlocks.aoBrightnessXYNN;
			}

			renderBlocks.colorRedTopLeft = renderBlocks.colorRedBottomLeft = renderBlocks.colorRedBottomRight = renderBlocks.colorRedTopRight = (var13 ? par5 : 1.0F) * 0.5F;
			renderBlocks.colorGreenTopLeft = renderBlocks.colorGreenBottomLeft = renderBlocks.colorGreenBottomRight = renderBlocks.colorGreenTopRight = (var13 ? par6 : 1.0F) * 0.5F;
			renderBlocks.colorBlueTopLeft = renderBlocks.colorBlueBottomLeft = renderBlocks.colorBlueBottomRight = renderBlocks.colorBlueTopRight = (var13 ? par7 : 1.0F) * 0.5F;
			renderBlocks.colorRedTopLeft *= var9;
			renderBlocks.colorGreenTopLeft *= var9;
			renderBlocks.colorBlueTopLeft *= var9;
			renderBlocks.colorRedBottomLeft *= var10;
			renderBlocks.colorGreenBottomLeft *= var10;
			renderBlocks.colorBlueBottomLeft *= var10;
			renderBlocks.colorRedBottomRight *= var11;
			renderBlocks.colorGreenBottomRight *= var11;
			renderBlocks.colorBlueBottomRight *= var11;
			renderBlocks.colorRedTopRight *= var12;
			renderBlocks.colorGreenTopRight *= var12;
			renderBlocks.colorBlueTopRight *= var12;
			this.renderBottomFace(renderBlocks, par1Block, (double) x, (double) y, (double) z, par1Block.getBlockTexture(renderBlocks.blockAccess, x, y, z, 0));
			var8 = true;
		}

		if (renderBlocks.renderAllFaces || par1Block.shouldSideBeRendered(renderBlocks.blockAccess, x, y + 1, z, 1))
		{
			if (renderBlocks.aoType > 0)
			{
				if (renderBlocks.renderMaxY >= 1.0D)
				{
					++y;
				}

				renderBlocks.aoBrightnessXYNP = par1Block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x - 1, y, z);
				renderBlocks.aoBrightnessXYPP = par1Block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x + 1, y, z);
				renderBlocks.aoBrightnessYZPN = par1Block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x, y, z - 1);
				renderBlocks.aoBrightnessYZPP = par1Block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x, y, z + 1);
				renderBlocks.aoLightValueScratchXYNP = par1Block.getAmbientOcclusionLightValue(renderBlocks.blockAccess, x - 1, y, z);
				renderBlocks.aoLightValueScratchXYPP = par1Block.getAmbientOcclusionLightValue(renderBlocks.blockAccess, x + 1, y, z);
				renderBlocks.aoLightValueScratchYZPN = par1Block.getAmbientOcclusionLightValue(renderBlocks.blockAccess, x, y, z - 1);
				renderBlocks.aoLightValueScratchYZPP = par1Block.getAmbientOcclusionLightValue(renderBlocks.blockAccess, x, y, z + 1);

				if (!renderBlocks.aoGrassXYZCPN && !renderBlocks.aoGrassXYZNPC)
				{
					renderBlocks.aoLightValueScratchXYZNPN = renderBlocks.aoLightValueScratchXYNP;
					renderBlocks.aoBrightnessXYZNPN = renderBlocks.aoBrightnessXYNP;
				}
				else
				{
					renderBlocks.aoLightValueScratchXYZNPN = par1Block.getAmbientOcclusionLightValue(renderBlocks.blockAccess, x - 1, y, z - 1);
					renderBlocks.aoBrightnessXYZNPN = par1Block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x - 1, y, z - 1);
				}

				if (!renderBlocks.aoGrassXYZCPN && !renderBlocks.aoGrassXYZPPC)
				{
					renderBlocks.aoLightValueScratchXYZPPN = renderBlocks.aoLightValueScratchXYPP;
					renderBlocks.aoBrightnessXYZPPN = renderBlocks.aoBrightnessXYPP;
				}
				else
				{
					renderBlocks.aoLightValueScratchXYZPPN = par1Block.getAmbientOcclusionLightValue(renderBlocks.blockAccess, x + 1, y, z - 1);
					renderBlocks.aoBrightnessXYZPPN = par1Block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x + 1, y, z - 1);
				}

				if (!renderBlocks.aoGrassXYZCPP && !renderBlocks.aoGrassXYZNPC)
				{
					renderBlocks.aoLightValueScratchXYZNPP = renderBlocks.aoLightValueScratchXYNP;
					renderBlocks.aoBrightnessXYZNPP = renderBlocks.aoBrightnessXYNP;
				}
				else
				{
					renderBlocks.aoLightValueScratchXYZNPP = par1Block.getAmbientOcclusionLightValue(renderBlocks.blockAccess, x - 1, y, z + 1);
					renderBlocks.aoBrightnessXYZNPP = par1Block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x - 1, y, z + 1);
				}

				if (!renderBlocks.aoGrassXYZCPP && !renderBlocks.aoGrassXYZPPC)
				{
					renderBlocks.aoLightValueScratchXYZPPP = renderBlocks.aoLightValueScratchXYPP;
					renderBlocks.aoBrightnessXYZPPP = renderBlocks.aoBrightnessXYPP;
				}
				else
				{
					renderBlocks.aoLightValueScratchXYZPPP = par1Block.getAmbientOcclusionLightValue(renderBlocks.blockAccess, x + 1, y, z + 1);
					renderBlocks.aoBrightnessXYZPPP = par1Block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x + 1, y, z + 1);
				}

				if (renderBlocks.renderMaxY >= 1.0D)
				{
					--y;
				}

				var12 = (renderBlocks.aoLightValueScratchXYZNPP + renderBlocks.aoLightValueScratchXYNP + renderBlocks.aoLightValueScratchYZPP + renderBlocks.aoLightValueYPos) / 4.0F;
				var9 = (renderBlocks.aoLightValueScratchYZPP + renderBlocks.aoLightValueYPos + renderBlocks.aoLightValueScratchXYZPPP + renderBlocks.aoLightValueScratchXYPP) / 4.0F;
				var10 = (renderBlocks.aoLightValueYPos + renderBlocks.aoLightValueScratchYZPN + renderBlocks.aoLightValueScratchXYPP + renderBlocks.aoLightValueScratchXYZPPN) / 4.0F;
				var11 = (renderBlocks.aoLightValueScratchXYNP + renderBlocks.aoLightValueScratchXYZNPN + renderBlocks.aoLightValueYPos + renderBlocks.aoLightValueScratchYZPN) / 4.0F;
				renderBlocks.brightnessTopRight = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessXYZNPP, renderBlocks.aoBrightnessXYNP, renderBlocks.aoBrightnessYZPP, var24);
				renderBlocks.brightnessTopLeft = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessYZPP, renderBlocks.aoBrightnessXYZPPP, renderBlocks.aoBrightnessXYPP, var24);
				renderBlocks.brightnessBottomLeft = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessYZPN, renderBlocks.aoBrightnessXYPP, renderBlocks.aoBrightnessXYZPPN, var24);
				renderBlocks.brightnessBottomRight = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessXYNP, renderBlocks.aoBrightnessXYZNPN, renderBlocks.aoBrightnessYZPN, var24);
			}
			else
			{
				var12 = renderBlocks.aoLightValueYPos;
				var11 = renderBlocks.aoLightValueYPos;
				var10 = renderBlocks.aoLightValueYPos;
				var9 = renderBlocks.aoLightValueYPos;
				renderBlocks.brightnessTopLeft = renderBlocks.brightnessBottomLeft = renderBlocks.brightnessBottomRight = renderBlocks.brightnessTopRight = var24;
			}

			renderBlocks.colorRedTopLeft = renderBlocks.colorRedBottomLeft = renderBlocks.colorRedBottomRight = renderBlocks.colorRedTopRight = var14 ? par5 : 1.0F;
			renderBlocks.colorGreenTopLeft = renderBlocks.colorGreenBottomLeft = renderBlocks.colorGreenBottomRight = renderBlocks.colorGreenTopRight = var14 ? par6 : 1.0F;
			renderBlocks.colorBlueTopLeft = renderBlocks.colorBlueBottomLeft = renderBlocks.colorBlueBottomRight = renderBlocks.colorBlueTopRight = var14 ? par7 : 1.0F;
			renderBlocks.colorRedTopLeft *= var9;
			renderBlocks.colorGreenTopLeft *= var9;
			renderBlocks.colorBlueTopLeft *= var9;
			renderBlocks.colorRedBottomLeft *= var10;
			renderBlocks.colorGreenBottomLeft *= var10;
			renderBlocks.colorBlueBottomLeft *= var10;
			renderBlocks.colorRedBottomRight *= var11;
			renderBlocks.colorGreenBottomRight *= var11;
			renderBlocks.colorBlueBottomRight *= var11;
			renderBlocks.colorRedTopRight *= var12;
			renderBlocks.colorGreenTopRight *= var12;
			renderBlocks.colorBlueTopRight *= var12;
			this.renderTopFace(renderBlocks, par1Block, (double) x, (double) y, (double) z, par1Block.getBlockTexture(renderBlocks.blockAccess, x, y, z, 1));
			var8 = true;
		}

		int var27;

		if (renderBlocks.renderAllFaces || par1Block.shouldSideBeRendered(renderBlocks.blockAccess, x, y, z - 1, 2))
		{
			if (renderBlocks.aoType > 0)
			{
				if (renderBlocks.renderMinZ <= 0.0D)
				{
					--z;
				}

				renderBlocks.aoLightValueScratchXZNN = par1Block.getAmbientOcclusionLightValue(renderBlocks.blockAccess, x - 1, y, z);
				renderBlocks.aoLightValueScratchYZNN = par1Block.getAmbientOcclusionLightValue(renderBlocks.blockAccess, x, y - 1, z);
				renderBlocks.aoLightValueScratchYZPN = par1Block.getAmbientOcclusionLightValue(renderBlocks.blockAccess, x, y + 1, z);
				renderBlocks.aoLightValueScratchXZPN = par1Block.getAmbientOcclusionLightValue(renderBlocks.blockAccess, x + 1, y, z);
				renderBlocks.aoBrightnessXZNN = par1Block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x - 1, y, z);
				renderBlocks.aoBrightnessYZNN = par1Block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x, y - 1, z);
				renderBlocks.aoBrightnessYZPN = par1Block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x, y + 1, z);
				renderBlocks.aoBrightnessXZPN = par1Block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x + 1, y, z);

				if (!renderBlocks.aoGrassXYZNCN && !renderBlocks.aoGrassXYZCNN)
				{
					renderBlocks.aoLightValueScratchXYZNNN = renderBlocks.aoLightValueScratchXZNN;
					renderBlocks.aoBrightnessXYZNNN = renderBlocks.aoBrightnessXZNN;
				}
				else
				{
					renderBlocks.aoLightValueScratchXYZNNN = par1Block.getAmbientOcclusionLightValue(renderBlocks.blockAccess, x - 1, y - 1, z);
					renderBlocks.aoBrightnessXYZNNN = par1Block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x - 1, y - 1, z);
				}

				if (!renderBlocks.aoGrassXYZNCN && !renderBlocks.aoGrassXYZCPN)
				{
					renderBlocks.aoLightValueScratchXYZNPN = renderBlocks.aoLightValueScratchXZNN;
					renderBlocks.aoBrightnessXYZNPN = renderBlocks.aoBrightnessXZNN;
				}
				else
				{
					renderBlocks.aoLightValueScratchXYZNPN = par1Block.getAmbientOcclusionLightValue(renderBlocks.blockAccess, x - 1, y + 1, z);
					renderBlocks.aoBrightnessXYZNPN = par1Block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x - 1, y + 1, z);
				}

				if (!renderBlocks.aoGrassXYZPCN && !renderBlocks.aoGrassXYZCNN)
				{
					renderBlocks.aoLightValueScratchXYZPNN = renderBlocks.aoLightValueScratchXZPN;
					renderBlocks.aoBrightnessXYZPNN = renderBlocks.aoBrightnessXZPN;
				}
				else
				{
					renderBlocks.aoLightValueScratchXYZPNN = par1Block.getAmbientOcclusionLightValue(renderBlocks.blockAccess, x + 1, y - 1, z);
					renderBlocks.aoBrightnessXYZPNN = par1Block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x + 1, y - 1, z);
				}

				if (!renderBlocks.aoGrassXYZPCN && !renderBlocks.aoGrassXYZCPN)
				{
					renderBlocks.aoLightValueScratchXYZPPN = renderBlocks.aoLightValueScratchXZPN;
					renderBlocks.aoBrightnessXYZPPN = renderBlocks.aoBrightnessXZPN;
				}
				else
				{
					renderBlocks.aoLightValueScratchXYZPPN = par1Block.getAmbientOcclusionLightValue(renderBlocks.blockAccess, x + 1, y + 1, z);
					renderBlocks.aoBrightnessXYZPPN = par1Block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x + 1, y + 1, z);
				}

				if (renderBlocks.renderMinZ <= 0.0D)
				{
					++z;
				}

				var9 = (renderBlocks.aoLightValueScratchXZNN + renderBlocks.aoLightValueScratchXYZNPN + renderBlocks.aoLightValueZNeg + renderBlocks.aoLightValueScratchYZPN) / 4.0F;
				var10 = (renderBlocks.aoLightValueZNeg + renderBlocks.aoLightValueScratchYZPN + renderBlocks.aoLightValueScratchXZPN + renderBlocks.aoLightValueScratchXYZPPN) / 4.0F;
				var11 = (renderBlocks.aoLightValueScratchYZNN + renderBlocks.aoLightValueZNeg + renderBlocks.aoLightValueScratchXYZPNN + renderBlocks.aoLightValueScratchXZPN) / 4.0F;
				var12 = (renderBlocks.aoLightValueScratchXYZNNN + renderBlocks.aoLightValueScratchXZNN + renderBlocks.aoLightValueScratchYZNN + renderBlocks.aoLightValueZNeg) / 4.0F;
				renderBlocks.brightnessTopLeft = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessXZNN, renderBlocks.aoBrightnessXYZNPN, renderBlocks.aoBrightnessYZPN, var22);
				renderBlocks.brightnessBottomLeft = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessYZPN, renderBlocks.aoBrightnessXZPN, renderBlocks.aoBrightnessXYZPPN, var22);
				renderBlocks.brightnessBottomRight = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessYZNN, renderBlocks.aoBrightnessXYZPNN, renderBlocks.aoBrightnessXZPN, var22);
				renderBlocks.brightnessTopRight = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessXYZNNN, renderBlocks.aoBrightnessXZNN, renderBlocks.aoBrightnessYZNN, var22);
			}
			else
			{
				var12 = renderBlocks.aoLightValueZNeg;
				var11 = renderBlocks.aoLightValueZNeg;
				var10 = renderBlocks.aoLightValueZNeg;
				var9 = renderBlocks.aoLightValueZNeg;
				renderBlocks.brightnessTopLeft = renderBlocks.brightnessBottomLeft = renderBlocks.brightnessBottomRight = renderBlocks.brightnessTopRight = var22;
			}

			renderBlocks.colorRedTopLeft = renderBlocks.colorRedBottomLeft = renderBlocks.colorRedBottomRight = renderBlocks.colorRedTopRight = (var15 ? par5 : 1.0F) * 0.8F;
			renderBlocks.colorGreenTopLeft = renderBlocks.colorGreenBottomLeft = renderBlocks.colorGreenBottomRight = renderBlocks.colorGreenTopRight = (var15 ? par6 : 1.0F) * 0.8F;
			renderBlocks.colorBlueTopLeft = renderBlocks.colorBlueBottomLeft = renderBlocks.colorBlueBottomRight = renderBlocks.colorBlueTopRight = (var15 ? par7 : 1.0F) * 0.8F;
			renderBlocks.colorRedTopLeft *= var9;
			renderBlocks.colorGreenTopLeft *= var9;
			renderBlocks.colorBlueTopLeft *= var9;
			renderBlocks.colorRedBottomLeft *= var10;
			renderBlocks.colorGreenBottomLeft *= var10;
			renderBlocks.colorBlueBottomLeft *= var10;
			renderBlocks.colorRedBottomRight *= var11;
			renderBlocks.colorGreenBottomRight *= var11;
			renderBlocks.colorBlueBottomRight *= var11;
			renderBlocks.colorRedTopRight *= var12;
			renderBlocks.colorGreenTopRight *= var12;
			renderBlocks.colorBlueTopRight *= var12;
			var27 = par1Block.getBlockTexture(renderBlocks.blockAccess, x, y, z, 2);
			this.renderEastFace(renderBlocks, par1Block, (double) x, (double) y, (double) z, var27);

			var8 = true;
		}

		if (renderBlocks.renderAllFaces || par1Block.shouldSideBeRendered(renderBlocks.blockAccess, x, y, z + 1, 3))
		{
			if (renderBlocks.aoType > 0)
			{
				if (renderBlocks.renderMaxZ >= 1.0D)
				{
					++z;
				}

				renderBlocks.aoLightValueScratchXZNP = par1Block.getAmbientOcclusionLightValue(renderBlocks.blockAccess, x - 1, y, z);
				renderBlocks.aoLightValueScratchXZPP = par1Block.getAmbientOcclusionLightValue(renderBlocks.blockAccess, x + 1, y, z);
				renderBlocks.aoLightValueScratchYZNP = par1Block.getAmbientOcclusionLightValue(renderBlocks.blockAccess, x, y - 1, z);
				renderBlocks.aoLightValueScratchYZPP = par1Block.getAmbientOcclusionLightValue(renderBlocks.blockAccess, x, y + 1, z);
				renderBlocks.aoBrightnessXZNP = par1Block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x - 1, y, z);
				renderBlocks.aoBrightnessXZPP = par1Block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x + 1, y, z);
				renderBlocks.aoBrightnessYZNP = par1Block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x, y - 1, z);
				renderBlocks.aoBrightnessYZPP = par1Block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x, y + 1, z);

				if (!renderBlocks.aoGrassXYZNCP && !renderBlocks.aoGrassXYZCNP)
				{
					renderBlocks.aoLightValueScratchXYZNNP = renderBlocks.aoLightValueScratchXZNP;
					renderBlocks.aoBrightnessXYZNNP = renderBlocks.aoBrightnessXZNP;
				}
				else
				{
					renderBlocks.aoLightValueScratchXYZNNP = par1Block.getAmbientOcclusionLightValue(renderBlocks.blockAccess, x - 1, y - 1, z);
					renderBlocks.aoBrightnessXYZNNP = par1Block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x - 1, y - 1, z);
				}

				if (!renderBlocks.aoGrassXYZNCP && !renderBlocks.aoGrassXYZCPP)
				{
					renderBlocks.aoLightValueScratchXYZNPP = renderBlocks.aoLightValueScratchXZNP;
					renderBlocks.aoBrightnessXYZNPP = renderBlocks.aoBrightnessXZNP;
				}
				else
				{
					renderBlocks.aoLightValueScratchXYZNPP = par1Block.getAmbientOcclusionLightValue(renderBlocks.blockAccess, x - 1, y + 1, z);
					renderBlocks.aoBrightnessXYZNPP = par1Block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x - 1, y + 1, z);
				}

				if (!renderBlocks.aoGrassXYZPCP && !renderBlocks.aoGrassXYZCNP)
				{
					renderBlocks.aoLightValueScratchXYZPNP = renderBlocks.aoLightValueScratchXZPP;
					renderBlocks.aoBrightnessXYZPNP = renderBlocks.aoBrightnessXZPP;
				}
				else
				{
					renderBlocks.aoLightValueScratchXYZPNP = par1Block.getAmbientOcclusionLightValue(renderBlocks.blockAccess, x + 1, y - 1, z);
					renderBlocks.aoBrightnessXYZPNP = par1Block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x + 1, y - 1, z);
				}

				if (!renderBlocks.aoGrassXYZPCP && !renderBlocks.aoGrassXYZCPP)
				{
					renderBlocks.aoLightValueScratchXYZPPP = renderBlocks.aoLightValueScratchXZPP;
					renderBlocks.aoBrightnessXYZPPP = renderBlocks.aoBrightnessXZPP;
				}
				else
				{
					renderBlocks.aoLightValueScratchXYZPPP = par1Block.getAmbientOcclusionLightValue(renderBlocks.blockAccess, x + 1, y + 1, z);
					renderBlocks.aoBrightnessXYZPPP = par1Block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x + 1, y + 1, z);
				}

				if (renderBlocks.renderMaxZ >= 1.0D)
				{
					--z;
				}

				var9 = (renderBlocks.aoLightValueScratchXZNP + renderBlocks.aoLightValueScratchXYZNPP + renderBlocks.aoLightValueZPos + renderBlocks.aoLightValueScratchYZPP) / 4.0F;
				var12 = (renderBlocks.aoLightValueZPos + renderBlocks.aoLightValueScratchYZPP + renderBlocks.aoLightValueScratchXZPP + renderBlocks.aoLightValueScratchXYZPPP) / 4.0F;
				var11 = (renderBlocks.aoLightValueScratchYZNP + renderBlocks.aoLightValueZPos + renderBlocks.aoLightValueScratchXYZPNP + renderBlocks.aoLightValueScratchXZPP) / 4.0F;
				var10 = (renderBlocks.aoLightValueScratchXYZNNP + renderBlocks.aoLightValueScratchXZNP + renderBlocks.aoLightValueScratchYZNP + renderBlocks.aoLightValueZPos) / 4.0F;
				renderBlocks.brightnessTopLeft = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessXZNP, renderBlocks.aoBrightnessXYZNPP, renderBlocks.aoBrightnessYZPP, var25);
				renderBlocks.brightnessTopRight = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessYZPP, renderBlocks.aoBrightnessXZPP, renderBlocks.aoBrightnessXYZPPP, var25);
				renderBlocks.brightnessBottomRight = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessYZNP, renderBlocks.aoBrightnessXYZPNP, renderBlocks.aoBrightnessXZPP, var25);
				renderBlocks.brightnessBottomLeft = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessXYZNNP, renderBlocks.aoBrightnessXZNP, renderBlocks.aoBrightnessYZNP, var25);
			}
			else
			{
				var12 = renderBlocks.aoLightValueZPos;
				var11 = renderBlocks.aoLightValueZPos;
				var10 = renderBlocks.aoLightValueZPos;
				var9 = renderBlocks.aoLightValueZPos;
				renderBlocks.brightnessTopLeft = renderBlocks.brightnessBottomLeft = renderBlocks.brightnessBottomRight = renderBlocks.brightnessTopRight = var25;
			}

			renderBlocks.colorRedTopLeft = renderBlocks.colorRedBottomLeft = renderBlocks.colorRedBottomRight = renderBlocks.colorRedTopRight = (var16 ? par5 : 1.0F) * 0.8F;
			renderBlocks.colorGreenTopLeft = renderBlocks.colorGreenBottomLeft = renderBlocks.colorGreenBottomRight = renderBlocks.colorGreenTopRight = (var16 ? par6 : 1.0F) * 0.8F;
			renderBlocks.colorBlueTopLeft = renderBlocks.colorBlueBottomLeft = renderBlocks.colorBlueBottomRight = renderBlocks.colorBlueTopRight = (var16 ? par7 : 1.0F) * 0.8F;
			renderBlocks.colorRedTopLeft *= var9;
			renderBlocks.colorGreenTopLeft *= var9;
			renderBlocks.colorBlueTopLeft *= var9;
			renderBlocks.colorRedBottomLeft *= var10;
			renderBlocks.colorGreenBottomLeft *= var10;
			renderBlocks.colorBlueBottomLeft *= var10;
			renderBlocks.colorRedBottomRight *= var11;
			renderBlocks.colorGreenBottomRight *= var11;
			renderBlocks.colorBlueBottomRight *= var11;
			renderBlocks.colorRedTopRight *= var12;
			renderBlocks.colorGreenTopRight *= var12;
			renderBlocks.colorBlueTopRight *= var12;
			var27 = par1Block.getBlockTexture(renderBlocks.blockAccess, x, y, z, 3);
			this.renderWestFace(renderBlocks, par1Block, (double) x, (double) y, (double) z, par1Block.getBlockTexture(renderBlocks.blockAccess, x, y, z, 3));

			if (Tessellator.instance.defaultTexture && renderBlocks.fancyGrass && var27 == 3 && renderBlocks.overrideBlockTexture < 0)
			{
				renderBlocks.colorRedTopLeft *= par5;
				renderBlocks.colorRedBottomLeft *= par5;
				renderBlocks.colorRedBottomRight *= par5;
				renderBlocks.colorRedTopRight *= par5;
				renderBlocks.colorGreenTopLeft *= par6;
				renderBlocks.colorGreenBottomLeft *= par6;
				renderBlocks.colorGreenBottomRight *= par6;
				renderBlocks.colorGreenTopRight *= par6;
				renderBlocks.colorBlueTopLeft *= par7;
				renderBlocks.colorBlueBottomLeft *= par7;
				renderBlocks.colorBlueBottomRight *= par7;
				renderBlocks.colorBlueTopRight *= par7;
				this.renderWestFace(renderBlocks, par1Block, (double) x, (double) y, (double) z, 38);
			}

			var8 = true;
		}

		if (renderBlocks.renderAllFaces || par1Block.shouldSideBeRendered(renderBlocks.blockAccess, x - 1, y, z, 4))
		{
			if (renderBlocks.aoType > 0)
			{
				if (renderBlocks.renderMinX <= 0.0D)
				{
					--x;
				}

				renderBlocks.aoLightValueScratchXYNN = par1Block.getAmbientOcclusionLightValue(renderBlocks.blockAccess, x, y - 1, z);
				renderBlocks.aoLightValueScratchXZNN = par1Block.getAmbientOcclusionLightValue(renderBlocks.blockAccess, x, y, z - 1);
				renderBlocks.aoLightValueScratchXZNP = par1Block.getAmbientOcclusionLightValue(renderBlocks.blockAccess, x, y, z + 1);
				renderBlocks.aoLightValueScratchXYNP = par1Block.getAmbientOcclusionLightValue(renderBlocks.blockAccess, x, y + 1, z);
				renderBlocks.aoBrightnessXYNN = par1Block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x, y - 1, z);
				renderBlocks.aoBrightnessXZNN = par1Block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x, y, z - 1);
				renderBlocks.aoBrightnessXZNP = par1Block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x, y, z + 1);
				renderBlocks.aoBrightnessXYNP = par1Block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x, y + 1, z);

				if (!renderBlocks.aoGrassXYZNCN && !renderBlocks.aoGrassXYZNNC)
				{
					renderBlocks.aoLightValueScratchXYZNNN = renderBlocks.aoLightValueScratchXZNN;
					renderBlocks.aoBrightnessXYZNNN = renderBlocks.aoBrightnessXZNN;
				}
				else
				{
					renderBlocks.aoLightValueScratchXYZNNN = par1Block.getAmbientOcclusionLightValue(renderBlocks.blockAccess, x, y - 1, z - 1);
					renderBlocks.aoBrightnessXYZNNN = par1Block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x, y - 1, z - 1);
				}

				if (!renderBlocks.aoGrassXYZNCP && !renderBlocks.aoGrassXYZNNC)
				{
					renderBlocks.aoLightValueScratchXYZNNP = renderBlocks.aoLightValueScratchXZNP;
					renderBlocks.aoBrightnessXYZNNP = renderBlocks.aoBrightnessXZNP;
				}
				else
				{
					renderBlocks.aoLightValueScratchXYZNNP = par1Block.getAmbientOcclusionLightValue(renderBlocks.blockAccess, x, y - 1, z + 1);
					renderBlocks.aoBrightnessXYZNNP = par1Block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x, y - 1, z + 1);
				}

				if (!renderBlocks.aoGrassXYZNCN && !renderBlocks.aoGrassXYZNPC)
				{
					renderBlocks.aoLightValueScratchXYZNPN = renderBlocks.aoLightValueScratchXZNN;
					renderBlocks.aoBrightnessXYZNPN = renderBlocks.aoBrightnessXZNN;
				}
				else
				{
					renderBlocks.aoLightValueScratchXYZNPN = par1Block.getAmbientOcclusionLightValue(renderBlocks.blockAccess, x, y + 1, z - 1);
					renderBlocks.aoBrightnessXYZNPN = par1Block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x, y + 1, z - 1);
				}

				if (!renderBlocks.aoGrassXYZNCP && !renderBlocks.aoGrassXYZNPC)
				{
					renderBlocks.aoLightValueScratchXYZNPP = renderBlocks.aoLightValueScratchXZNP;
					renderBlocks.aoBrightnessXYZNPP = renderBlocks.aoBrightnessXZNP;
				}
				else
				{
					renderBlocks.aoLightValueScratchXYZNPP = par1Block.getAmbientOcclusionLightValue(renderBlocks.blockAccess, x, y + 1, z + 1);
					renderBlocks.aoBrightnessXYZNPP = par1Block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x, y + 1, z + 1);
				}

				if (renderBlocks.renderMinX <= 0.0D)
				{
					++x;
				}

				var12 = (renderBlocks.aoLightValueScratchXYNN + renderBlocks.aoLightValueScratchXYZNNP + renderBlocks.aoLightValueXNeg + renderBlocks.aoLightValueScratchXZNP) / 4.0F;
				var9 = (renderBlocks.aoLightValueXNeg + renderBlocks.aoLightValueScratchXZNP + renderBlocks.aoLightValueScratchXYNP + renderBlocks.aoLightValueScratchXYZNPP) / 4.0F;
				var10 = (renderBlocks.aoLightValueScratchXZNN + renderBlocks.aoLightValueXNeg + renderBlocks.aoLightValueScratchXYZNPN + renderBlocks.aoLightValueScratchXYNP) / 4.0F;
				var11 = (renderBlocks.aoLightValueScratchXYZNNN + renderBlocks.aoLightValueScratchXYNN + renderBlocks.aoLightValueScratchXZNN + renderBlocks.aoLightValueXNeg) / 4.0F;
				renderBlocks.brightnessTopRight = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessXYNN, renderBlocks.aoBrightnessXYZNNP, renderBlocks.aoBrightnessXZNP, var20);
				renderBlocks.brightnessTopLeft = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessXZNP, renderBlocks.aoBrightnessXYNP, renderBlocks.aoBrightnessXYZNPP, var20);
				renderBlocks.brightnessBottomLeft = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessXZNN, renderBlocks.aoBrightnessXYZNPN, renderBlocks.aoBrightnessXYNP, var20);
				renderBlocks.brightnessBottomRight = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessXYZNNN, renderBlocks.aoBrightnessXYNN, renderBlocks.aoBrightnessXZNN, var20);
			}
			else
			{
				var12 = renderBlocks.aoLightValueXNeg;
				var11 = renderBlocks.aoLightValueXNeg;
				var10 = renderBlocks.aoLightValueXNeg;
				var9 = renderBlocks.aoLightValueXNeg;
				renderBlocks.brightnessTopLeft = renderBlocks.brightnessBottomLeft = renderBlocks.brightnessBottomRight = renderBlocks.brightnessTopRight = var20;
			}

			renderBlocks.colorRedTopLeft = renderBlocks.colorRedBottomLeft = renderBlocks.colorRedBottomRight = renderBlocks.colorRedTopRight = (var17 ? par5 : 1.0F) * 0.6F;
			renderBlocks.colorGreenTopLeft = renderBlocks.colorGreenBottomLeft = renderBlocks.colorGreenBottomRight = renderBlocks.colorGreenTopRight = (var17 ? par6 : 1.0F) * 0.6F;
			renderBlocks.colorBlueTopLeft = renderBlocks.colorBlueBottomLeft = renderBlocks.colorBlueBottomRight = renderBlocks.colorBlueTopRight = (var17 ? par7 : 1.0F) * 0.6F;
			renderBlocks.colorRedTopLeft *= var9;
			renderBlocks.colorGreenTopLeft *= var9;
			renderBlocks.colorBlueTopLeft *= var9;
			renderBlocks.colorRedBottomLeft *= var10;
			renderBlocks.colorGreenBottomLeft *= var10;
			renderBlocks.colorBlueBottomLeft *= var10;
			renderBlocks.colorRedBottomRight *= var11;
			renderBlocks.colorGreenBottomRight *= var11;
			renderBlocks.colorBlueBottomRight *= var11;
			renderBlocks.colorRedTopRight *= var12;
			renderBlocks.colorGreenTopRight *= var12;
			renderBlocks.colorBlueTopRight *= var12;
			var27 = par1Block.getBlockTexture(renderBlocks.blockAccess, x, y, z, 4);
			this.renderNorthFace(renderBlocks, par1Block, (double) x, (double) y, (double) z, var27);

			if (Tessellator.instance.defaultTexture && renderBlocks.fancyGrass && var27 == 3 && renderBlocks.overrideBlockTexture < 0)
			{
				renderBlocks.colorRedTopLeft *= par5;
				renderBlocks.colorRedBottomLeft *= par5;
				renderBlocks.colorRedBottomRight *= par5;
				renderBlocks.colorRedTopRight *= par5;
				renderBlocks.colorGreenTopLeft *= par6;
				renderBlocks.colorGreenBottomLeft *= par6;
				renderBlocks.colorGreenBottomRight *= par6;
				renderBlocks.colorGreenTopRight *= par6;
				renderBlocks.colorBlueTopLeft *= par7;
				renderBlocks.colorBlueBottomLeft *= par7;
				renderBlocks.colorBlueBottomRight *= par7;
				renderBlocks.colorBlueTopRight *= par7;
				this.renderNorthFace(renderBlocks, par1Block, (double) x, (double) y, (double) z, 38);
			}

			var8 = true;
		}

		if (renderBlocks.renderAllFaces || par1Block.shouldSideBeRendered(renderBlocks.blockAccess, x + 1, y, z, 5))
		{
			if (renderBlocks.aoType > 0)
			{
				if (renderBlocks.renderMaxX >= 1.0D)
				{
					++x;
				}

				renderBlocks.aoLightValueScratchXYPN = par1Block.getAmbientOcclusionLightValue(renderBlocks.blockAccess, x, y - 1, z);
				renderBlocks.aoLightValueScratchXZPN = par1Block.getAmbientOcclusionLightValue(renderBlocks.blockAccess, x, y, z - 1);
				renderBlocks.aoLightValueScratchXZPP = par1Block.getAmbientOcclusionLightValue(renderBlocks.blockAccess, x, y, z + 1);
				renderBlocks.aoLightValueScratchXYPP = par1Block.getAmbientOcclusionLightValue(renderBlocks.blockAccess, x, y + 1, z);
				renderBlocks.aoBrightnessXYPN = par1Block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x, y - 1, z);
				renderBlocks.aoBrightnessXZPN = par1Block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x, y, z - 1);
				renderBlocks.aoBrightnessXZPP = par1Block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x, y, z + 1);
				renderBlocks.aoBrightnessXYPP = par1Block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x, y + 1, z);

				if (!renderBlocks.aoGrassXYZPNC && !renderBlocks.aoGrassXYZPCN)
				{
					renderBlocks.aoLightValueScratchXYZPNN = renderBlocks.aoLightValueScratchXZPN;
					renderBlocks.aoBrightnessXYZPNN = renderBlocks.aoBrightnessXZPN;
				}
				else
				{
					renderBlocks.aoLightValueScratchXYZPNN = par1Block.getAmbientOcclusionLightValue(renderBlocks.blockAccess, x, y - 1, z - 1);
					renderBlocks.aoBrightnessXYZPNN = par1Block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x, y - 1, z - 1);
				}

				if (!renderBlocks.aoGrassXYZPNC && !renderBlocks.aoGrassXYZPCP)
				{
					renderBlocks.aoLightValueScratchXYZPNP = renderBlocks.aoLightValueScratchXZPP;
					renderBlocks.aoBrightnessXYZPNP = renderBlocks.aoBrightnessXZPP;
				}
				else
				{
					renderBlocks.aoLightValueScratchXYZPNP = par1Block.getAmbientOcclusionLightValue(renderBlocks.blockAccess, x, y - 1, z + 1);
					renderBlocks.aoBrightnessXYZPNP = par1Block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x, y - 1, z + 1);
				}

				if (!renderBlocks.aoGrassXYZPPC && !renderBlocks.aoGrassXYZPCN)
				{
					renderBlocks.aoLightValueScratchXYZPPN = renderBlocks.aoLightValueScratchXZPN;
					renderBlocks.aoBrightnessXYZPPN = renderBlocks.aoBrightnessXZPN;
				}
				else
				{
					renderBlocks.aoLightValueScratchXYZPPN = par1Block.getAmbientOcclusionLightValue(renderBlocks.blockAccess, x, y + 1, z - 1);
					renderBlocks.aoBrightnessXYZPPN = par1Block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x, y + 1, z - 1);
				}

				if (!renderBlocks.aoGrassXYZPPC && !renderBlocks.aoGrassXYZPCP)
				{
					renderBlocks.aoLightValueScratchXYZPPP = renderBlocks.aoLightValueScratchXZPP;
					renderBlocks.aoBrightnessXYZPPP = renderBlocks.aoBrightnessXZPP;
				}
				else
				{
					renderBlocks.aoLightValueScratchXYZPPP = par1Block.getAmbientOcclusionLightValue(renderBlocks.blockAccess, x, y + 1, z + 1);
					renderBlocks.aoBrightnessXYZPPP = par1Block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x, y + 1, z + 1);
				}

				if (renderBlocks.renderMaxX >= 1.0D)
				{
					--x;
				}

				var9 = (renderBlocks.aoLightValueScratchXYPN + renderBlocks.aoLightValueScratchXYZPNP + renderBlocks.aoLightValueXPos + renderBlocks.aoLightValueScratchXZPP) / 4.0F;
				var12 = (renderBlocks.aoLightValueXPos + renderBlocks.aoLightValueScratchXZPP + renderBlocks.aoLightValueScratchXYPP + renderBlocks.aoLightValueScratchXYZPPP) / 4.0F;
				var11 = (renderBlocks.aoLightValueScratchXZPN + renderBlocks.aoLightValueXPos + renderBlocks.aoLightValueScratchXYZPPN + renderBlocks.aoLightValueScratchXYPP) / 4.0F;
				var10 = (renderBlocks.aoLightValueScratchXYZPNN + renderBlocks.aoLightValueScratchXYPN + renderBlocks.aoLightValueScratchXZPN + renderBlocks.aoLightValueXPos) / 4.0F;
				renderBlocks.brightnessTopLeft = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessXYPN, renderBlocks.aoBrightnessXYZPNP, renderBlocks.aoBrightnessXZPP, var23);
				renderBlocks.brightnessTopRight = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessXZPP, renderBlocks.aoBrightnessXYPP, renderBlocks.aoBrightnessXYZPPP, var23);
				renderBlocks.brightnessBottomRight = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessXZPN, renderBlocks.aoBrightnessXYZPPN, renderBlocks.aoBrightnessXYPP, var23);
				renderBlocks.brightnessBottomLeft = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessXYZPNN, renderBlocks.aoBrightnessXYPN, renderBlocks.aoBrightnessXZPN, var23);
			}
			else
			{
				var12 = renderBlocks.aoLightValueXPos;
				var11 = renderBlocks.aoLightValueXPos;
				var10 = renderBlocks.aoLightValueXPos;
				var9 = renderBlocks.aoLightValueXPos;
				renderBlocks.brightnessTopLeft = renderBlocks.brightnessBottomLeft = renderBlocks.brightnessBottomRight = renderBlocks.brightnessTopRight = var23;
			}

			renderBlocks.colorRedTopLeft = renderBlocks.colorRedBottomLeft = renderBlocks.colorRedBottomRight = renderBlocks.colorRedTopRight = (var18 ? par5 : 1.0F) * 0.6F;
			renderBlocks.colorGreenTopLeft = renderBlocks.colorGreenBottomLeft = renderBlocks.colorGreenBottomRight = renderBlocks.colorGreenTopRight = (var18 ? par6 : 1.0F) * 0.6F;
			renderBlocks.colorBlueTopLeft = renderBlocks.colorBlueBottomLeft = renderBlocks.colorBlueBottomRight = renderBlocks.colorBlueTopRight = (var18 ? par7 : 1.0F) * 0.6F;
			renderBlocks.colorRedTopLeft *= var9;
			renderBlocks.colorGreenTopLeft *= var9;
			renderBlocks.colorBlueTopLeft *= var9;
			renderBlocks.colorRedBottomLeft *= var10;
			renderBlocks.colorGreenBottomLeft *= var10;
			renderBlocks.colorBlueBottomLeft *= var10;
			renderBlocks.colorRedBottomRight *= var11;
			renderBlocks.colorGreenBottomRight *= var11;
			renderBlocks.colorBlueBottomRight *= var11;
			renderBlocks.colorRedTopRight *= var12;
			renderBlocks.colorGreenTopRight *= var12;
			renderBlocks.colorBlueTopRight *= var12;
			var27 = par1Block.getBlockTexture(renderBlocks.blockAccess, x, y, z, 5);
			this.renderSouthFace(renderBlocks, par1Block, (double) x, (double) y, (double) z, var27);

			if (Tessellator.instance.defaultTexture && renderBlocks.fancyGrass && var27 == 3 && renderBlocks.overrideBlockTexture < 0)
			{
				renderBlocks.colorRedTopLeft *= par5;
				renderBlocks.colorRedBottomLeft *= par5;
				renderBlocks.colorRedBottomRight *= par5;
				renderBlocks.colorRedTopRight *= par5;
				renderBlocks.colorGreenTopLeft *= par6;
				renderBlocks.colorGreenBottomLeft *= par6;
				renderBlocks.colorGreenBottomRight *= par6;
				renderBlocks.colorGreenTopRight *= par6;
				renderBlocks.colorBlueTopLeft *= par7;
				renderBlocks.colorBlueBottomLeft *= par7;
				renderBlocks.colorBlueBottomRight *= par7;
				renderBlocks.colorBlueTopRight *= par7;
				this.renderSouthFace(renderBlocks, par1Block, (double) x, (double) y, (double) z, 38);
			}

			var8 = true;
		}

		renderBlocks.enableAO = false;
		return var8;
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
