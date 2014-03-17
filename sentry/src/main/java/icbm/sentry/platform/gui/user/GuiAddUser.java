package icbm.sentry.platform.gui.user;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;

/** @author DarkGuardsman */
public class GuiAddUser extends GuiAccessGuiComponent
{
    private GuiTextField username_field;
    private GuiButton addUser_button;

    public GuiAddUser(GuiUserAccess return_gui)
    {
        super(return_gui);
    }

    @Override
    public void initGui()
    {
        super.initGui();
        this.username_field = new GuiTextField(fontRenderer, 20, 60, 100, 20);

        addUser_button = new GuiButton(0, this.guiLeft + 10, this.guiTop + 80, 50, 20, "ADD");
        this.buttonList.add(addUser_button);

    }

    @Override
    public void keyTyped(char par1, int par2)
    {
        if (!username_field.isFocused())
            super.keyTyped(par1, par2);
        else
            this.username_field.textboxKeyTyped(par1, par2);
    }

    @Override
    public void mouseClicked(int par1, int par2, int par3)
    {
        super.mouseClicked(par1, par2, par3);
        this.username_field.mouseClicked(par1 - containerWidth, par2 - containerHeight, par3);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y)
    {
        super.drawGuiContainerForegroundLayer(x, y);
        this.fontRenderer.drawString("\u00a77Add New User", 52, 6, 4210752);
        this.username_field.drawTextBox();
    }
}
