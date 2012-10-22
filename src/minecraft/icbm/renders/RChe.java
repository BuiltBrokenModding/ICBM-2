package icbm.renders;

import icbm.ZhuYao;
import icbm.api.ICBM;
import icbm.cart.EChe;
import net.minecraft.src.Block;
import net.minecraft.src.Entity;
import net.minecraft.src.MathHelper;
import net.minecraft.src.ModelBase;
import net.minecraft.src.ModelMinecart;
import net.minecraft.src.Render;
import net.minecraft.src.RenderBlocks;
import net.minecraft.src.Vec3;

import org.lwjgl.opengl.GL11;

public class RChe extends Render
{
    private RenderBlocks blockRenderer = new RenderBlocks();

    /** instance of ModelMinecart for rendering */
    protected ModelBase modelMinecart;

    public RChe()
    {
        this.shadowSize = 0.5F;
        this.modelMinecart = new ModelMinecart();
    }

    /**
     * Renders the Minecart.
     */
    public void renderTheMinecart(EChe minecart, double x, double y, double z, float par8, float par9)
    {
        GL11.glPushMatrix();
        long var10 = (long)minecart.entityId * 493286711L;
        var10 = var10 * var10 * 4392167121L + var10 * 98761L;
        float var12 = (((float)(var10 >> 16 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
        float var13 = (((float)(var10 >> 20 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
        float var14 = (((float)(var10 >> 24 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
        GL11.glTranslatef(var12, var13, var14);
        double var15 = minecart.lastTickPosX + (minecart.posX - minecart.lastTickPosX) * (double)par9;
        double var17 = minecart.lastTickPosY + (minecart.posY - minecart.lastTickPosY) * (double)par9;
        double var19 = minecart.lastTickPosZ + (minecart.posZ - minecart.lastTickPosZ) * (double)par9;
        double var21 = 0.30000001192092896D;
        Vec3 var23 = minecart.func_70489_a(var15, var17, var19);
        float var24 = minecart.prevRotationPitch + (minecart.rotationPitch - minecart.prevRotationPitch) * par9;

        if (var23 != null)
        {
            Vec3 var25 = minecart.func_70495_a(var15, var17, var19, var21);
            Vec3 var26 = minecart.func_70495_a(var15, var17, var19, -var21);

            if (var25 == null)
            {
                var25 = var23;
            }

            if (var26 == null)
            {
                var26 = var23;
            }

            x += var23.xCoord - var15;
            y += (var25.yCoord + var26.yCoord) / 2.0D - var17;
            z += var23.zCoord - var19;
            Vec3 var27 = var26.addVector(-var25.xCoord, -var25.yCoord, -var25.zCoord);

            if (var27.lengthVector() != 0.0D)
            {
                var27 = var27.normalize();
                par8 = (float)(Math.atan2(var27.zCoord, var27.xCoord) * 180.0D / Math.PI);
                var24 = (float)(Math.atan(var27.yCoord) * 73.0D);
            }
        }

        GL11.glTranslatef((float)x, (float)y, (float)z);
        GL11.glRotatef(180.0F - par8, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-var24, 0.0F, 0.0F, 1.0F);
        float var28 = (float)minecart.func_70496_j() - par9;
        float var30 = (float)minecart.getDamage() - par9;

        if (var30 < 0.0F)
        {
            var30 = 0.0F;
        }

        if (var28 > 0.0F)
        {
            GL11.glRotatef(MathHelper.sin(var28) * var28 * var30 / 10.0F * (float)minecart.func_70493_k(), 1.0F, 0.0F, 0.0F);
        }
        
        float var29 = 0.75F;
        GL11.glScalef(var29, var29, var29);
    	
    	GL11.glPushMatrix();
        GL11.glTranslatef(0.0F, 0.3125F, 0.0F);
        float var101;

        if (minecart.isPrimed() && (float)minecart.fuse - par9 + 1.0F < 10.0F)
        {
            var101 = 1.0F - ((float)minecart.fuse - par9 + 1.0F) / 10.0F;

            if (var101 < 0.0F)
            {
                var101 = 0.0F;
            }

            if (var101 > 1.0F)
            {
                var101 = 1.0F;
            }

            var101 *= var101;
            var101 *= var101;
            float var11 = 1.0F + var101 * 0.3F;
            GL11.glScalef(var11, var11, var11);
        }

        var101 = (1.0F - ((float)minecart.fuse - par9 + 1.0F) / 100.0F) * 0.8F;
        this.loadTexture(ICBM.BLOCK_TEXTURE_FILE);
        this.blockRenderer.renderBlockAsItem(ZhuYao.bZha4Dan4, minecart.explosiveID, minecart.getBrightness(par9));
        
        if (minecart.isPrimed() &&  minecart.fuse / 5 % 2 == 0)
        {
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_DST_ALPHA);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, var101);
            this.blockRenderer.renderBlockAsItem(Block.tnt, 0, 1.0F);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
        }

        GL11.glPopMatrix();

        GL11.glScalef(1.0F / var29, 1.0F / var29, 1.0F / var29);
        
        this.loadTexture("/item/cart.png");
        GL11.glScalef(-1.0F, -1.0F, 1.0F);
        this.modelMinecart.render(minecart, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
        GL11.glPopMatrix();
    }
    
    public void renderTNTCart(EChe cart, float light, float time)
    {
        GL11.glPushMatrix();
        loadTexture("/terrain.png");
        GL11.glTranslatef(0.0F, 0.3125F, 0.0F);
        GL11.glRotatef(90F, 0.0F, 1.0F, 0.0F);
        
        EChe tnt = (EChe)cart;
        
        if(tnt.isPrimed() && ((float)tnt.getFuse() - time) + 1.0F < 10F)
        {
            float scale = 1.0F - (((float)tnt.getFuse() - time) + 1.0F) / 10F;
            if(scale < 0.0F) {
                scale = 0.0F;
            }
            if(scale > 1.0F) {
                scale = 1.0F;
            }
            scale *= scale;
            scale *= scale;
            scale = 1.0F + scale * 0.3F;
            GL11.glScalef(scale, scale, scale);
        }
        (new RenderBlocks()).renderBlockAsItem(Block.tnt, 0, light);
        if(tnt.isPrimed() && (tnt.getFuse() / 5) % 2 == 0) {
            GL11.glDisable(3553);
            GL11.glDisable(2896);
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 772);
            float alpha = (1.0F - (((float)tnt.getFuse() - time) + 1.0F) / 100F) * 0.8F;
            GL11.glColor4f(1.0F, 1.0F, 1.0F, alpha);
            renderBlocks.renderBlockAsItem(Block.tnt, 0, 1.0F);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glDisable(3042);
            GL11.glEnable(2896);
            GL11.glEnable(3553);
        }
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
        this.renderTheMinecart((EChe)par1Entity, par2, par4, par6, par8, par9);
    }
}
