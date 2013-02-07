package icbm.client.render;

import icbm.api.ICBM;
import icbm.explosion.jiqi.TCiGuiPao;
import icbm.explosion.muoxing.jiqi.MCiGuiPao;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RCiGuiPao extends TileEntitySpecialRenderer
{
	public static final String TEXTURE_FILE = "Railgun.png";
	public static final MCiGuiPao MODEL = new MCiGuiPao();

	public void renderAModelAt(TCiGuiPao tileEntity, double d, double d1, double d2, float f)
	{
		GL11.glPushMatrix();
		GL11.glTranslatef((float) d + 0.5F, (float) d1 + 2.2F, (float) d2 + 0.5F);
		GL11.glScalef(1.5f, 1.5f, 1.5f);
		this.bindTextureByName(ICBM.TEXTURE_FILE_PATH + TEXTURE_FILE);
		GL11.glRotatef(180F, 0F, 0F, 1F);
		GL11.glRotatef(180F, 0F, 1F, 0F);
		MODEL.render(tileEntity.displayRotationYaw, tileEntity.displayRotationPitch, 0.0625F);
		GL11.glPopMatrix();
	}

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double d, double d1, double d2, float f)
	{
		renderAModelAt((TCiGuiPao) tileentity, d, d1, d2, f);
	}
}