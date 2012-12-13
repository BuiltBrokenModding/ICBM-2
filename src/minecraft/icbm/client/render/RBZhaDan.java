package icbm.client.render;

import icbm.client.models.ICBMModelBase;
import icbm.common.zhapin.TZhaDan;
import icbm.common.zhapin.ZhaPin;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;

@SideOnly(Side.CLIENT)
public class RBZhaDan extends TileEntitySpecialRenderer
{
	@Override
	public void renderTileEntityAt(TileEntity var1, double x, double y, double z, float var8)
	{
		TZhaDan tileEntity = (TZhaDan) var1;
		Object[] data = ZhaPin.list[tileEntity.explosiveID].getRenderData();

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
