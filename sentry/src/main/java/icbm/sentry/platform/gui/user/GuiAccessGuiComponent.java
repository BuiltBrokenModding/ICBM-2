package icbm.sentry.platform.gui.user;

import net.minecraft.client.gui.GuiButton;
import resonant.lib.References;
import resonant.lib.gui.GuiContainerBase;
import cpw.mods.fml.common.FMLCommonHandler;

/** @author DarkGuardsman */
public class GuiAccessGuiComponent extends GuiContainerBase
{
    protected GuiButton back_button;

    protected GuiUserAccess return_gui;

    public GuiAccessGuiComponent(GuiUserAccess return_gui)
    {
        super(return_gui.inventorySlots);
        this.return_gui = return_gui;
        this.baseTexture = References.GUI_EMPTY_FILE;
    }

    @Override
    public void initGui()
    {
        super.initGui();
        back_button = new GuiButtonNextPage(0, this.guiLeft + 10, this.guiTop + 20, false);
        this.buttonList.add(back_button);
    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        super.actionPerformed(button);
        if (button == back_button)
        {
            FMLCommonHandler.instance().showGuiScreen(return_gui);
        }
    }
}
