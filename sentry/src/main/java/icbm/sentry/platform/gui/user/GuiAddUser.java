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

    public GuiAddUser(GuiUserAccess return_gui)
    {
        super(return_gui);
    }

    @Override
    public void initGui()
    {
        super.initGui();
        this.username_field = new GuiTextField(fontRenderer, 20, 60, 100, 20);
        this.group_field = new GuiTextField(fontRenderer, 20, 80, 100, 20);

        addUser_button = new GuiButton(0, this.guiLeft + 10, this.guiTop + 120, 50, 20, "ADD");
        this.buttonList.add(addUser_button);

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
                ((TileTerminal) return_gui.tileEntity).sendCommandToServer(return_gui.player, "users add " + group_field.getText() + " " + username_field.getText());
                FMLCommonHandler.instance().showGuiScreen(return_gui);
            }

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
        this.fontRenderer.drawString("\u00a77Add New User", 52, 6, 4210752);
        this.username_field.drawTextBox();
        this.group_field.drawTextBox();
    }
}
