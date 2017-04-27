package com.builtbroken.icbm.content.launcher.controller.local;

import com.builtbroken.icbm.api.controller.ISiloConnectionData;
import com.builtbroken.icbm.content.launcher.launcher.TileAbstractLauncherPad;
import com.builtbroken.mc.api.map.radio.wireless.ConnectionStatus;
import com.builtbroken.mc.client.SharedAssets;
import com.builtbroken.mc.imp.transform.region.Rectangle;
import com.builtbroken.mc.lib.helper.LanguageUtility;
import com.builtbroken.mc.prefab.gui.ContainerDummy;
import com.builtbroken.mc.prefab.gui.EnumGuiIconSheet;
import com.builtbroken.mc.prefab.gui.GuiButton2;
import com.builtbroken.mc.prefab.gui.GuiContainerBase;
import com.builtbroken.mc.prefab.gui.buttons.GuiImageButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.List;

/**
 * Main GUI for the small missile silo controller
 * Created by robert on 2/20/2015.
 */
public class GuiLocalController extends GuiContainerBase
{
    protected TileLocalController controller;

    List<ConnectionStatus> connectionStatuses = new ArrayList();

    long lastClickTime = 0;

    private int ticks = 0;
    private static int updateGuiTicks = 100;

    private final String launcherLabel;
    private final String siloLabel;

    int buttonRowX = 30;
    int buttonRowY = 30;


    public GuiLocalController(TileLocalController launcher, EntityPlayer player)
    {
        super(new ContainerDummy(player, launcher));
        this.controller = launcher;
        this.baseTexture = SharedAssets.GUI__MC_EMPTY_FILE;
        launcherLabel = LanguageUtility.getLocalName("gui.icbm:controller.launcher");
        siloLabel = LanguageUtility.getLocalName("gui.icbm:controller.silo");
    }

    @Override
    public void initGui()
    {
        super.initGui();
        buttonList.add(GuiImageButton.newRefreshButton(0, guiLeft + 150, guiTop + 5));

        connectionStatuses.clear();
        if (controller.launcherData != null)
        {
            int row = 0;
            for (int i = 0; i < controller.launcherData.size() && i < TileLocalController.MAX_LAUNCHER_LINK; i++)
            {
                final int rowHeight = row * 21;

                ISiloConnectionData data = controller.launcherData.get(i);
                //10 11 12 13 14 15
                String siloPrefix = (data.getSilo() instanceof TileAbstractLauncherPad ? launcherLabel : siloLabel);
                GuiButton2 button = new GuiButton2(10 + i, guiLeft + buttonRowX, guiTop + buttonRowY + rowHeight, 80, 20, siloPrefix + "[" + i + "]");
                if (data != null)
                {
                    if (data.getSiloName() != null && !data.getSiloName().isEmpty())
                    {
                        String siloName = data.getSiloName();
                        if (siloName.length() > 8)
                        {
                            siloName = siloName.substring(0, 8);
                        }
                        button.displayString = siloPrefix + "[" + siloName + "]";
                    }
                    connectionStatuses.add(data.getSiloStatus());
                }
                else
                {
                    connectionStatuses.add(ConnectionStatus.NO_CONNECTION);
                }
                if (connectionStatuses.get(connectionStatuses.size() - 1) == ConnectionStatus.NO_CONNECTION)
                {
                    button.disable();
                }
                //16 17 18 19 20 21
                buttonList.add(new GuiButton2(10 + i + TileLocalController.MAX_LAUNCHER_LINK, guiLeft + buttonRowX + 81, guiTop + buttonRowY + rowHeight, 30, 20, LanguageUtility.getLocalName("gui.icbm:controller.fire")).setEnabled(connectionStatuses.get(connectionStatuses.size() - 1) == ConnectionStatus.ONLINE));
                buttonList.add(button);
                //22 23 24 25 26 27
                buttonList.add(new GuiButton2(10 + i + TileLocalController.MAX_LAUNCHER_LINK * 2, guiLeft + buttonRowX + 81 + 36, guiTop + buttonRowY + rowHeight, 20, 20, "[x]"));

                row++;
            }

            //Create tool tips for connection icons
            for (int i = 0; i < connectionStatuses.size(); i++)
            {
                tooltips.put(new Rectangle(buttonRowX - 26, buttonRowY + (i * 21), 28, 28 + (i * 21)), connectionStatuses.get(i).toString());
            }
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY)
    {
        super.drawGuiContainerBackgroundLayer(f, mouseX, mouseY);

        for (int i = 0; i < connectionStatuses.size(); i++)
        {
            switch (connectionStatuses.get(i))
            {
                case ONLINE:
                    EnumGuiIconSheet.STATUS_ON.draw(this, buttonRowX - 24 + guiLeft, buttonRowY + (i * 21) + guiTop);
                    break;
                case OFFLINE:
                    EnumGuiIconSheet.STATUS_OFF.draw(this, buttonRowX - 24 + guiLeft, buttonRowY + (i * 21) + guiTop);
                    break;
                case NO_CONNECTION:
                    EnumGuiIconSheet.STATUS_CONNECTION_LOST.draw(this, buttonRowX - 24 + guiLeft, buttonRowY + (i * 21) + guiTop);
                    break;
            }
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        if (ticks++ >= updateGuiTicks)
        {
            reloadData();
        }
        String name = LanguageUtility.getLocalName(controller.getInventory().getInventoryName());
        drawStringCentered(name, 85, 10);

        if (controller.launcherData == null || controller.launcherData.size() == 0)
        {
            drawStringCentered(LanguageUtility.getLocal("gui.icbm:controller.links.none"), 85, 40);
            drawStringCentered(LanguageUtility.getLocal("gui.icbm:controller.links.none.hint"), 85, 50);
        }
    }

    public void reloadData()
    {
        initGui();
    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        super.actionPerformed(button);
        //Prevents double click when GUI is reloaded
        if (Minecraft.getSystemTime() - lastClickTime < 50)
        {
            return;
        }
        if(button.id == 0)
        {
            //TODO refresh connections
        }
        else if (button.id >= 10 && button.id < 10 + TileLocalController.MAX_LAUNCHER_LINK * 3)
        {
            int id = button.id - 10;
            //Edit launcher button
            if (id < TileLocalController.MAX_LAUNCHER_LINK) //less than 6
            {
                controller.openSiloGui(id, Minecraft.getMinecraft().thePlayer);
            }
            //Fire launcher
            else if (id < TileLocalController.MAX_LAUNCHER_LINK * 2) //less than 12
            {
                controller.fireLauncher(id - TileLocalController.MAX_LAUNCHER_LINK, Minecraft.getMinecraft().thePlayer);
            }
            //Unlink
            else //less than 18
            {
                controller.unlink(id - TileLocalController.MAX_LAUNCHER_LINK * 2, Minecraft.getMinecraft().thePlayer);
            }
        }
        lastClickTime = Minecraft.getSystemTime();
    }
}
