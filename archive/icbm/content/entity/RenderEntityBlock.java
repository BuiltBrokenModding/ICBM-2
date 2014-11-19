package icbm.content.entity;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import resonant.lib.render.RenderUtility;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderEntityBlock extends Render
{
    public RenderEntityBlock()
    {
        this.shadowSize = 0.5F;
    }

    /** The actual render method that is used in doRender */
    public void doRenderGravityBlock(EntityFlyingBlock entity, double x, double y, double z, float par8, float par9)
    {
        GL11.glPushMatrix();
        GL11.glTranslatef((float) x, (float) y, (float) z);
        RenderUtility.setTerrainTexture();

        Block block = entity.mimicBlock;
        World world = entity.worldObj;
        GL11.glDisable(GL11.GL_LIGHTING);

        GL11.glRotatef(entity.rotationPitch, 0.0F, 0.0F, 1.0F);
        GL11.glRotatef(entity.rotationYaw, 0.0F, 1.0F, 0.0F);

        this.field_147909_c.blockAccess = world;

        if (block == Blocks.dragon_egg || block == Blocks.grass || block == Blocks.fence || block == Blocks.wheat || block == Blocks.leaves || block == Blocks.redstone_torch || block == Blocks.torch || block == Blocks.redstone_torch || block == Blocks.tallgrass || block == Blocks.vine || block == Blocks.planks || block == Blocks.bookshelf || block == Blocks.pumpkin)
        {
            Tessellator tessellator = Tessellator.instance;
            tessellator.startDrawingQuads();
            tessellator.setTranslation((-MathHelper.floor_double(entity.posX)) - 0.5F, (-MathHelper.floor_double(entity.posY)) - 0.5F, (-MathHelper.floor_double(entity.posZ)) - 0.5F);
            this.field_147909_c.renderBlockByRenderType(block, MathHelper.floor_double(entity.posX), MathHelper.floor_double(entity.posY), MathHelper.floor_double(entity.posZ));
            tessellator.setTranslation(0.0D, 0.0D, 0.0D);
            tessellator.draw();
        }
        else
        {
            this.renderBlockGravity(block, entity.metadata, this.field_147909_c);
        }

        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();
    }

    public void renderBlockGravity(Block block, int metadata, RenderBlocks renderer)
    {
        float var6 = 0.5F;
        float var7 = 1.0F;
        float var8 = 0.8F;
        float var9 = 0.6F;
        Tessellator tess = Tessellator.instance;
        tess.startDrawingQuads();

        float var12 = 1.0F;

        tess.setColorOpaque_F(var6 * var12, var6 * var12, var6 * var12);
        renderer.renderFaceYNeg(block, -0.5D, -0.5D, -0.5D, renderer.getBlockIconFromSideAndMetadata(block, 0, metadata));

        tess.setColorOpaque_F(var7 * var12, var7 * var12, var7 * var12);
        renderer.renderFaceYPos(block, -0.5D, -0.5D, -0.5D, renderer.getBlockIconFromSideAndMetadata(block, 1, metadata));

        tess.setColorOpaque_F(var8 * var12, var8 * var12, var8 * var12);
        renderer.renderFaceZNeg(block, -0.5D, -0.5D, -0.5D, renderer.getBlockIconFromSideAndMetadata(block, 2, metadata));

        tess.setColorOpaque_F(var8 * var12, var8 * var12, var8 * var12);
        renderer.renderFaceZPos(block, -0.5D, -0.5D, -0.5D, renderer.getBlockIconFromSideAndMetadata(block, 3, metadata));

        tess.setColorOpaque_F(var9 * var12, var9 * var12, var9 * var12);
        renderer.renderFaceXNeg(block, -0.5D, -0.5D, -0.5D, renderer.getBlockIconFromSideAndMetadata(block, 4, metadata));

        tess.setColorOpaque_F(var9 * var12, var9 * var12, var9 * var12);
        renderer.renderFaceXPos(block, -0.5D, -0.5D, -0.5D, renderer.getBlockIconFromSideAndMetadata(block, 5, metadata));
        tess.draw();
    }

    /** Actually renders the given argument. This is a synthetic bridge method, always casting down
     * its argument and then handing it off to a worker function which does the actual work. In all
     * probabilty, the class Render is generic (Render<T extends Entity) and this method has
     * signature public void doRender(T entity, double d, double d1, double d2, float f, float f1).
     * But JAD is pre 1.5 so doesn't do that. */
    @Override
    public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9)
    {
        this.doRenderGravityBlock((EntityFlyingBlock) par1Entity, par2, par4, par6, par8, par9);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity)
    {
        return null;
    }
}