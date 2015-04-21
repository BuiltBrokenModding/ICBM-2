package com.builtbroken.icbm.content.launcher.controller;

import com.builtbroken.icbm.content.launcher.TileAbstractLauncher;
import com.builtbroken.mc.core.References;
import com.builtbroken.mc.lib.helper.LanguageUtility;
import com.builtbroken.mc.prefab.gui.ContainerDummy;
import com.builtbroken.mc.prefab.gui.GuiContainerBase;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

/**
 * Created by robert on 2/20/2015.
 */
public class GuiController extends GuiContainerBase
{
    protected TileController controller;

    GuiButton[] buttons;

    protected GuiTextField x_field;
    protected GuiTextField y_field;
    protected GuiTextField z_field;
    protected String errorString = "";

    boolean editMode = false;
    int editMissile = 0;


    public GuiController(TileController launcher, EntityPlayer player)
    {
        super(new ContainerDummy(player, launcher));
        this.controller = launcher;
        this.baseTexture = References.GUI__MC_EMPTY_FILE;
    }

    @Override
    public void initGui()
    {
        super.initGui();

        int x = this.guiLeft + 20;
        int y = this.guiTop + 20;

        if (!editMode)
        {
            if (controller.launcherData != null)
            {
                for (int i = 0; i < controller.launcherData.size(); i++)
                {
                    buttons = new GuiButton[controller.launcherData.size() * 2];

                    //Launcher edit button
                    buttons[i] = new GuiButton(i, x, y, 100, 20, "Launcher " + i);
                    this.buttonList.add(buttons[i]);

                    //Fire launcher button
                    buttons[i + controller.launcherData.size()] = new GuiButton(i + controller.launcherData.size(), x + 105, y, 30, 20, "Fire");
                    if (controller.launcherData.get(i).missile == null)
                        buttons[i + controller.launcherData.size()].enabled = false;
                    this.buttonList.add(buttons[i + controller.launcherData.size()]);
                    y += 22;
                }
            }
        }
        else
        {
            this.buttonList.add(new GuiButton(0, x, y, 30, 20, "Back"));
            this.buttonList.add(new GuiButton(1, x + 100, y + 100, 30, 20, "Update"));

            x = guiLeft + 10;
            y = guiTop + 40;

            TileEntity tile = controller.launcherData.get(editMissile).location.getTileEntity();

            if(tile instanceof TileAbstractLauncher && ((TileAbstractLauncher) tile).target != null)
            {
                this.x_field = this.newField(x, y, 30, 20, "" + ((TileAbstractLauncher) tile).target.xi());
                this.y_field = this.newField(x + 35, y, 30, 20, "" + ((TileAbstractLauncher) tile).target.yi());
                this.z_field = this.newField(x + 75, y, 30, 20, "" + ((TileAbstractLauncher) tile).target.zi());
            }
        }
    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        super.actionPerformed(button);
        if (!editMode)
        {
            if (button.id >= 0 && button.id < buttons.length)
            {
                //Edit launcher button
                if (button.id < controller.launcherData.size())
                {
                    editMissile = button.id;
                    editMode = true;
                    initGui();
                }
                //Fire launcher
                else
                {
                    controller.fireLauncher(button.id - controller.launcherData.size());
                }
            }
        }
        else
        {
            if(button.id == 0)
            {
                editMissile = -1;
                editMode = false;
                initGui();
            }
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        drawStringCentered(LanguageUtility.getLocalName(controller.getInventoryName()), 85, 10);

        if (!editMode && controller.launcherData != null && controller.launcherData.size() == 0)
        {
            drawStringCentered("No linked silos", 85, 40);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY)
    {
        super.drawGuiContainerBackgroundLayer(f, mouseX, mouseY);
    }
}
