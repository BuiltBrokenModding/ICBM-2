package com.builtbroken.icbm.content.launcher.controller;

import com.builtbroken.mc.core.References;
import com.builtbroken.mc.lib.helper.LanguageUtility;
import com.builtbroken.mc.prefab.gui.ContainerDummy;
import com.builtbroken.mc.prefab.gui.GuiContainerBase;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.input.Keyboard;

/**
 * Created by robert on 2/20/2015.
 */
public class GuiController extends GuiContainerBase
{
    protected TileController controller;

    GuiButton[] buttons;

    int page = 0;


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
        if(controller.launcherData != null)
        {
            for(int i = 0; i < controller.launcherData.size(); i++)
            {
                buttons = new GuiButton[controller.launcherData.size() * 2];
                buttons[i] = new GuiButton(i, x, y, 100, 20, "Launcher " + i);
                this.buttonList.add(buttons[i]);
                buttons[i + controller.launcherData.size()] = new GuiButton(i + controller.launcherData.size(), x + 105, y, 30, 20, "Fire");
                this.buttonList.add(buttons[i + controller.launcherData.size()]);
                y += 22;
            }
        }
    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        super.actionPerformed(button);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        drawStringCentered(LanguageUtility.getLocalName(controller.getInventoryName()), 85, 10);


    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY)
    {
        super.drawGuiContainerBackgroundLayer(f, mouseX, mouseY);
    }
}
