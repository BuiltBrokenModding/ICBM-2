package icbm.sentry.platform.gui.user;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import calclavia.lib.prefab.terminal.TileTerminal;
import cpw.mods.fml.common.FMLCommonHandler;

/** @author DarkGuardsman */
public class GuiAddUser extends GuiAccessGuiComponent
{
    private GuiTextField username_field;
    private GuiTextField group_field;
    private GuiButton addUser_button;
    private GuiButton clear_button;

    private String error_lineOne = "";
    private String error_lineTwo = "";

    public GuiAddUser(GuiUserAccess return_gui)
    {
        super(return_gui);
    }

    @Override
    public void initGui()
    {
        super.initGui();
        this.username_field = new GuiTextField(fontRenderer, 30, 60, 120, 20);
        this.group_field = new GuiTextField(fontRenderer, 30, 100, 120, 20);

        addUser_button = new GuiButton(0, this.guiLeft + 40, this.guiTop + 150, 50, 20, "ADD");
        this.buttonList.add(addUser_button);

        clear_button = new GuiButton(1, this.guiLeft + 95, this.guiTop + 150, 50, 20, "CLEAR");
        this.buttonList.add(clear_button);

    }

    @Override
    public void keyTyped(char par1, int par2)
    {
        if (!username_field.isFocused() && !group_field.isFocused())
            super.keyTyped(par1, par2);
        else
        {
            this.username_field.textboxKeyTyped(par1, par2);
            this.group_field.textboxKeyTyped(par1, par2);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        super.actionPerformed(button);
        if (button == addUser_button)
        {
            if (return_gui.tileEntity instanceof TileTerminal)
            {
                if (username_field.getText() == null || username_field.getText().isEmpty())
                {
                    this.error_lineOne = "Field Error:";
                    this.error_lineTwo = "  Username field is empty";
                    return;
                }
                if (group_field.getText() == null || group_field.getText().isEmpty())
                {
                    this.error_lineOne = "Field Error:";
                    this.error_lineTwo = "  Group field is empty";
                    return;
                }
                String command = "users add " + group_field.getText() + " " + username_field.getText();
                ((TileTerminal) return_gui.tileEntity).sendCommandToServer(return_gui.player, command);
                FMLCommonHandler.instance().showGuiScreen(return_gui);
            }
        }
        else if (button == clear_button)
        {
            this.username_field.setText("");
            this.group_field.setText("");
        }
    }

    @Override
    public void mouseClicked(int par1, int par2, int par3)
    {
        super.mouseClicked(par1, par2, par3);
        this.username_field.mouseClicked(par1 - containerWidth, par2 - containerHeight, par3);
        this.group_field.mouseClicked(par1 - containerWidth, par2 - containerHeight, par3);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y)
    {
        super.drawGuiContainerForegroundLayer(x, y);
        this.fontRenderer.drawString("New User Dialog", 52, 6, 4210752);

        this.fontRenderer.drawString("Username:", 30, 50, 4210752);
        this.username_field.drawTextBox();

        this.fontRenderer.drawString("Group:", 30, 90, 4210752);
        this.group_field.drawTextBox();

        this.fontRenderer.drawString("*" + this.error_lineOne, 30, 130, 4210752);
        this.fontRenderer.drawString("*" + this.error_lineTwo, 30, 140, 4210752);
    }
}
