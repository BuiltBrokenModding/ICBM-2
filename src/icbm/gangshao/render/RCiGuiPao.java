package icbm.gangshao.render;

import icbm.core.ZhuYaoICBM;
import icbm.gangshao.muoxing.ModelRailgun;
import icbm.gangshao.turret.mount.TCiGuiPao;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import calclavia.lib.render.RenderTaggedTile;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RCiGuiPao extends RenderTaggedTile
{
	public static final ResourceLocation TEXTURE = new ResourceLocation(ZhuYaoICBM.DOMAIN, ZhuYaoICBM.MODEL_PATH + "railgun.png");
	public static final ModelRailgun MODEL = new ModelRailgun();

	@Override
	public void renderTileEntityAt(TileEntity t, double x, double y, double z, float f)
	{
		super.renderTileEntityAt(t, x, y, z, f);

		if (t instanceof TCiGuiPao)
		{
			TCiGuiPao tileEntity = (TCiGuiPao) t;

			GL11.glPushMatrix();
			GL11.glTranslatef((float) x + 0.5F, (float) y + 2.2F, (float) z + 0.5F);
			GL11.glScalef(1.5f, 1.5f, 1.5f);
			this.bindTexture(TEXTURE);
			GL11.glRotatef(180F, 0F, 0F, 1F);
			GL11.glRotatef(180F, 0F, 1F, 0F);
			MODEL.render((float) Math.toRadians(tileEntity.currentRotationYaw), (float) Math.toRadians(tileEntity.currentRotationPitch), 0.0625F);
			GL11.glPopMatrix();
		}
	}
}