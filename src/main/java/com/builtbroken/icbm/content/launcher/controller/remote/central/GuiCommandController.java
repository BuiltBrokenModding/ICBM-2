package com.builtbroken.icbm.content.launcher.controller.remote.central;

import com.builtbroken.mc.prefab.gui.GuiContainerBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/21/2016.
 */
public class GuiCommandController extends GuiContainerBase
{
    protected GuiTextField name_field;

    long lastClickTime = 0;

    private int ticks = 0;
    private static int updateGuiTicks = 100;

    TileCommandController tile;

    public GuiCommandController(EntityPlayer player, TileCommandController controller)
    {
        super(new ContainerCommandController(player, controller));
        this.tile = controller;
    }

    @Override
    public void initGui()
    {
        super.initGui();

        int x = this.guiLeft + 20;
        int y = this.guiTop + 20;

        name_field = this.newField(x, y + 40, 100, 15, tile.getControllerDisplayName() != null ? tile.getControllerDisplayName() : "");

        this.buttonList.add(new GuiButton(0, x + 110, y + 100, 40, 20, "Save"));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        //initGui();
        if (ticks++ >= updateGuiTicks)
        {
            reloadData();
            ticks = 0;
        }
        drawString("Display Name", 10, 50);
        //drawString("Group ID", 10, 10);
    }

    public void reloadData()
    {
        if (this.name_field != null && !this.name_field.isFocused()) //TODO something tells me is should be !isFocused
        {
            this.name_field.setText(tile.getControllerDisplayName() != null ? tile.getControllerDisplayName() : "");
        }
    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        super.actionPerformed(button);
        //Prevents double click when GUI is reloaded
        if (Minecraft.getSystemTime() - lastClickTime < 2)
        {
            return;
        }
        if (button.id == 0)
        {
            tile.setConnectorDisplayName(name_field.getText() != null ? name_field.getText() : "");
        }
        lastClickTime = Minecraft.getSystemTime();
    }
}
