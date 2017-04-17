package com.builtbroken.icbm.content.launcher.gui;

import com.builtbroken.icbm.content.launcher.TileAbstractLauncher;
import com.builtbroken.jlib.data.Colors;
import com.builtbroken.mc.lib.helper.LanguageUtility;
import com.builtbroken.mc.imp.transform.vector.Pos;
import com.builtbroken.mc.prefab.gui.EnumGuiIconSheet;
import com.builtbroken.mc.prefab.gui.GuiButton2;
import com.builtbroken.mc.prefab.gui.GuiContainerBase;
import com.builtbroken.mc.prefab.gui.buttons.GuiImageButton;
import com.builtbroken.mc.prefab.gui.buttons.GuiLeftRightArrowButton;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;

/**
 * Main GUI for the small missile silo controller
 * Created by robert on 2/20/2015.
 */
public class GuiSiloSettings extends GuiContainerBase
{
    protected GuiTextField x_field;
    protected GuiTextField y_field;
    protected GuiTextField z_field;
    protected GuiTextField name_field;
    public String errorString = "error.test";

    long lastClickTime = 0;

    private int ticks = 0;
    private static int updateGuiTicks = 100;

    private TileAbstractLauncher launcher;
    private EntityPlayer player;

    public GuiSiloSettings(TileAbstractLauncher launcher, EntityPlayer player)
    {
        super(new ContainerSilo(player, launcher));
        this.launcher = launcher;
        this.player = player;
    }

    @Override
    public void initGui()
    {
        super.initGui();

        int x = this.guiLeft + 20;
        int y = this.guiTop + 20;

        this.buttonList.add(new GuiLeftRightArrowButton(0, guiLeft + 3, guiTop + 3, true));
        this.buttonList.add(GuiImageButton.newSaveButton(1, x + 125, y));
        this.buttonList.add(new GuiButton2(2, x + 112, y + 30, 40, 20, "Encode"));

        x = guiLeft + 10;
        y = guiTop + 23;

        this.x_field = this.newField(x, y, 40, 15, launcher.target == null ? "Error" : "" + launcher.target.xi());
        this.y_field = this.newField(x + 45, y, 40, 15, launcher.target == null ? "Error" : "" + launcher.target.yi());
        this.z_field = this.newField(x + 90, y, 40, 15, launcher.target == null ? "Error" : "" + launcher.target.zi());
        this.name_field = this.newField(x, y + 30, 90, 15, launcher.getCustomName() == null ? "" : launcher.getCustomName());
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY)
    {
        super.drawGuiContainerBackgroundLayer(f, mouseX, mouseY);
        for (Object object : inventorySlots.inventorySlots)
        {
            if (object instanceof Slot)
            {
                Slot slot = (Slot) object;
                drawSlot(slot.xDisplayPosition - 1, slot.yDisplayPosition - 1, EnumGuiIconSheet.NONE);
            }
        }
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
        String name = "Silo";
        Block block = launcher.toLocation().getBlock();
        if (block != null)
        {
            String localization = block.getLocalizedName();
            if (localization != null && !localization.contains("tile."))
            {
                name = localization;
            }
        }

        drawStringCentered(name, 85, 4);

        drawString(LanguageUtility.getLocalName("gui.icbm:controller.target"), 10, 13);
        drawString(LanguageUtility.getLocalName("gui.icbm:controller.launcherName"), 10, 42);

        if (errorString != null && !errorString.isEmpty())
        {
            drawString(Colors.RED.code + "Error: " + errorString, 10, 13);
        }
    }

    public void reloadData()
    {
        if (this.x_field != null && this.y_field != null && this.z_field != null && this.x_field.isFocused() && this.y_field.isFocused() && this.z_field.isFocused())
        {
            if (launcher.target != null)
            {
                this.x_field.setText("" + launcher.target.xi());
                this.y_field.setText("" + launcher.target.yi());
                this.z_field.setText("" + launcher.target.zi());
                this.name_field.setText(launcher.getCustomName() == null ? "" : launcher.getCustomName());
            }
        }
        errorString = null;
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
        this.errorString = "";

        if (button.id == 0)
        {
            launcher.returnToPrevGui();
        }
        else if (button.id == 1)
        {
            try
            {
                launcher.setTarget(new Pos(Integer.parseInt(x_field.getText()), Integer.parseInt(y_field.getText()), Integer.parseInt(z_field.getText())));
            }
            catch (NumberFormatException e)
            {
                errorString = "gui.icbm:controller.invalid.input";
            }
            launcher.setCustomName("" + name_field.getText());
        }
        else if (button.id == 2)
        {
            launcher.encodeItem(player);
        }
        lastClickTime = Minecraft.getSystemTime();
    }
}
