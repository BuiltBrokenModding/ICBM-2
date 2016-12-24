package com.builtbroken.icbm.content.crafting.station.small.auto;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.client.Assets;
import com.builtbroken.mc.client.SharedAssets;
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
public class GuiSMAutoCraft extends GuiContainerBase
{
    public static final int CRAFTING_WINDOW_BUTTON_ID = 1;
    public static final int WARHEAD_WINDOW_BUTTON_ID = 2;
    public static final int GUIDANCE_WINDOW_BUTTON_ID = 3;
    public static final int ENGINE_WINDOW_BUTTON_ID = 4;
    public static final int AUTO_CRAFT_WINDOW_BUTTON_ID = 5;

    public static final int AUTO_CRAFT_TOGGLE_BUTTON_ID = 12;
    public static final int WARHEAD_TOGGLE_BUTTON_ID = 13;
    public static final int ENGINE_TOGGLE_BUTTON_ID = 14;
    public static final int GUIDANCE_TOGGLE_BUTTON_ID = 15;

    private static final ResourceLocation guiTexture0 = new ResourceLocation(ICBM.DOMAIN, References.GUI_DIRECTORY + "missile.small.workstation.png");

    private final TileSMAutoCraftClient tile;

    private final int id;

    //Main GUI
    private GuiButton2 craftButton;

    //Automation GUI
    private GuiButton2 autoCraftButton;
    private GuiButton2 requireWarheadButton;
    private GuiButton2 requireGuidanceButton;
    private GuiButton2 requireEngineButton;

    //Tabs on left of GUI
    private GuiImageButton craftingWindowButton;
    private GuiImageButton warheadWindowButton;
    private GuiImageButton guidanceWindowButton;
    private GuiImageButton engineWindowButton;
    private GuiImageButton autocraftingButton;

    public GuiSMAutoCraft(EntityPlayer player, TileSMAutoCraftClient tile, int id)
    {
        super(new ContainerSMAutoCraft(player, tile, id));
        this.tile = tile;
        this.id = id;
        if (id == 0)
        {
            baseTexture = guiTexture0;
        }
        else
        {
            this.baseTexture = SharedAssets.GUI__MC_EMPTY_FILE;
        }
    }

    @Override
    public void initGui()
    {
        super.initGui();
        craftingWindowButton = addButton(GuiImageButton.newButton18(CRAFTING_WINDOW_BUTTON_ID, guiLeft - 18, guiTop + 5, 0, 0).setTexture(Assets.GUI_BUTTONS));
        warheadWindowButton = addButton(GuiImageButton.newButton18(WARHEAD_WINDOW_BUTTON_ID, guiLeft - 18, guiTop + 5 + 19, 6, 0).setTexture(Assets.GUI_BUTTONS));
        guidanceWindowButton = addButton(GuiImageButton.newButton18(GUIDANCE_WINDOW_BUTTON_ID, guiLeft - 18, guiTop + 5 + 19 * 2, 7, 0).setTexture(Assets.GUI_BUTTONS));
        engineWindowButton = addButton(GuiImageButton.newButton18(ENGINE_WINDOW_BUTTON_ID, guiLeft - 18, guiTop + 5 + 19 * 3, 8, 0).setTexture(Assets.GUI_BUTTONS));
        autocraftingButton = addButton(GuiImageButton.newButton18(AUTO_CRAFT_WINDOW_BUTTON_ID, guiLeft - 18, guiTop + 5 + 19 * 4, 2, 0).setTexture(Assets.GUI_BUTTONS));

        //Disable buttons that go to this GUI instead of a new GUI
        switch (id)
        {
            case 0:
                craftButton = addButton(new GuiButton2(0, guiLeft + 80, guiTop + 23, 50, 20, "Craft"));
                craftingWindowButton.disable();
                break;
            case 1:
                warheadWindowButton.disable();
                break;
            case 2:
                guidanceWindowButton.disable();
                break;
            case 3:
                engineWindowButton.disable();
                break;
            case 4:
                final int left = 105;
                autoCraftButton = addButton(new GuiButton2(AUTO_CRAFT_TOGGLE_BUTTON_ID, guiLeft + left, guiTop + 20, 20, 20, tile.isAutocrafting ? "[x]" : "[ ]"));
                requireWarheadButton = addButton(new GuiButton2(WARHEAD_TOGGLE_BUTTON_ID, guiLeft + left, guiTop + 42, 20, 20, tile.requiresWarhead ? "[x]" : "[ ]"));
                requireGuidanceButton = addButton(new GuiButton2(GUIDANCE_TOGGLE_BUTTON_ID, guiLeft + left, guiTop + 64, 20, 20, tile.requiresGuidance ? "[x]" : "[ ]"));
                requireEngineButton = addButton(new GuiButton2(ENGINE_TOGGLE_BUTTON_ID, guiLeft + left, guiTop + 86, 20, 20, tile.requiresEngine ? "[x]" : "[ ]"));
                autocraftingButton.disable();
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
                drawString("Missile Workstation", 50, 7);
                //drawString("" + tile.explosiveStackSizeRequired, 104, 62); TODO
                break;
            case 1:
                drawString("Warhead Configuration", 33, 7);
                drawString("Not implemented yet", 33, 30);
                break;
            case 2:
                drawString("Guidance Configuration", 33, 7);
                drawString("Not implemented yet", 33, 30);
                break;
            case 3:
                drawString("Engine Configuration", 33, 7);
                drawString("Not implemented yet", 33, 30);
                break;
            case 4:
                drawString("Automation & Crafting Settings", 10, 7);
                drawString("Enable Automation:", 12, 26);
                drawString("Require Warhead:", 12, 47);
                drawString("Require Guidance:", 12, 69);
                drawString("Require Engine:", 12, 90);
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
        else if (buttonId > 0 && buttonId <= 5 && buttonId - 1 != id)
        {
            tile.sendPacketToServer(new PacketTile(tile, 2, buttonId - 1));
        }
        else if (buttonId == AUTO_CRAFT_TOGGLE_BUTTON_ID)
        {
            tile.isAutocrafting = !tile.isAutocrafting;
            autoCraftButton.displayString = tile.isAutocrafting ? "[x]" : "[ ]";
            tile.sendGUIDataUpdate();
        }
        else if (buttonId == WARHEAD_TOGGLE_BUTTON_ID)
        {
            tile.requiresWarhead = !tile.requiresWarhead;
            requireWarheadButton.displayString = tile.requiresWarhead ? "[x]" : "[ ]";
            tile.sendGUIDataUpdate();
        }
        else if (buttonId == GUIDANCE_TOGGLE_BUTTON_ID)
        {
            tile.requiresGuidance = !tile.requiresGuidance;
            requireGuidanceButton.displayString = tile.requiresGuidance ? "[x]" : "[ ]";
            tile.sendGUIDataUpdate();
        }
        else if (buttonId == ENGINE_TOGGLE_BUTTON_ID)
        {
            tile.requiresEngine = !tile.requiresEngine;
            requireEngineButton.displayString = tile.requiresEngine ? "[x]" : "[ ]";
            tile.sendGUIDataUpdate();
        }
    }
}
