package icbm.explosion.render;

import icbm.api.ICBM;
import icbm.explosion.jiqi.TLeiDaTai;
import icbm.explosion.muoxing.jiqi.MLeiDa;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RLeiDaTai extends TileEntitySpecialRenderer
{
	public static final MLeiDa MODEL = new MLeiDa();
	public static final String TEXTURE_FILE = "Radar.png";

	public void renderAModelAt(TLeiDaTai tileEntity, double x, double y, double z, float f)
	{
		GL11.glPushMatrix();
		GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
		this.bindTextureByName(ICBM.TEXTURE_FILE_PATH + TEXTURE_FILE);
		GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
		MODEL.render(tileEntity.xuanZhuan, 0.0625F);
		GL11.glPopMatrix();
	}

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double d, double d1, double d2, float f)
	{
		renderAModelAt((TLeiDaTai) tileentity, d, d1, d2, f);
	}
}