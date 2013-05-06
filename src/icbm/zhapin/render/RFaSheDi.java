package icbm.zhapin.render;

import icbm.core.ZhuYaoBase;
import icbm.zhapin.jiqi.TFaSheDi;
import icbm.zhapin.muoxing.jiqi.MFaSheDi0;
import icbm.zhapin.muoxing.jiqi.MFaSheDi1;
import icbm.zhapin.muoxing.jiqi.MFaSheDi2;
import icbm.zhapin.muoxing.jiqi.MFaSheDiRail0;
import icbm.zhapin.muoxing.jiqi.MFaSheDiRail1;
import icbm.zhapin.muoxing.jiqi.MFaSheDiRail2;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

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
	public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float f)
	{
		TFaSheDi tileEntity = (TFaSheDi) tileentity;

		GL11.glPushMatrix();
		GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);

		String textureFile = ZhuYaoBase.MODEL_PATH + "launcher_" + tileEntity.getTier() + ".png";

		this.bindTextureByName(textureFile);
		GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);

		if (tileEntity.getDirection(tileEntity.worldObj, (int) x, (int) y, (int) z) != ForgeDirection.NORTH && tileEntity.getDirection(tileEntity.worldObj, (int) x, (int) y, (int) z) != ForgeDirection.SOUTH)
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