package icbm.explosion.render;

import icbm.core.muoxing.ICBMModelBase;
import icbm.explosion.zhapin.EZhaPin;
import icbm.explosion.zhapin.ZhaPin;

import java.util.Random;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RZhaPin extends Render
{
	public Random random = new Random();

	@Override
	public void doRender(Entity par1Entity, double x, double y, double z, float par8, float par9)
	{
		EZhaPin eZhaPin = (EZhaPin) par1Entity;

		if (eZhaPin.explosiveID == ZhaPin.hongSu.getID())
		{
			Tessellator tessellator = Tessellator.instance;

			/**
			 * Sphere
			 * 
			 * GL11.glPushMatrix(); GL11.glTranslatef((float) x, (float) y, (float) z);
			 * 
			 * GL11.glScalef(5f, 5f, 5f); GL11.glColor4f(0.0F, 0.0F, 0.0F, 1.0F);
			 * 
			 * this.loadTexture(ICBM.TEXTURE_FILE_PATH + "black_circle.png");
			 * 
			 * float var4 = (float)(0 % 16 * 16 + 0) / 256.0F; float var5 = (float)(0 % 16 * 16 +
			 * 16) / 256.0F; float var6 = (float)(0 / 16 * 16 + 0) / 256.0F; float var7 = (float)(0
			 * / 16 * 16 + 16) / 256.0F; float var8 = 1.0F; float var9 = 0.5F; float var10 = 0.25F;
			 * 
			 * GL11.glPushMatrix();
			 * 
			 * GL11.glRotatef(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
			 * tessellator.startDrawingQuads(); tessellator.setNormal(0.0F, 1.0F, 0.0F);
			 * tessellator.addVertexWithUV((double) (0.0F - var9), (double) (0.0F - var10), 0.0D,
			 * (double) var4, (double) var7); tessellator.addVertexWithUV((double) (var8 - var9),
			 * (double) (0.0F - var10), 0.0D, (double) var5, (double) var7);
			 * tessellator.addVertexWithUV((double) (var8 - var9), (double) (1.0F - var10), 0.0D,
			 * (double) var5, (double) var6); tessellator.addVertexWithUV((double) (0.0F - var9),
			 * (double) (1.0F - var10), 0.0D, (double) var4, (double) var6); tessellator.draw();
			 * GL11.glPopMatrix();
			 * 
			 * GL11.glPopMatrix();
			 */

			/**
			 * Enderdragon Light
			 */
			float par2 = (float) (par1Entity.ticksExisted);

			while (par2 > 200)
				par2 -= 100;

			RenderHelper.disableStandardItemLighting();
			float var41 = ((float) 5 + par2) / 200.0F;
			float var51 = 0.0F;

			if (var41 > 0.8F)
			{
				var51 = (var41 - 0.8F) / 0.2F;
			}

			Random rand = new Random(432L);

			GL11.glPushMatrix();
			GL11.glTranslatef((float) x + 0.5F, (float) y + 0.5F, (float) z + 0.5F);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glShadeModel(GL11.GL_SMOOTH);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
			GL11.glDisable(GL11.GL_ALPHA_TEST);
			GL11.glEnable(GL11.GL_CULL_FACE);
			GL11.glDepthMask(false);
			GL11.glPushMatrix();
			GL11.glTranslatef(0.0F, -1.0F, -2.0F);

			for (int i1 = 0; (float) i1 < (var41 + var41 * var41) / 2.0F * 60.0F; ++i1)
			{
				GL11.glRotatef(rand.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(rand.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
				GL11.glRotatef(rand.nextFloat() * 360.0F, 0.0F, 0.0F, 1.0F);
				GL11.glRotatef(rand.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(rand.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
				GL11.glRotatef(rand.nextFloat() * 360.0F + var41 * 90.0F, 0.0F, 0.0F, 1.0F);
				tessellator.startDrawing(6);
				float var81 = rand.nextFloat() * 20.0F + 5.0F + var51 * 10.0F;
				float var91 = rand.nextFloat() * 2.0F + 1.0F + var51 * 2.0F;
				tessellator.setColorRGBA_I(16777215, (int) (255.0F * (1.0F - var51)));
				tessellator.addVertex(0.0D, 0.0D, 0.0D);
				tessellator.setColorRGBA_I(0, 0);
				tessellator.addVertex(-0.866D * (double) var91, (double) var81, (double) (-0.5F * var91));
				tessellator.addVertex(0.866D * (double) var91, (double) var81, (double) (-0.5F * var91));
				tessellator.addVertex(0.0D, (double) var81, (double) (1.0F * var91));
				tessellator.addVertex(-0.866D * (double) var91, (double) var81, (double) (-0.5F * var91));
				tessellator.draw();
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
		else
		{
			Object[] data = ZhaPin.list[eZhaPin.explosiveID].getRenderData();

			if (data != null)
			{
				GL11.glPushMatrix();
				GL11.glTranslatef((float) x, (float) y + 1F, (float) z);
				GL11.glRotatef(eZhaPin.rotationPitch, 0.0F, 0.0F, 1.0F);
				this.loadTexture((String) data[1]);
				((ICBMModelBase) data[0]).render(eZhaPin, (float) x, (float) y, (float) z, par8, par9, 0.0625F);
				GL11.glPopMatrix();
			}
		}
	}

	public void drawCircle(double x, double y, double radius, double accuracy)
	{
		GL11.glDisable(GL11.GL_TEXTURE_2D);

		double da = Math.min((2.0 * Math.asin(1.0 / radius) / accuracy), 10000);

		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
		GL11.glVertex2d(x, y);

		for (double a = 0.0; a <= 2 * Math.PI; a += da)
		{
			GL11.glVertex2d(x + Math.cos(a) * radius, y + Math.sin(a) * radius);
		}

		GL11.glVertex2d(x + radius, y);
		GL11.glEnd();
	}

}
