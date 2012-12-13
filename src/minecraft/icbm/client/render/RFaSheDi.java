package icbm.client.render;

import icbm.ZhuYao;
import icbm.client.models.MFaSheDi0;
import icbm.client.models.MFaSheDi1;
import icbm.client.models.MFaSheDi2;
import icbm.client.models.MFaSheDiRail0;
import icbm.client.models.MFaSheDiRail1;
import icbm.client.models.MFaSheDiRail2;
import icbm.jiqi.TFaSheDi;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
@SideOnly(Side.CLIENT)
public class RFaSheDi extends TileEntitySpecialRenderer
{
	public static final MFaSheDi0 modelBase0 = new MFaSheDi0();
	public static final MFaSheDiRail0 modelRail0 = new MFaSheDiRail0();

	public static final MFaSheDi1 modelBase1 = new MFaSheDi1();
	public static final MFaSheDiRail1 modelRail1 = new MFaSheDiRail1();

	public static final MFaSheDi2 modelBase2 = new MFaSheDi2();
	public static final MFaSheDiRail2 modelRail2 = new MFaSheDiRail2();

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double d, double d1, double d2, float f)
	{
		TFaSheDi tileEntity = (TFaSheDi) tileentity;

		GL11.glPushMatrix();
		GL11.glTranslatef((float) d + 0.5F, (float) d1 + 1.5F, (float) d2 + 0.5F);

		String textureFile = ZhuYao.TEXTURE_FILE_PATH + "Launcher" + tileEntity.getTier() + ".png";

		this.bindTextureByName(textureFile);
		GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);

		if (tileEntity.getDirection() != ForgeDirection.NORTH && tileEntity.getDirection() != ForgeDirection.SOUTH)
		{
			GL11.glRotatef(90F, 0F, 180F, 1.0F);
		}

		// The missile launcher screen
		if (tileEntity.getTier() == 0)
		{
			modelBase0.render(0.0625F);
			modelRail0.render(0.0625F);
		}
		else if (tileEntity.getTier() == 1)
		{
			modelBase1.render(0.0625F);
			modelRail1.render(0.0625F);
			GL11.glRotatef(180F, 0F, 180F, 1.0F);
			modelRail1.render(0.0625F);
		}
		else if (tileEntity.getTier() == 2)
		{
			modelBase2.render(0.0625F);
			modelRail2.render(0.0625F);
			GL11.glRotatef(180F, 0F, 180F, 1.0F);
			modelRail2.render(0.0625F);
		}

		GL11.glPopMatrix();
	}
}