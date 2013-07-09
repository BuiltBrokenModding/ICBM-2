package icbm.zhapin.render.tile;

import icbm.zhapin.jiqi.TDianCiQi;
import icbm.zhapin.muoxing.jiqi.MDianCiQi;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RDianCiQi extends TileEntitySpecialRenderer
{
	public static final MDianCiQi MODEL = new MDianCiQi();
	public static final String TEXTURE_FILE = "emp_tower.png";

	@Override
	public void renderTileEntityAt(TileEntity t, double x, double y, double z, float f)
	{
		TDianCiQi tileEntity = (TDianCiQi) t;
		GL11.glPushMatrix();
		GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
		this.func_110628_a(TEXTURE_FILE);
		GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
		MODEL.render(tileEntity.xuanZhuan, 0.0625F);
		GL11.glPopMatrix();
	}
}