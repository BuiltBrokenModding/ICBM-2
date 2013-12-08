package icbm.sentry.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiButtonArrow extends GuiButton
{

    boolean isLeft = false;

    public GuiButtonArrow(int par1, int par2, int par3, boolean left)
    {
        super(par1, par2, par3, 10, 10, "");
        isLeft = left;
    }

    /** Draws this button to the screen. */
    @Override
    public void drawButton(Minecraft par1Minecraft, int width, int hight)
    {
        if (this.drawButton)
        {
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(GuiButtonImage.TEXTURE);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            boolean var4 = width >= this.xPosition && hight >= this.yPosition && width < this.xPosition + this.width && hight < this.yPosition + this.height;
            int var5 = 126;
            int varWid = 20;
            if (isLeft)
            {
                varWid += 10;
            }
            if (var4)
            {
                var5 += this.height;
            }

            this.drawTexturedModalRect(this.xPosition, this.yPosition, varWid, var5, this.width, this.height);
        }
    }
}