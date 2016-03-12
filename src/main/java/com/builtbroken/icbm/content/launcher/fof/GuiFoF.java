package com.builtbroken.icbm.content.launcher.fof;

import com.builtbroken.jlib.data.Colors;
import com.builtbroken.mc.core.network.packet.PacketTile;
import com.builtbroken.mc.lib.helper.LanguageUtility;
import com.builtbroken.mc.prefab.gui.GuiContainerBase;
import cpw.mods.fml.client.config.GuiCheckBox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;

import java.awt.*;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/9/2016.
 */
public class GuiFoF extends GuiContainerBase
{
    protected TileFoF fof;

    protected String message = "sdasdasdasdasdasdasdasdasdasdasd";

    protected GuiTextField userFoFIDField;
    protected GuiCheckBox saveCheckBox;

    public GuiFoF(TileFoF fof, EntityPlayer player)
    {
        super(new ContainerFoF(player, fof));
    }

    @Override
    public void initGui()
    {
        super.initGui();
        userFoFIDField = new GuiTextField(Minecraft.getMinecraft().fontRenderer, 130, 60, 160, 15);
        fields.add(userFoFIDField);
        buttonList.add(new GuiButton(0, 240, 80, 50, 20, "Update"));
        saveCheckBox = new GuiCheckBox(1, 160, 80, "Archive", true);
        buttonList.add(saveCheckBox);
    }


    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        drawStringCentered("Main FoF ID", 40, 10);
        String renderString = LanguageUtility.getLocal("gui.fof.message." + message);
        Color color = null;
        if (renderString.contains("error:"))
        {
            renderString = renderString.replace("error:", "");
        }
        if (renderString.length() <= 25)
        {
            drawString(renderString, 5, 65, color != null ? Colors.getIntFromColor(color) : 4210752);
        }
        else
        {
            drawString(renderString.substring(0, 25) + "...", 5, 65, color != null ? Colors.getIntFromColor(color) : 4210752);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        if (button.id == 0 && userFoFIDField.getText() != null)
        {
            fof.sendPacket(new PacketTile(fof, 2, userFoFIDField.getText(), saveCheckBox.isChecked()));
        }
    }

    public void updateFoFIDField(String id)
    {
        if (!userFoFIDField.isFocused())
        {
            userFoFIDField.setText(id != null ? id : "");
        }
    }
}
