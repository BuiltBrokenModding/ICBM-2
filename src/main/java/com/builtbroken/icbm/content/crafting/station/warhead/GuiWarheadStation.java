package com.builtbroken.icbm.content.crafting.station.warhead;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.mc.core.References;
import com.builtbroken.mc.core.network.packet.PacketTile;
import com.builtbroken.mc.prefab.gui.GuiButton2;
import com.builtbroken.mc.prefab.gui.GuiContainerBase;
import com.builtbroken.mc.prefab.gui.buttons.GuiImageButton;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;

/**
 * GUI for the warhead crafting station
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/13/2016.
 */
public class GuiWarheadStation extends GuiContainerBase
{
    private static final ResourceLocation guiTexture0 = new ResourceLocation(ICBM.DOMAIN, References.GUI_DIRECTORY + "warhead.workstation.0.png");
    private static final ResourceLocation guiTexture1 = new ResourceLocation(ICBM.DOMAIN, References.GUI_DIRECTORY + "warhead.workstation.1.png");
    private static final ResourceLocation guiTexture2 = new ResourceLocation(ICBM.DOMAIN, References.GUI_DIRECTORY + "warhead.workstation.2.png");
    private static final ResourceLocation guiTexture3 = new ResourceLocation(ICBM.DOMAIN, References.GUI_DIRECTORY + "warhead.workstation.3.png");

    private final TileWarheadStationClient tile;

    private final int id;

    private GuiButton2 craftButton;
    private GuiImageButton craftingWindowButton;
    private GuiImageButton explosiveWindowButton;
    private GuiImageButton triggerWindowButton;
    private GuiImageButton otherWindowButton;

    public GuiWarheadStation(EntityPlayer player, TileWarheadStationClient tile, int id)
    {
        super(new ContainerWarheadStation(player, tile, id));
        this.tile = tile;
        this.id = id;
        switch (id)
        {
            case 0:
                baseTexture = guiTexture0;
                break;
            case 1:
                baseTexture = guiTexture1;
                break;
            case 2:
                baseTexture = guiTexture2;
                break;
            case 3:
                baseTexture = guiTexture3;
                break;
        }
    }

    @Override
    public void initGui()
    {
        super.initGui();
        craftingWindowButton = addButton(GuiImageButton.newRefreshButton(1, guiLeft - 18, guiTop + 5));
        explosiveWindowButton = addButton(GuiImageButton.newRefreshButton(2, guiLeft - 18, guiTop + 5 + 19));
        triggerWindowButton = addButton(GuiImageButton.newRefreshButton(3, guiLeft - 18, guiTop + 5 + 19 * 2));
        otherWindowButton = addButton(GuiImageButton.newRefreshButton(4, guiLeft - 18, guiTop + 5 + 19 * 3));

        //Disable buttons that go to this GUI instead of a new GUI
        switch (id)
        {
            case 0:
                craftButton = addButton(new GuiButton2(0, guiLeft + 80, guiTop + 23, 50, 20, "Craft"));
                craftingWindowButton.disable();
                break;
            case 1:
                explosiveWindowButton.disable();
                break;
            case 2:
                triggerWindowButton.disable();
                break;
            case 3:
                otherWindowButton.disable();
                break;
        }
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();
        if (id == 0)
        {
            if (tile.canCraft())
            {
                craftButton.enable();
            }
            else
            {
                craftButton.disable();
            }
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
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        switch (id)
        {
            case 0:
                drawString("Warhead Workstation", 50, 7);
                break;
            case 1:
                drawString("Explosive Configuration", 33, 7);
                break;
            case 2:
                drawString("Trigger Configuration", 33, 7);
                break;
            case 3:
                drawString("Extras", 33, 7);
                break;
        }
    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        final int buttonId = button.id;
        if (buttonId == 0)
        {
            tile.sendCraftingPacket();
        }
        else if (buttonId > 0 && buttonId < 5 && buttonId - 1 != id)
        {
            tile.sendPacketToServer(new PacketTile(tile, 2, buttonId - 1));
        }
    }
}
