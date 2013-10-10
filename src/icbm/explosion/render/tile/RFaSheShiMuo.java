package icbm.explosion.render.tile;

import icbm.core.ICBMCore;
import icbm.explosion.machines.TFaSheShiMuo;
import icbm.explosion.model.tiles.MFaSheShiMuo0;
import icbm.explosion.model.tiles.MFaSheShiMuo1;
import icbm.explosion.model.tiles.MFaSheShiMuo2;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RFaSheShiMuo extends TileEntitySpecialRenderer
{
	public static final ResourceLocation TEXTURE_FILE_0 = new ResourceLocation(ICBMCore.DOMAIN, ICBMCore.MODEL_PATH + "launcher_0.png");
	public static final ResourceLocation TEXTURE_FILE_1 = new ResourceLocation(ICBMCore.DOMAIN, ICBMCore.MODEL_PATH + "launcher_1.png");
	public static final ResourceLocation TEXTURE_FILE_2 = new ResourceLocation(ICBMCore.DOMAIN, ICBMCore.MODEL_PATH + "launcher_2.png");

	public static final MFaSheShiMuo0 model0 = new MFaSheShiMuo0();
	public static final MFaSheShiMuo1 model1 = new MFaSheShiMuo1();
	public static final MFaSheShiMuo2 model2 = new MFaSheShiMuo2();

	@Override
	public void renderTileEntityAt(TileEntity var1, double x, double y, double z, float var8)
	{
		TFaSheShiMuo tileEntity = (TFaSheShiMuo) var1;

		GL11.glPushMatrix();
		GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);

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
				this.bindTexture(TEXTURE_FILE_0);
				model0.render(0.0625F);
				break;
			case 1:
				this.bindTexture(TEXTURE_FILE_1);
				model1.render(0.0625F);
				break;
			case 2:
				this.bindTexture(TEXTURE_FILE_2);
				model2.render(0.0625F);
				break;
		}

		GL11.glPopMatrix();
	}

}
