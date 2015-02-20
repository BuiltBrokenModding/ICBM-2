package com.builtbroken.icbm.content.launcher;

import com.builtbroken.mc.lib.helper.LanguageUtility;
import com.builtbroken.mc.prefab.gui.ContainerDummy;
import com.builtbroken.mc.prefab.gui.GuiContainerBase;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
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

    public GuiSmallLauncher(TileSmallLauncher launcher, EntityPlayer player)
    {
        super(new ContainerDummy(player, launcher));
        this.launcher = launcher;
        int x = 150;
        this.x_field = new GuiTextField(this.fontRendererObj, x + 35, 50, 30, 20);
        this.x_field.setMaxStringLength(15);
        this.x_field.setEnableBackgroundDrawing(false);
        this.x_field.setTextColor(16777215);

        this.y_field = new GuiTextField(this.fontRendererObj, x + 75, 50, 30, 20);
        this.y_field.setMaxStringLength(15);
        this.y_field.setEnableBackgroundDrawing(false);
        this.y_field.setTextColor(16777215);

        this.z_field = new GuiTextField(this.fontRendererObj, x + 115, 50, 30, 20);
        this.z_field.setMaxStringLength(15);
        this.z_field.setEnableBackgroundDrawing(false);
        this.z_field.setTextColor(16777215);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        drawStringCentered(LanguageUtility.getLocalName(launcher.getInventoryName()), 100, 50);
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
    protected void keyTyped(char p_73869_1_, int p_73869_2_)
    {
        this.x_field.textboxKeyTyped(p_73869_1_, p_73869_2_);
        this.y_field.textboxKeyTyped(p_73869_1_, p_73869_2_);
        this.z_field.textboxKeyTyped(p_73869_1_, p_73869_2_);
        super.keyTyped(p_73869_1_, p_73869_2_);
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
