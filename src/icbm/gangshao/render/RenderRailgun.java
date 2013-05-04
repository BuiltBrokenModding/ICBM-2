package icbm.gangshao.render;

import icbm.core.ZhuYaoBase;
import icbm.gangshao.model.ModelRailgun;
import icbm.gangshao.turret.TCiGuiPao;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderRailgun extends RenderTaggedTile
{
	public static final String TEXTURE_FILE = "railgun.png";
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
			this.bindTextureByName(ZhuYaoBase.MODEL_PATH + TEXTURE_FILE);
			GL11.glRotatef(180F, 0F, 0F, 1F);
			GL11.glRotatef(180F, 0F, 1F, 0F);
			MODEL.render(tileEntity.rotationYaw, tileEntity.rotationPitch, 0.0625F);
			GL11.glPopMatrix();
		}
	}
}