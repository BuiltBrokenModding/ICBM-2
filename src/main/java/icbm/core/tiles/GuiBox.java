package icbm.core.tiles;

import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.opengl.GL11;

import calclavia.lib.gui.GuiContainerBase;

/** @author Darkguardsman */
public class GuiBox extends GuiContainerBase
{
    private TileBox tile;

    public GuiBox(EntityPlayer player, TileBox tile)
    {
        super(new ContainerBox(player.inventory, tile));
        this.tile = tile;
    }

    /** Draw the foreground layer for the GuiContainer (everything in front of the items) */
    @Override
    public void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        this.fontRenderer.drawString(tile.getInvName(), 52, 6, 4210752);
    }

    /** Draw the background layer for the GuiContainer (everything behind the items) */
    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int x, int y)
    {
        super.drawGuiContainerBackgroundLayer(par1, x, y);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        for (int xSlot = 0; xSlot < 9; xSlot++)
        {
            for (int ySlot = 0; ySlot < 3; ySlot++)
            {
                this.drawSlot(8 + 18 * xSlot - 1, 28 + 18 * ySlot - 1);
            }
        }
    }
}