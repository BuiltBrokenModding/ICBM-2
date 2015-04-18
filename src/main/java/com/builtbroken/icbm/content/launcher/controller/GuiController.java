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

        int i = 0;
        for(LauncherData data : controller.launcherData)
        {
            i++;
            drawString("#" + i + " " + data.location, 85, 10 + i * 11);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY)
    {
        super.drawGuiContainerBackgroundLayer(f, mouseX, mouseY);
    }
}
