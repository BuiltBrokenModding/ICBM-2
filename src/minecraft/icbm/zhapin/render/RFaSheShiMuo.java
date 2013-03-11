package icbm.zhapin.render;

import icbm.core.ZhuYao;
import icbm.zhapin.jiqi.TFaSheShiMuo;
import icbm.zhapin.muoxing.jiqi.MFaSheShiMuo0;
import icbm.zhapin.muoxing.jiqi.MFaSheShiMuo1;
import icbm.zhapin.muoxing.jiqi.MFaSheShiMuo2;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RFaSheShiMuo extends TileEntitySpecialRenderer
{
	public static final MFaSheShiMuo0 model0 = new MFaSheShiMuo0();
	public static final MFaSheShiMuo1 model1 = new MFaSheShiMuo1();
	public static final MFaSheShiMuo2 model2 = new MFaSheShiMuo2();

	@Override
	public void renderTileEntityAt(TileEntity var1, double x, double y, double z, float var8)
	{
		TFaSheShiMuo tileEntity = (TFaSheShiMuo) var1;

		GL11.glPushMatrix();
		GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);

		String textureFile = ZhuYao.TEXTURE_PATH + "Launcher" + tileEntity.getTier() + ".png";

		this.bindTextureByName(textureFile);
		GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);

		switch (tileEntity.getDirection(tileEntity.worldObj, (int) x, (int) y, (int) z).ordinal())
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

		switch (tileEntity.getTier())
		{
			case 0:
				model0.render(0.0625F);
				break;
			case 1:
				model1.render(0.0625F);
				break;
			case 2:
				model2.render(0.0625F);
				break;
		}

		GL11.glPopMatrix();
	}

}
