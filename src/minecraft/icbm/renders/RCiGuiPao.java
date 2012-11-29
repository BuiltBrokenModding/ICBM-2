package icbm.renders;

import icbm.ZhuYao;
import icbm.jiqi.TCiGuiPao;
import icbm.models.MCiGuiPao;
import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntitySpecialRenderer;

import org.lwjgl.opengl.GL11;

public class RCiGuiPao extends TileEntitySpecialRenderer
{
	private MCiGuiPao modelBase = new MCiGuiPao();

	public void renderAModelAt(TCiGuiPao tileEntity, double d, double d1, double d2, float f)
	{
		GL11.glPushMatrix();
		GL11.glTranslatef((float) d + 0.5F, (float) d1 + 2.2F, (float) d2 + 0.5F);
		GL11.glScalef(1.5f, 1.5f, 1.5f);
		this.bindTextureByName(ZhuYao.TEXTURE_FILE_PATH + "Railgun.png");
		GL11.glRotatef(180F, 0F, 0F, 1F);
		GL11.glRotatef(180F, 0F, 1F, 0F);
		modelBase.render(tileEntity.displayRotationYaw, tileEntity.displayRotationPitch, 0.0625F);
		GL11.glPopMatrix();
	}

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double d, double d1, double d2, float f)
	{
		renderAModelAt((TCiGuiPao) tileentity, d, d1, d2, f);
	}
}