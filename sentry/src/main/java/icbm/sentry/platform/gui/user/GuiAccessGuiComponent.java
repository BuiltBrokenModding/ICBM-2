package icbm.sentry.platform.gui.user;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.client.gui.GuiButton;
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
public class GuiAccessGuiComponent extends GuiICBMContainer
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
        if(button == back_button)
        {
            FMLCommonHandler.instance().showGuiScreen(return_gui);
        }
    }
}
