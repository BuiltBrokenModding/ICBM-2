package com.builtbroken.icbm.content.crafting.station.warhead;

import com.builtbroken.mc.prefab.gui.GuiButton2;
import com.builtbroken.mc.prefab.gui.GuiContainerBase;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/13/2016.
 */
public class GuiWarheadStation extends GuiContainerBase
{
    private TileWarheadStationClient tile;
    private GuiButton2 craftButton;

    public GuiWarheadStation(EntityPlayer player, TileWarheadStationClient tile)
    {
        super(new ContainerWarheadStation(player, tile));
        this.tile = tile;
    }

    @Override
    public void initGui()
    {
        super.initGui();
        craftButton = new GuiButton2(0, guiLeft + 120, guiTop + 50, 50, 20, "Craft");
        buttonList.add(craftButton);
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();
        if (tile.canCraft())
        {
            craftButton.enable();
        }
        else
        {
            craftButton.disable();
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY)
    {
        super.drawGuiContainerBackgroundLayer(f, mouseX, mouseY);
        for (Object object : inventorySlots.inventorySlots)
        {
            drawSlot((Slot) object);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        if (button.id == 0)
        {
            tile.sendCraftingPacket();
        }
    }
}
