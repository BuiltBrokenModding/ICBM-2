package com.builtbroken.icbm.content.launcher.controller.remote.connector;

import com.builtbroken.mc.client.SharedAssets;
import com.builtbroken.mc.prefab.gui.ContainerDummy;
import com.builtbroken.mc.prefab.gui.GuiContainerBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/24/2016.
 */
public class GuiCommandSiloConnector extends GuiContainerBase
{
    protected GuiTextField groupName_field;
    protected GuiTextField name_field;

    TileCommandSiloConnector tile;

    long lastClickTime = 0;

    private int ticks = 0;
    private static int updateGuiTicks = 100;

    public GuiCommandSiloConnector(EntityPlayer player, TileCommandSiloConnector tile)
    {
        super(new ContainerDummy(player, tile));
        baseTexture = SharedAssets.GUI__MC_EMPTY_FILE;
        this.tile = tile;
    }

    @Override
    public void initGui()
    {
        super.initGui();

        int x = this.guiLeft + 20;
        int y = this.guiTop + 20;

        groupName_field = this.newField(x, y, 100, 15, tile.getConnectorGroupName() != null ? tile.getConnectorGroupName() : "");
        name_field = this.newField(x, y + 40, 100, 15, tile.getConnectorDisplayName() != null ? tile.getConnectorDisplayName() : "");

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
        drawString("Group ID", 10, 10);
    }

    public void reloadData()
    {
        if (this.groupName_field != null && this.name_field != null && !this.groupName_field.isFocused() && !this.name_field.isFocused()) //TODO something tells me is should be !isFocused
        {
            this.groupName_field.setText(tile.getConnectorGroupName() != null ? tile.getConnectorGroupName() : "");
            this.name_field.setText(tile.getConnectorDisplayName() != null ? tile.getConnectorDisplayName() : "");
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
            tile.setConnectorGroupName(groupName_field.getText() != null ? groupName_field.getText() : "");
        }
        lastClickTime = Minecraft.getSystemTime();
    }
}
