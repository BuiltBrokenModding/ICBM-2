package icbm.explosion.render;

import icbm.api.ICBM;
import icbm.explosion.jiqi.TFaSheShiMuo;
import icbm.explosion.muoxing.jiqi.MFaSheShiMuo0;
import icbm.explosion.muoxing.jiqi.MFaSheShiMuo1;
import icbm.explosion.muoxing.jiqi.MFaSheShiMuo2;
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
	public void renderTileEntityAt(TileEntity var1, double d, double d1, double d2, float var8)
	{
		TFaSheShiMuo tileEntity = (TFaSheShiMuo) var1;

		GL11.glPushMatrix();
		GL11.glTranslatef((float) d + 0.5F, (float) d1 + 1.5F, (float) d2 + 0.5F);

		String textureFile = ICBM.TEXTURE_FILE_PATH + "Launcher" + tileEntity.getTier() + ".png";

		this.bindTextureByName(textureFile);
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
