package icbm.sentry.platform.gui.user;

import calclavia.lib.access.AccessUser;

/** @author DarkGuardsman */
public class GuiEditUser extends GuiAccessGuiComponent
{
    private AccessUser user;

    public GuiEditUser(GuiUserAccess return_gui, AccessUser user)
    {
        super(return_gui);
        this.user = user;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y)
    {
        super.drawGuiContainerForegroundLayer(x, y);
        this.fontRenderer.drawString("\u00a77" + (user != null ? user.getName() : "Error"), (int) (this.containerWidth / 2 - 7 * 2.5), 4, 4210752);
    }
}
