package icbm.sentry.gui;

import icbm.core.ICBMCore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiButtonImage extends GuiButton
{
    public static final ResourceLocation TEXTURE = new ResourceLocation(ICBMCore.DOMAIN, ICBMCore.GUI_PATH + "gui_button.png");

    private int type = 0;

    public GuiButtonImage(int par1, int par2, int par3, int type)
    {
        super(par1, par2, par3, 20, 20, "");
        this.type = type;
    }

    /** Draws this button to the screen. */
    @Override
    public void drawButton(Minecraft par1Minecraft, int width, int hight)
    {
        if (this.drawButton)
        {
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(TEXTURE);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            boolean var4 = width >= this.xPosition && hight >= this.yPosition && width < this.xPosition + this.width && hight < this.yPosition + this.height;
            int var5 = 106;
            int var6 = 0;
            if (var4)
            {
                var5 += this.height;
            }
            switch (type)
            {
                case 0:
                    var5 += 40;
                    break;
                case 1:
                    var5 += 40;
                    var6 += 20;
                    break;
                case 2:
                    var5 += 40;
                    var6 += 40;
                    break;
            }

            this.drawTexturedModalRect(this.xPosition, this.yPosition, var6, var5, this.width, this.height);
        }
    }

    /** Checks to see if the x and y coords are intersecting with the button. */
    public boolean isIntersect(int x, int y)
    {
        return x >= this.xPosition && y >= this.yPosition && x < this.xPosition + this.width && y < this.yPosition + this.height;

    }
}