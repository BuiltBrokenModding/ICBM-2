package com.builtbroken.icbm.content.crafting.station.warhead;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.api.modules.IWarhead;
import com.builtbroken.icbm.api.warhead.ITrigger;
import com.builtbroken.icbm.api.warhead.ITriggerAccepter;
import com.builtbroken.icbm.client.Assets;
import com.builtbroken.mc.api.modules.IModule;
import com.builtbroken.mc.api.modules.IModuleItem;
import com.builtbroken.mc.core.References;
import com.builtbroken.mc.prefab.gui.GuiButton2;
import com.builtbroken.mc.prefab.gui.GuiContainerBase;
import com.builtbroken.mc.prefab.gui.buttons.GuiButton9px;
import com.builtbroken.mc.prefab.gui.buttons.GuiImageButton;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 * GUI for the warhead crafting station
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/13/2016.
 */
public class GuiWarheadStation extends GuiContainerBase<TileWarheadStation>
{
    private static final ResourceLocation guiTexture0 = new ResourceLocation(ICBM.DOMAIN, References.GUI_DIRECTORY + "warhead.workstation.0.png");
    private static final ResourceLocation guiTexture1 = new ResourceLocation(ICBM.DOMAIN, References.GUI_DIRECTORY + "warhead.workstation.1.png");
    private static final ResourceLocation guiTexture2 = new ResourceLocation(ICBM.DOMAIN, References.GUI_DIRECTORY + "warhead.workstation.2.png");
    private static final ResourceLocation guiTexture3 = new ResourceLocation(ICBM.DOMAIN, References.GUI_DIRECTORY + "warhead.workstation.3.png");

    private final int id;

    //Main GUI
    private GuiButton2 craftButton;

    //Automation GUI
    private GuiButton2 autoCraftButton;
    private GuiButton2 requireTriggerButton;
    private GuiButton2 requireExplosiveButton;

    //Tabs on left of GUI
    private GuiImageButton craftingWindowButton;
    private GuiImageButton explosiveWindowButton;
    private GuiImageButton triggerWindowButton;
    private GuiImageButton autocraftingButton;

    public GuiWarheadStation(EntityPlayer player, TileWarheadStation host, int id)
    {
        super(new ContainerWarheadStation(player, host, id), host);
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
            case 4:
                baseTexture = guiTexture3;
                break;
        }
    }

    @Override
    public void initGui()
    {
        super.initGui();
        craftingWindowButton = addButton(GuiImageButton.newButton18(1, guiLeft - 18, guiTop + 5, 0, 0).setTexture(Assets.GUI_BUTTONS));
        explosiveWindowButton = addButton(GuiImageButton.newButton18(2, guiLeft - 18, guiTop + 5 + 19, 1, 0).setTexture(Assets.GUI_BUTTONS));
        triggerWindowButton = addButton(GuiImageButton.newButton18(3, guiLeft - 18, guiTop + 5 + 19 * 2, 3, 0).setTexture(Assets.GUI_BUTTONS));
        autocraftingButton = addButton(GuiImageButton.newButton18(4, guiLeft - 18, guiTop + 5 + 19 * 3, 2, 0).setTexture(Assets.GUI_BUTTONS));

        //Disable buttons that go to this GUI instead of a new GUI
        switch (id)
        {
            case 0:
                craftButton = addButton(new GuiButton2(0, guiLeft + 80, guiTop + 23, 50, 20, "Craft"));
                buttonList.add(GuiButton9px.newMinusButton(11, guiLeft + 102, guiTop + 70));
                buttonList.add(GuiButton9px.newPlusButton(10, guiLeft + 102, guiTop + 52));
                craftingWindowButton.disable();
                break;
            case 1:
                explosiveWindowButton.disable();
                break;
            case 2:
                triggerWindowButton.disable();
                break;
            case 3:
                autoCraftButton = addButton(new GuiButton2(12, guiLeft + 12, guiTop + 20, 120, 20, host.isAutocrafting ? "Disable Autocrafting" : "Enable Autocrafting"));
                requireTriggerButton = addButton(new GuiButton2(13, guiLeft + 105, guiTop + 42, 20, 20, host.requireTrigger ? "[X]" : "[ ]"));
                requireExplosiveButton = addButton(new GuiButton2(14, guiLeft + 105, guiTop + 64, 20, 20, host.requireExplosive ? "[X]" : "[ ]"));
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
            if (host.canCraft())
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
                drawString("Explosives to use:", 5, 62);
                drawString("" + host.explosiveStackSizeRequired, 104, 62);
                break;
            case 1:
                drawString("Explosive Configuration", 33, 7);
                drawString("Not implemented yet", 33, 30);
                break;
            case 2:
                drawString("Trigger Configuration", 33, 7);
                if (host.getTriggerStack() != null)
                {
                    drawString("No options for this trigger", 25, 50);
                }
                else if (host.getWarheadStack() != null)
                {
                    ItemStack stack = host.getWarheadStack();
                    if (stack.getItem() instanceof IModuleItem)
                    {
                        IModule module = ((IModuleItem) stack.getItem()).getModule(stack);
                        if (module != null && module instanceof IWarhead)
                        {
                            if (module instanceof ITriggerAccepter)
                            {
                                ITrigger trigger = ((ITriggerAccepter) module).getTrigger();
                                if (trigger != null)
                                {
                                    drawString("No options for this trigger", 25, 50);
                                }
                                else
                                {
                                    drawString("No options for this trigger", 25, 50);
                                }
                            }
                            else
                            {
                                drawString("Warhead does not support triggers", 25, 50);
                            }
                        }
                        else
                        {
                            drawString("Failed to read warhead data", 25, 50);
                        }
                    }
                    else
                    {
                        drawString("Insert trigger for options", 25, 50);
                    }
                }
                else
                {
                    drawString("Insert trigger for options", 25, 50);
                }
                break;
            case 3:
                drawString("Autocrafting Settings", 10, 7);
                drawString("Require Explosive:", 12, 47);
                drawString("Require Trigger:", 12, 67);
                break;
        }
    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        final int buttonId = button.id;
        //Crafting button
        if (buttonId == 0)
        {
            host.sendCraftingPacket();
        }
        //Menu tabs
        else if (buttonId > 0 && buttonId < 5 && buttonId - 1 != id)
        {
            host.switchTab(buttonId - 1);
        }
        //Explosives increase
        else if (buttonId == 10)
        {
            if (host.explosiveStackSizeRequired < 64)
            {
                host.explosiveStackSizeRequired += 1;
                host.sendGUIDataUpdate();
            }
        }
        //Explosives decrease
        else if (buttonId == 11)
        {
            if (host.explosiveStackSizeRequired > 1)
            {
                host.explosiveStackSizeRequired -= 1;
                host.sendGUIDataUpdate();
            }
        }
        else if (buttonId == 12)
        {
            host.isAutocrafting = !host.isAutocrafting;
            autoCraftButton.displayString = host.isAutocrafting ? "Disable Autocrafting" : "Enable Autocrafting";
            host.sendGUIDataUpdate();
        }
        else if (buttonId == 13)
        {
            host.requireTrigger = !host.requireTrigger;
            requireTriggerButton.displayString = host.requireTrigger ? "[x]" : "[ ]";
            host.sendGUIDataUpdate();
        }
        else if (buttonId == 14)
        {
            host.requireExplosive = !host.requireExplosive;
            requireExplosiveButton.displayString = host.requireExplosive ? "[x]" : "[ ]";
            host.sendGUIDataUpdate();
        }
    }
}
