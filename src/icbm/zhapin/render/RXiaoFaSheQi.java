package icbm.zhapin.render;

import icbm.core.ZhuYaoBase;
import icbm.zhapin.jiqi.TXiaoFaSheQi;
import icbm.zhapin.muoxing.jiqi.MXiaoFaSheQi;
import icbm.zhapin.muoxing.jiqi.MXiaoFaSheQiJia;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RXiaoFaSheQi extends TileEntitySpecialRenderer
{
	public static final String TEXTURE_FILE = "cruise_launcher.png";
	public static final MXiaoFaSheQi MODEL0 = new MXiaoFaSheQi();
	public static final MXiaoFaSheQiJia MODEL1 = new MXiaoFaSheQiJia();

	public void renderModelAt(TXiaoFaSheQi tileEntity, double d, double d1, double d2, float f)
	{
		GL11.glPushMatrix();
		GL11.glTranslatef((float) d + 0.5F, (float) d1 + 1.5F, (float) d2 + 0.5F);
		this.bindTextureByName(ZhuYaoBase.MODEL_PATH + TEXTURE_FILE);
		GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
		MODEL0.render(0.0625F);
		GL11.glRotatef(tileEntity.rotationYaw + 90, 0F, 1F, 0F);
		GL11.glRotatef(-tileEntity.rotationPitch, 1F, 0F, 0F);
		MODEL1.render(0.0625F);
		GL11.glPopMatrix();
	}

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double d, double d1, double d2, float f)
	{
		renderModelAt((TXiaoFaSheQi) tileentity, d, d1, d2, f);
	}
}