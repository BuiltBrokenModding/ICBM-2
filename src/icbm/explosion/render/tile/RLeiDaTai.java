package icbm.explosion.render.tile;

import icbm.core.ZhuYaoICBM;
import icbm.explosion.jiqi.TLeiDaTai;
import icbm.explosion.muoxing.jiqi.MLeiDaTai;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RLeiDaTai extends TileEntitySpecialRenderer
{
	public static final ResourceLocation TEXTURE_FILE = new ResourceLocation(ZhuYaoICBM.DOMAIN, ZhuYaoICBM.MODEL_PATH + "radar.png");
	public static final ResourceLocation TEXTURE_FILE_OFF = new ResourceLocation(ZhuYaoICBM.DOMAIN, ZhuYaoICBM.MODEL_PATH + "radar_off.png");

	public static final MLeiDaTai MODEL = new MLeiDaTai();

	public void renderAModelAt(TLeiDaTai tileEntity, double x, double y, double z, float f)
	{
		GL11.glPushMatrix();
		GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);

		if (tileEntity.getEnergyStored() >= tileEntity.getRequest(null))
		{
			this.bindTexture(TEXTURE_FILE);
		}
		else
		{
			this.bindTexture(TEXTURE_FILE_OFF);
		}

		GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
		switch (tileEntity.getDirection().ordinal())
		{
			case 2:
				GL11.glRotatef(180F, 0.0F, 180F, 1.0F);
				break;
			case 4:
				GL11.glRotatef(90F, 0.0F, 180F, 1.0F);
				break;
			case 5:
				GL11.glRotatef(-90F, 0.0F, 180F, 1.0F);
				break;
		}

		MODEL.render(0.0625F, 0f, tileEntity.xuanZhuan);
		GL11.glPopMatrix();
	}

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double d, double d1, double d2, float f)
	{
		renderAModelAt((TLeiDaTai) tileentity, d, d1, d2, f);
	}
}