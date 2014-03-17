package icbm.sentry.platform.gui.user;

import icbm.core.prefab.render.GuiICBMContainer;
import calclavia.lib.access.AccessUser;

/** Gui for editing the group and access of the user. Should should the user's name, then the group
 * bellow that with a scroll buttons to cycle threw the groups. Bellow that it should show the
 * options that are locked in for the group and the options that can be added to the user alone.
 * Return button should be added near the bottom to go back to the user access page. A delete button
 * should also be included to remove the user. The user should not be able to edit there own
 * settings.
 * 
 * @author DarkGuardsman */
public class GuiAddUser extends GuiICBMContainer
{
    private GuiUserAccess return_gui;

    public GuiAddUser(GuiUserAccess return_gui)
    {
        super(return_gui.inventorySlots);
        this.return_gui = return_gui;

    }

    @Override
    public void initGui()
    {
        super.initGui();
        //TODO implement back button to return user to calling gui
    }

    @Override
    public void onGuiClosed()
    {
        super.onGuiClosed();
        //FMLCommonHandler.instance().showGuiScreen(return_gui);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y)
    {
        //this.fontRenderer.drawString("\u00a77" + user.getName(), (int) (this.containerWidth / 2 - 7 * 2.5), 4, 4210752);
    }
}
