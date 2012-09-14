package icbm.renders;

import icbm.EntityGravityBlock;
import icbm.ICBMRenderBlocks;
import net.minecraft.src.Block;
import net.minecraft.src.Entity;
import net.minecraft.src.MathHelper;
import net.minecraft.src.Render;
import net.minecraft.src.RenderManager;
import net.minecraft.src.Tessellator;
import net.minecraft.src.World;

import org.lwjgl.opengl.GL11;

public class RenderGravityBlock extends Render
{
    private ICBMRenderBlocks renderBlocks = new ICBMRenderBlocks();
    
    public RenderGravityBlock()
    {
        this.shadowSize = 0.5F;
    }

    /**
     * The actual render method that is used in doRender
     */
    public void doRenderGravityBlock(EntityGravityBlock entity, double x, double y, double z, float par8, float par9)
    {   
    	if(this.renderManager == null)
    	{
    		this.setRenderManager(RenderManager.instance);
    	}
    	
    	GL11.glPushMatrix();
        GL11.glTranslatef((float)x, (float)y, (float)z);
        this.loadTexture(Block.blocksList[entity.blockID].getTextureFile());

        Block block = Block.blocksList[entity.blockID];
        World world = entity.worldObj;
        GL11.glDisable(GL11.GL_LIGHTING);
        
        GL11.glRotatef((float)entity.rotationPitch, 0.0F, 0.0F, 1.0F);
        GL11.glRotatef((float)entity.rotationYaw, 0.0F, 1.0F, 0.0F);
        
        if(block == Block.dragonEgg || block == Block.grass || block == Block.fence || block == Block.crops || block == Block.leaves || block == Block.torchRedstoneActive || block == Block.torchWood || block == Block.torchRedstoneIdle || block == Block.tallGrass ||  block == Block.vine || block == Block.wood || block == Block.bookShelf || block == Block.pumpkin)
        {
        	renderBlocks.blockAccess = world;
            Tessellator tessellator = Tessellator.instance;
            tessellator.startDrawingQuads();
            tessellator.setTranslation((float)(-MathHelper.floor_double(entity.posX)) - 0.5F, (float)(-MathHelper.floor_double(entity.posY)) - 0.5F, (float)(-MathHelper.floor_double(entity.posZ)) - 0.5F);
            renderBlocks.renderBlockByRenderType(block, MathHelper.floor_double(entity.posX), MathHelper.floor_double(entity.posY), MathHelper.floor_double(entity.posZ));
            tessellator.setTranslation(0.0D, 0.0D, 0.0D);
            tessellator.draw();
        }
        else
        {
            this.renderBlocks.renderBlockGravity(block, world, (int)entity.posX, (int)entity.posY, (int)entity.posZ, entity.metadata);
        }
        
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity) and this method has signature public void doRender(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
     */
    public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9)
    {
        this.doRenderGravityBlock((EntityGravityBlock)par1Entity, par2, par4, par6, par8, par9);
    }
}