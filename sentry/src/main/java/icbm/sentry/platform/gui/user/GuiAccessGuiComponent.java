package icbm.sentry.platform.gui.user;

import net.minecraft.client.gui.GuiButton;
import calclavia.lib.Calclavia;
import calclavia.lib.gui.GuiContainerBase;
import cpw.mods.fml.common.FMLCommonHandler;

/** @author DarkGuardsman */
public class GuiAccessGuiComponent extends GuiContainerBase
{
    private GuiButton back_button;

    private GuiUserAccess return_gui;

    public GuiAccessGuiComponent(GuiUserAccess return_gui)
    {
        super(return_gui.inventorySlots);
        this.return_gui = return_gui;
    }

    @Override
    public void initGui()
    {
        super.initGui();
        back_button = new GuiButton(0, this.guiLeft + 10, this.guiTop + 20, 50, 20, "BACK");
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
