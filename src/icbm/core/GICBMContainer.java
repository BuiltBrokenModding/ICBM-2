package icbm.core;

import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import calclavia.lib.gui.GuiContainerBase;

public abstract class GICBMContainer extends GuiContainerBase
{
	public static final ResourceLocation ICBM_EMPTY_TEXTURE = new ResourceLocation(ZhuYaoICBM.DOMAIN, ZhuYaoICBM.GUI_PATH + "gui_empty.png");

	public GICBMContainer(Container container)
	{
		super(container);
		this.baseTexture = ICBM_EMPTY_TEXTURE;
	}
}
