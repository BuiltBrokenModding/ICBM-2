package icbm.core;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RBICBM extends RenderBlocks
{
	public RBICBM(IBlockAccess iBlockAccess)
	{
		super(iBlockAccess);
	}

	public void renderBlockGravity(Block par1Block, World par2World, int par3, int par4, int par5, int metadata)
	{
		float var6 = 0.5F;
		float var7 = 1.0F;
		float var8 = 0.8F;
		float var9 = 0.6F;
		Tessellator tess = Tessellator.instance;
		tess.startDrawingQuads();

		float var12 = 1.0F;

		tess.setColorOpaque_F(var6 * var12, var6 * var12, var6 * var12);
		this.renderFaceYNeg(par1Block, -0.5D, -0.5D, -0.5D, par1Block.getIcon(0, metadata));

		tess.setColorOpaque_F(var7 * var12, var7 * var12, var7 * var12);
		this.renderFaceYPos(par1Block, -0.5D, -0.5D, -0.5D, par1Block.getIcon(1, metadata));

		tess.setColorOpaque_F(var8 * var12, var8 * var12, var8 * var12);
		this.renderFaceZNeg(par1Block, -0.5D, -0.5D, -0.5D, par1Block.getIcon(2, metadata));

		tess.setColorOpaque_F(var8 * var12, var8 * var12, var8 * var12);
		this.renderFaceZPos(par1Block, -0.5D, -0.5D, -0.5D, par1Block.getIcon(3, metadata));

		tess.setColorOpaque_F(var9 * var12, var9 * var12, var9 * var12);
		this.renderFaceXNeg(par1Block, -0.5D, -0.5D, -0.5D, par1Block.getIcon(4, metadata));

		tess.setColorOpaque_F(var9 * var12, var9 * var12, var9 * var12);
		this.renderFaceXPos(par1Block, -0.5D, -0.5D, -0.5D, par1Block.getIcon(5, metadata));
		tess.draw();
	}
}
