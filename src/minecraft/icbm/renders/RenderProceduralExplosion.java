package icbm.renders;

import icbm.zhapin.EZhaPin;
import icbm.zhapin.ZhaPin;

import java.util.Random;

import net.minecraft.src.Entity;
import net.minecraft.src.Render;
import net.minecraft.src.RenderHelper;
import net.minecraft.src.RenderManager;
import net.minecraft.src.Tessellator;

import org.lwjgl.opengl.GL11;

public class RenderProceduralExplosion extends Render
{
	@Override
	public void doRender(Entity par1Entity, double x, double y, double z, float par8, float par9)
    {
		if(this.renderManager == null)
    	{
    		this.setRenderManager(RenderManager.instance);
    	}
		
		if(((EZhaPin)par1Entity).explosiveID == ZhaPin.Redmatter.getID())
		{
			Tessellator var3 = Tessellator.instance;
			float par2 = (float)(par1Entity.ticksExisted);
			
			while(par2 > 200) par2 -= 100;
			
	        RenderHelper.disableStandardItemLighting();
	        float var4 = ((float)5 + par2) / 200.0F;
	        float var5 = 0.0F;
	
	        if (var4 > 0.8F)
	        {
	            var5 = (var4 - 0.8F) / 0.2F;
	        }
	
	        Random var6 = new Random(432L);
	        
	        GL11.glPushMatrix();
	        GL11.glTranslatef((float)x + 0.5F, (float)y + 0.5F, (float)z + 0.5F);
	        GL11.glDisable(GL11.GL_TEXTURE_2D);
	        GL11.glShadeModel(GL11.GL_SMOOTH);
	        GL11.glEnable(GL11.GL_BLEND);
	        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
	        GL11.glDisable(GL11.GL_ALPHA_TEST);
	        GL11.glEnable(GL11.GL_CULL_FACE);
	        GL11.glDepthMask(false);
	        GL11.glPushMatrix();
	        GL11.glTranslatef(0.0F, -1.0F, -2.0F);
	
	        for (int var7 = 0; (float)var7 < (var4 + var4 * var4) / 2.0F * 60.0F; ++var7)
	        {
	            GL11.glRotatef(var6.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
	            GL11.glRotatef(var6.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
	            GL11.glRotatef(var6.nextFloat() * 360.0F, 0.0F, 0.0F, 1.0F);
	            GL11.glRotatef(var6.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
	            GL11.glRotatef(var6.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
	            GL11.glRotatef(var6.nextFloat() * 360.0F + var4 * 90.0F, 0.0F, 0.0F, 1.0F);
	            var3.startDrawing(6);
	            float var8 = var6.nextFloat() * 20.0F + 5.0F + var5 * 10.0F;
	            float var9 = var6.nextFloat() * 2.0F + 1.0F + var5 * 2.0F;
	            var3.setColorRGBA_I(16777215, (int)(255.0F * (1.0F - var5)));
	            var3.addVertex(0.0D, 0.0D, 0.0D);
	            var3.setColorRGBA_I(0, 0);
	            var3.addVertex(-0.866D * (double)var9, (double)var8, (double)(-0.5F * var9));
	            var3.addVertex(0.866D * (double)var9, (double)var8, (double)(-0.5F * var9));
	            var3.addVertex(0.0D, (double)var8, (double)(1.0F * var9));
	            var3.addVertex(-0.866D * (double)var9, (double)var8, (double)(-0.5F * var9));
	            var3.draw();
	        }
	
	        GL11.glPopMatrix();
	        GL11.glDepthMask(true);
	        GL11.glDisable(GL11.GL_CULL_FACE);
	        GL11.glDisable(GL11.GL_BLEND);
	        GL11.glShadeModel(GL11.GL_FLAT);
	        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	        GL11.glEnable(GL11.GL_TEXTURE_2D);
	        GL11.glEnable(GL11.GL_ALPHA_TEST);
	        RenderHelper.enableStandardItemLighting();
	        GL11.glPopMatrix();                  
		}
    }

}
