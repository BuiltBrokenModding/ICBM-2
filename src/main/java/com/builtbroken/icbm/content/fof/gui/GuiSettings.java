package com.builtbroken.icbm.content.fof.gui;

import com.builtbroken.jlib.data.Colors;
import com.builtbroken.mc.core.network.packet.PacketTile;
import com.builtbroken.mc.lib.helper.LanguageUtility;
import com.builtbroken.mc.prefab.gui.GuiContainerBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

import java.awt.*;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/14/2016.
 */
public class GuiSettings extends GuiContainerBase
{
    protected final GuiFoF returnGui;
    private String message;
    private long messageSetTime;
    public int pos = 0;

    public GuiSettings(GuiFoF returnGui)
    {
        super(returnGui.inventorySlots);
        this.returnGui = returnGui;
    }

    @Override
    public void initGui()
    {
        super.initGui();
        buttonList.add(new GuiButton(0, guiLeft + 5, guiTop + 5, 30, 20, "Back"));
        GuiButton enable = new GuiButton(1, guiLeft + 50, guiTop + 17, 30, 20, "Enable");
        enable.enabled = !returnGui.fof.hasProfile;

        GuiButton disable = new GuiButton(2, guiLeft + 95, guiTop + 17, 30, 20, "Disable");
        disable.enabled = returnGui.fof.hasProfile;

        GuiButton users = new GuiButton(4, guiLeft + 140, guiTop + 17, 30, 20, "Users");
        disable.enabled = returnGui.fof.hasProfile;

        GuiButton clear = new GuiButton(3, guiLeft + 50, guiTop + 60, 30, 20, "Clear");

        buttonList.add(enable);
        buttonList.add(disable);
        buttonList.add(clear);
        buttonList.add(users);
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();
        if (message != null && (System.currentTimeMillis() - messageSetTime) > 60000)
        {
            message = null;
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        initGui();
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        drawString("User Management System", 40, 7);
        drawString("FoF Archive", 40, 50);
        if (message != null)
        {

            String renderString = message;
            String s = null;
            if (renderString.contains("{"))
            {
                //TODO add , split support for several values of replacement
                s = renderString.substring(renderString.indexOf("{"), renderString.indexOf("}") + 1);
                renderString = renderString.replace(s, "");
                s = s.substring(1, 2);
            }
            renderString = LanguageUtility.getLocal("gui.fof.message." + renderString);
            if (s != null)
            {
                renderString = String.format(renderString, s);
            }
            int x = 90;
            int y = 60;
            if (pos == 1)
            {
                x = 65;
                y = 40;
            }
            Color color = null;
            if (renderString.contains("error:"))
            {
                renderString = renderString.replace("error:", "");
            }
            if (renderString.length() <= 25)
            {
                drawString(renderString, x, y, color != null ? Colors.getIntFromColor(color) : 4210752);
            }
            else
            {
                //TODO add line wrapper up to 2 additional lines of info
                drawString(renderString.substring(0, 25) + "...", x, y, color != null ? Colors.getIntFromColor(color) : 4210752);
            }
        }
    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        if (button.id == 0)
        {
            Minecraft.getMinecraft().displayGuiScreen(returnGui);
        }
        else if (button.id == 1)
        {
            returnGui.fof.sendPacketToServer(new PacketTile(returnGui.fof, 3, true));
        }
        else if (button.id == 2)
        {
            returnGui.fof.sendPacketToServer(new PacketTile(returnGui.fof, 3, false));
        }
        else if (button.id == 3)
        {
            returnGui.fof.sendPacketToServer(new PacketTile(returnGui.fof, 4));
        }
        else if (button.id == 4)
        {
            pos = 2;
            setMessage("wip");
        }
    }

    public void setMessage(String message)
    {
        this.message = message;
        this.messageSetTime = System.currentTimeMillis();
    }
}
