package icbm.sentry.platform.gui.user;

import icbm.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiButtonNextPage extends GuiButton
{
    /** True for pointing right (next page), false for pointing left (previous page). */
    private boolean pointRight;
    public static final ResourceLocation gui_sheet = new ResourceLocation(Reference.DOMAIN, Reference.GUI_PATH + "sentry_terminal.png");
    private static final int icon_ySize = 9;
    private static final int icon_xSize = 9;
    
    public GuiButtonNextPage(int buttonID, int xPos, int yPos, boolean pointRight)
    {
        super(buttonID, xPos, yPos, 23, 13, "");
        this.pointRight = pointRight;
    }

    /** Draws this button to the screen. */
    public void drawButton(Minecraft mc, int xPos, int yPos)
    {
        if (this.drawButton)
        {
            boolean flag = xPos >= this.xPosition && yPos >= this.yPosition && xPos < this.xPosition + this.width && yPos < this.yPosition + this.height;
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            mc.getTextureManager().bindTexture(gui_sheet);
            int k = 0;
            int l = 193;

            if (flag)
            {
                k += icon_xSize;
            }

            if (!this.pointRight)
            {
                l += icon_ySize;
            }

            this.drawTexturedModalRect(this.xPosition, this.yPosition, k, l, icon_xSize, icon_ySize);
        }
    }
}
