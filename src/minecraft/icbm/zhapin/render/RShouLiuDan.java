package icbm.zhapin.render;

import icbm.zhapin.ZhuYaoZhaPin;
import icbm.zhapin.zhapin.EShouLiuDan;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.Icon;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RShouLiuDan extends Render
{
	/**
	 * Have the icon index (in items.png) that will be used to render the image. Currently, eggs and
	 * snowballs uses this classes.
	 */
	private Icon itemIconIndex;

	/**
	 * Actually renders the given argument. This is a synthetic bridge method, always casting down
	 * its argument and then handing it off to a worker function which does the actual work. In all
	 * probabilty, the class Render is generic (Render<T extends Entity) and this method has
	 * signature public void doRender(T entity, double d, double d1, double d2, float f, float f1).
	 * But JAD is pre 1.5 so doesn't do that.
	 */
	public void doRender(Entity entity, double x, double y, double z, float par8, float par9)
	{
		/**
		 * Renders the grenade based on the explosive ID.
		 */
		if (this.itemIconIndex == null)
		{
			this.itemIconIndex = ZhuYaoZhaPin.itShouLiuDan.getIconFromDamage(((EShouLiuDan) entity).haoMa);
		}

		GL11.glPushMatrix();
		GL11.glTranslatef((float) x, (float) y + 0.4f, (float) z);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glScalef(0.6F, 0.6F, 0.6F);
        this.loadTexture("/gui/items.png");
		Tessellator tessellator = Tessellator.instance;
		this.renderIcon(tessellator, this.itemIconIndex);
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
	}

	private void renderIcon(Tessellator par1Tessellator, Icon icon)
	{
		float f = icon.func_94209_e();
		float f1 = icon.func_94212_f();
		float f2 = icon.func_94206_g();
		float f3 = icon.func_94210_h();
		float f4 = 1.0F;
		float f5 = 0.5F;
		float f6 = 0.25F;
		GL11.glRotatef(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
		par1Tessellator.startDrawingQuads();
		par1Tessellator.setNormal(0.0F, 1.0F, 0.0F);
		par1Tessellator.addVertexWithUV((double) (0.0F - f5), (double) (0.0F - f6), 0.0D, (double) f, (double) f3);
		par1Tessellator.addVertexWithUV((double) (f4 - f5), (double) (0.0F - f6), 0.0D, (double) f1, (double) f3);
		par1Tessellator.addVertexWithUV((double) (f4 - f5), (double) (f4 - f6), 0.0D, (double) f1, (double) f2);
		par1Tessellator.addVertexWithUV((double) (0.0F - f5), (double) (f4 - f6), 0.0D, (double) f, (double) f2);
		par1Tessellator.draw();
	}
}
