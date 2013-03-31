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
	@Override
	public void doRender(Entity entity, double x, double y, double z, float par8, float par9)
	{
		/**
		 * Renders the grenade based on the explosive ID.
		 */
		Icon icon = ZhuYaoZhaPin.itShouLiuDan.getIconFromDamage(((EShouLiuDan) entity).haoMa);

		GL11.glPushMatrix();
		GL11.glTranslatef((float) x, (float) y + 0.4f, (float) z);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glScalef(0.6F, 0.6F, 0.6F);
		this.loadTexture("/gui/items.png");
		Tessellator tessellator = Tessellator.instance;
		this.renderIcon(tessellator, icon);
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
	}

	private void renderIcon(Tessellator par1Tessellator, Icon icon)
	{
		float f = icon.getMinU();
		float f1 = icon.getMaxU();
		float f2 = icon.getMinV();
		float f3 = icon.getMaxV();
		float f4 = 1.0F;
		float f5 = 0.5F;
		float f6 = 0.25F;
		GL11.glRotatef(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
		par1Tessellator.startDrawingQuads();
		par1Tessellator.setNormal(0.0F, 1.0F, 0.0F);
		par1Tessellator.addVertexWithUV(0.0F - f5, 0.0F - f6, 0.0D, f, f3);
		par1Tessellator.addVertexWithUV(f4 - f5, 0.0F - f6, 0.0D, f1, f3);
		par1Tessellator.addVertexWithUV(f4 - f5, f4 - f6, 0.0D, f1, f2);
		par1Tessellator.addVertexWithUV(0.0F - f5, f4 - f6, 0.0D, f, f2);
		par1Tessellator.draw();
	}
}
