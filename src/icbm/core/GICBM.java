package icbm.core;

import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import calclavia.lib.gui.GuiBase;
import cpw.mods.fml.client.FMLClientHandler;

public abstract class GICBM extends GuiBase
{
	public static final ResourceLocation TEXTURE = new ResourceLocation(ZhuYaoICBM.DOMAIN, ZhuYaoICBM.GUI_PATH + "gui_empty.png");

	private int containerWidth;
	private int containerHeight;

	@Override
	protected void drawBackgroundLayer(int var2, int var3, float var1)
	{
		FMLClientHandler.instance().getClient().renderEngine.func_110577_a(TEXTURE);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		this.containerWidth = (this.width - this.xSize) / 2;
		this.containerHeight = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(containerWidth, containerHeight, 0, 0, this.xSize, this.ySize);
	}

}
