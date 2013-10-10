package icbm.core.base;

import icbm.core.ICBMCore;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import calclavia.lib.gui.GuiBase;
import cpw.mods.fml.client.FMLClientHandler;

public abstract class GuiICBM extends GuiBase
{
	public static final ResourceLocation TEXTURE = new ResourceLocation(ICBMCore.DOMAIN, ICBMCore.GUI_PATH + "gui_empty.png");

	private int containerWidth;
	private int containerHeight;

	@Override
	protected void drawBackgroundLayer(int var2, int var3, float var1)
	{
		FMLClientHandler.instance().getClient().renderEngine.bindTexture(TEXTURE);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		this.containerWidth = (this.width - this.xSize) / 2;
		this.containerHeight = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(containerWidth, containerHeight, 0, 0, this.xSize, this.ySize);
	}

}
