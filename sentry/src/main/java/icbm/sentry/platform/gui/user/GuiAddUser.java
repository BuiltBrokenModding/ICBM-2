package icbm.sentry.platform.gui.user;


/** Gui for editing the group and access of the user. Should should the user's name, then the group
 * bellow that with a scroll buttons to cycle threw the groups. Bellow that it should show the
 * options that are locked in for the group and the options that can be added to the user alone.
 * Return button should be added near the bottom to go back to the user access page. A delete button
 * should also be included to remove the user. The user should not be able to edit there own
 * settings.
 * 
 * @author DarkGuardsman */
public class GuiAddUser extends GuiAccessGuiComponent
{
    public GuiAddUser(GuiUserAccess return_gui)
    {
        super(return_gui);
    }
}
