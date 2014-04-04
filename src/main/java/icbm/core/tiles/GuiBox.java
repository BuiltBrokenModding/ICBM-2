package icbm.core.tiles;

import icbm.Reference;
import icbm.sentry.interfaces.IEnergyTurret;
import icbm.sentry.interfaces.IEnergyWeapon;
import icbm.sentry.interfaces.IWeaponProvider;
import icbm.sentry.platform.TileTurretPlatform;
import icbm.sentry.turret.block.TileTurret;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeDirection;

import org.lwjgl.opengl.GL11;

import universalelectricity.api.energy.IEnergyContainer;
import universalelectricity.api.energy.UnitDisplay.Unit;
import calclavia.lib.gui.GuiContainerBase;
import calclavia.lib.render.EnumColor;

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