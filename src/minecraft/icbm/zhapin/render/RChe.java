package icbm.zhapin.render;

import icbm.zhapin.cart.EChe;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.entity.RenderMinecart;
import net.minecraft.entity.item.EntityMinecart;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RChe extends RenderMinecart
{
	/**
	 * Renders the Minecart.
	 */
	protected void renderExplosiveCart(EChe minecart, float brightness, Block block, int par4)
	{
		int yinXin = minecart.yinXin;

		if (yinXin > -1 && yinXin - brightness + 1.0F < 10.0F)
		{
			float f1 = 1.0F - (yinXin - brightness + 1.0F) / 10.0F;

			if (f1 < 0.0F)
			{
				f1 = 0.0F;
			}

			if (f1 > 1.0F)
			{
				f1 = 1.0F;
			}

			f1 *= f1;
			f1 *= f1;
			float f2 = 1.0F + f1 * 0.3F;
			GL11.glScalef(f2, f2, f2);
		}

		super.func_94144_a(minecart, brightness, block, par4);

		if (minecart.isPrimed() && (yinXin > -1 && yinXin / 5 % 2 == 0))
		{
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_DST_ALPHA);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, (1.0F - (yinXin - brightness + 1.0F) / 100.0F) * 0.8F);
			GL11.glPushMatrix();
			this.field_94145_f.renderBlockAsItem(block, 0, 1.0F);
			GL11.glPopMatrix();
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
		}
	}

	@Override
	protected void func_94144_a(EntityMinecart par1EntityMinecart, float par2, Block par3Block, int par4)
	{
		this.renderExplosiveCart((EChe) par1EntityMinecart, par2, par3Block, par4);
	}
}
