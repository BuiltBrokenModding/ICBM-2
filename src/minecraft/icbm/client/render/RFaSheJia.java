package icbm.client.render;

import icbm.api.ICBM;
import icbm.client.models.MFaSheJia;
import icbm.common.jiqi.TFaSheJia;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RFaSheJia extends TileEntitySpecialRenderer
{
	public static final MFaSheJia MODEL = new MFaSheJia();
	public static final String TEXTURE_FILE = "Launcher0.png";

	@Override
	public void renderTileEntityAt(TileEntity var1, double d, double d1, double d2, float var8)
	{
		TFaSheJia tileEntity = (TFaSheJia) var1;

		GL11.glPushMatrix();
		GL11.glTranslatef((float) d + 0.5F, (float) d1 + 1.25F, (float) d2 + 0.5F);
		GL11.glScalef(1f, 0.85f, 1f);

		String textureFile = ICBM.TEXTURE_FILE_PATH + TEXTURE_FILE;

		this.bindTextureByName(textureFile);
		GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);

		if (tileEntity.getDirection() != ForgeDirection.NORTH && tileEntity.getDirection() != ForgeDirection.SOUTH)
		{
			GL11.glRotatef(90F, 0.0F, 180F, 1.0F);
		}

		MODEL.render(0.0625F);

		GL11.glPopMatrix();
	}

}
