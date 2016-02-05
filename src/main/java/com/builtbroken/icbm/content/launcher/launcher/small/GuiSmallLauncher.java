package com.builtbroken.icbm.content.launcher.launcher.small;

import com.builtbroken.jlib.data.Colors;
import com.builtbroken.mc.core.References;
import com.builtbroken.mc.lib.helper.LanguageUtility;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.prefab.gui.GuiContainerBase;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

/**
 * Created by robert on 2/20/2015.
 */
public class GuiSmallLauncher extends GuiContainerBase
{
    protected TileSmallLauncher launcher;

    protected GuiTextField x_field;
    protected GuiTextField y_field;
    protected GuiTextField z_field;
    protected String errorString = "";

    private int field_update_delay = 0;

    public GuiSmallLauncher(TileSmallLauncher launcher, EntityPlayer player)
    {
        super(new ContainerSmallLauncher(player, launcher));
        this.launcher = launcher;
        this.baseTexture = References.GUI__MC_EMPTY_FILE;
    }

    @Override
    public void initGui()
    {
        super.initGui();
        Keyboard.enableRepeatEvents(true);
        int x = guiLeft + 10;
        int y = guiTop + 40;
        this.x_field = new GuiTextField(this.fontRendererObj, x, y, 30, 20);
        this.x_field.setText("" + launcher.target.xi());
        this.x_field.setMaxStringLength(15);
        this.x_field.setTextColor(16777215);

        this.y_field = new GuiTextField(this.fontRendererObj, x + 35, y, 30, 20);
        this.y_field.setText("" + launcher.target.yi());
        this.y_field.setMaxStringLength(15);
        this.y_field.setTextColor(16777215);

        this.z_field = new GuiTextField(this.fontRendererObj, x + 75, y, 30, 20);
        this.z_field.setText("" + launcher.target.zi());
        this.z_field.setMaxStringLength(15);
        this.z_field.setTextColor(16777215);

        this.buttonList.add(new GuiButton(0, x + 115, y, 40, 20, "Update"));
    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        super.actionPerformed(button);

        //Update button
        if (button.id == 0)
        {
            try
            {
                launcher.setTarget(new Pos(Integer.parseInt(x_field.getText()), Integer.parseInt(y_field.getText()), Integer.parseInt(z_field.getText())));
            } catch (NumberFormatException e)
            {
                //Ignore as this is expected
                errorString = "Invalid target data";
            }
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        drawStringCentered(LanguageUtility.getLocalName(launcher.getInventoryName()), 85, 10);
        drawStringCentered(errorString, 85, 80, Colors.RED.color);

        if (!x_field.isFocused() && !y_field.isFocused() && !z_field.isFocused())
        {
            field_update_delay++;
            if(field_update_delay > 100)
            {
                field_update_delay = 0;
                this.x_field.setText("" + launcher.target.xi());
                this.y_field.setText("" + launcher.target.yi());
                this.z_field.setText("" + launcher.target.zi());
            }
        }
    }

    @Override
    public void drawScreen(int p_73863_1_, int p_73863_2_, float p_73863_3_)
    {
        super.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
        this.x_field.drawTextBox();
        this.y_field.drawTextBox();
        this.z_field.drawTextBox();
    }

    @Override
    protected void keyTyped(char c, int id)
    {
        this.x_field.textboxKeyTyped(c, id);
        this.y_field.textboxKeyTyped(c, id);
        this.z_field.textboxKeyTyped(c, id);
        super.keyTyped(c, id);
    }

    @Override
    protected void mouseClicked(int p_73864_1_, int p_73864_2_, int p_73864_3_)
    {
        super.mouseClicked(p_73864_1_, p_73864_2_, p_73864_3_);
        this.x_field.mouseClicked(p_73864_1_, p_73864_2_, p_73864_3_);
        this.y_field.mouseClicked(p_73864_1_, p_73864_2_, p_73864_3_);
        this.z_field.mouseClicked(p_73864_1_, p_73864_2_, p_73864_3_);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY)
    {
        super.drawGuiContainerBackgroundLayer(f, mouseX, mouseY);

    }
}
