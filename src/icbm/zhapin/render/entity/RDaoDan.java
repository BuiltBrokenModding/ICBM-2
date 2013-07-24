package icbm.zhapin.render.entity;

import icbm.zhapin.zhapin.daodan.DaoDan;
import icbm.zhapin.zhapin.daodan.EDaoDan;
import icbm.zhapin.zhapin.daodan.EDaoDan.XingShi;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RDaoDan extends Render
{
	public RDaoDan(float f)
	{
		this.shadowSize = f;
	}

	@Override
	public void doRender(Entity entity, double x, double y, double z, float f, float f1)
	{
		EDaoDan entityMissile = (EDaoDan) entity;

		if (entityMissile.getExplosiveType() instanceof DaoDan)
		{
			DaoDan daoDan = (DaoDan) entityMissile.getExplosiveType();

			GL11.glPushMatrix();
			GL11.glTranslated(x, y, z);
			GL11.glRotatef(entityMissile.prevRotationYaw + (entityMissile.rotationYaw - entityMissile.prevRotationYaw) * f1 - 90.0F, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(entityMissile.prevRotationPitch + (entityMissile.rotationPitch - entityMissile.prevRotationPitch) * f1 + 90.0F, 0.0F, 0.0F, 1.0F);

			if (entityMissile.xingShi == XingShi.XIAO_DAN)
			{
				GL11.glScalef(0.5f, 0.5f, 0.5f);
			}

			FMLClientHandler.instance().getClient().renderEngine.func_110577_a(daoDan.getMissileResource());
			daoDan.getMissileModel().render(entityMissile, (float) x, (float) y, (float) z, f, f1, 0.0625F);

			GL11.glPopMatrix();
		}
	}

	@Override
	protected ResourceLocation func_110775_a(Entity entity)
	{
		return null;
	}
}