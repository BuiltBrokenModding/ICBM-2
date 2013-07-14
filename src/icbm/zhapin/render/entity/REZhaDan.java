package icbm.zhapin.render.entity;

import icbm.zhapin.ZhuYaoZhaPin;
import icbm.zhapin.zhapin.EZhaDan;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import calclavia.lib.render.CalclaviaRenderHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class REZhaDan extends Render {
	private RenderBlocks blockRenderer = new RenderBlocks();

	public REZhaDan() {
		this.shadowSize = 0.5F;
	}

	@Override
	public void doRender(Entity par1Entity, double x, double y, double z,
			float par8, float par9) {
		EZhaDan entityExplosive = (EZhaDan) par1Entity;

		GL11.glPushMatrix();
		GL11.glTranslatef((float) x, (float) y, (float) z);
		float f2;

		if (entityExplosive.fuse - par9 + 1.0F < 10.0F) {
			f2 = 1.0F - (entityExplosive.fuse - par9 + 1.0F) / 10.0F;

			if (f2 < 0.0F) {
				f2 = 0.0F;
			}

			if (f2 > 1.0F) {
				f2 = 1.0F;
			}

			f2 *= f2;
			f2 *= f2;
			float f3 = 1.0F + f2 * 0.3F;
			GL11.glScalef(f3, f3, f3);
		}

		f2 = (1.0F - (entityExplosive.fuse - par9 + 1.0F) / 100.0F) * 0.8F;
		CalclaviaRenderHelper.setTerrainTexture();
		this.blockRenderer.renderBlockAsItem(ZhuYaoZhaPin.bZhaDan,
				entityExplosive.haoMa, entityExplosive.getBrightness(par9));

		if (entityExplosive.fuse / 5 % 2 == 0) {
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_DST_ALPHA);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, f2);
			this.blockRenderer.renderBlockAsItem(ZhuYaoZhaPin.bZhaDan,
					entityExplosive.haoMa, 1.0F);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
		}

		GL11.glPopMatrix();
	}

	@Override
	protected ResourceLocation func_110775_a(Entity entity) {
		return null;
	}

}
