package icbm.client.render;

import icbm.core.muoxing.ICBMModelBase;
import icbm.explosion.zhapin.TZhaDan;
import icbm.explosion.zhapin.ZhaPin;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RZhaDan extends TileEntitySpecialRenderer
{
	@Override
	public void renderTileEntityAt(TileEntity var1, double x, double y, double z, float var8)
	{
		TZhaDan tileEntity = (TZhaDan) var1;
		Object[] data = ZhaPin.list[tileEntity.haoMa].getRenderData();

		if (data != null)
		{
			GL11.glPushMatrix();
			GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
			this.bindTextureByName((String) data[1]);
			GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
			((ICBMModelBase) data[0]).render(0.0625F);
			GL11.glPopMatrix();
		}
	}

}
