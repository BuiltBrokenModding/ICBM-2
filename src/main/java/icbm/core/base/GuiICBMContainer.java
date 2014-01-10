package icbm.core.base;

import icbm.Reference;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

public abstract class GuiICBMContainer extends GuiContainerBase
{
    public static final ResourceLocation ICBM_EMPTY_TEXTURE = new ResourceLocation(Reference.DOMAIN, Reference.GUI_PATH + "gui_container.png");

    public GuiICBMContainer(Container container)
    {
        super(container);
        this.baseTexture = ICBM_EMPTY_TEXTURE;
    }
}
