package com.builtbroken.icbm.content.crafting.station;

import com.builtbroken.jlib.data.Colors;
import com.builtbroken.mc.core.network.packet.PacketTile;
import com.builtbroken.mc.prefab.gui.GuiContainerBase;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Created by robert on 3/12/2015.
 */
public class GuiMissileWorkstation extends GuiContainerBase
{
    public String error_msg = "------";

    private TileMissileWorkstation tile;
    private GuiButton action_button;
    private GuiButton mode_button;
    private GuiButton auto_button;

    public GuiMissileWorkstation(EntityPlayer player, TileMissileWorkstation tile)
    {
        super(new ContainerMissileWorkstation(player, tile));
        this.tile = tile;
    }

    @Override
    public void initGui()
    {
        super.initGui();
        action_button = new GuiButton(0, guiLeft + 0, guiTop + 60, 30, 15, "Assemble");
        auto_button = new GuiButton(1, guiLeft + 100, guiTop + 20, 50, 20, "Auto");
        mode_button = new GuiButton(2, guiLeft + 60, guiTop + 20, 50, 20, "Mode");

        buttonList.add(auto_button);
        buttonList.add(mode_button);
        buttonList.add(action_button);
    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        if (button == action_button)
        {
            tile.sendPacketToServer(new PacketTile(tile, 4));
        }
        else if (button.id == 1)
        {
            if (tile.automated)
            {
                auto_button.displayString = "Auto";
            }
            else
            {
                auto_button.displayString = "Manual";
            }
            tile.setAutomated(!tile.automated);
        }
        else if (button.id == 2)
        {
            if (tile.assemble)
            {
                action_button.displayString = "Disassemble";
            }
            else
            {
                action_button.displayString = "Assemble";
            }
            tile.setAssemble(!tile.assemble);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        drawStringCentered(error_msg, 60, 70, Colors.RED.color());
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY)
    {
        super.drawGuiContainerBackgroundLayer(f, mouseX, mouseY);

        //Draw slots
        for (int i = 0; i < 5; i++)
            this.drawSlot(inventorySlots.getSlot(i));
    }
}
