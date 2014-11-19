package icbm.sentry.platform.gui;

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

import resonant.lib.gui.GuiContainerBase;
import resonant.lib.render.EnumColor;
import universalelectricity.api.energy.IEnergyContainer;
import universalelectricity.api.energy.UnitDisplay.Unit;

public class GuiTurretPlatform extends GuiContainerBase
{
    public static final ResourceLocation TERMINAL_TEXTURE = new ResourceLocation(Reference.DOMAIN, Reference.GUI_PATH + "gui_platform_terminal.png");

    private TileTurretPlatform tile;

    public GuiTurretPlatform(EntityPlayer player, TileTurretPlatform tile)
    {
        super(new ContainerTurretPlatform(player.inventory, tile));
        this.tile = tile;
    }

    /** Draw the foreground layer for the GuiContainer (everything in front of the items) */
    @Override
    public void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        this.fontRenderer.drawString(tile.getInvName(), 52, 6, 4210752);

        // TODO: Add different directions in the future.
        TileTurret turret = tile.getTurret(ForgeDirection.UP);

        if (turret != null)
        {
            //TODO: re-add when rotation is implemented for sentries
            //fontRenderer.drawString("Position: " + ForgeDirection.UP, 8, 20, 4210752); 

            if (turret.getTurret() instanceof IEnergyContainer && ((IEnergyContainer) turret.getTurret()).getEnergyCapacity(ForgeDirection.UNKNOWN) > 0)
            {
                fontRenderer.drawString(EnumColor.BRIGHT_GREEN + "Energy", 8, 30, 4210752);
                renderUniversalDisplay(8, 40, ((IEnergyContainer) turret.getTurret()).getEnergy(ForgeDirection.UNKNOWN), ((IEnergyContainer) turret.getTurret()).getEnergyCapacity(ForgeDirection.UNKNOWN), mouseX, mouseY, Unit.JOULES, true);
            }
            if (turret.getTurret() instanceof IEnergyTurret)
            {
                fontRenderer.drawString(EnumColor.BRIGHT_GREEN + "Per Tick Cost", 8, 50, 4210752);
                renderUniversalDisplay(8, 60, ((IEnergyTurret) turret.getTurret()).getRunningCost(), mouseX, mouseY, Unit.JOULES, true);
            }
            if (turret.getTurret() instanceof IWeaponProvider && ((IWeaponProvider) turret.getTurret()).getWeaponSystem() instanceof IEnergyWeapon)
            {
                fontRenderer.drawString(EnumColor.BRIGHT_GREEN + "Weapon Cost", 8, 75, 4210752);
                renderUniversalDisplay(8, 85, ((IEnergyWeapon) ((IWeaponProvider) turret.getTurret()).getWeaponSystem()).getEnergyPerShot(), mouseX, mouseY, Unit.JOULES, true);
            }
        }
    }

    /** Draw the background layer for the GuiContainer (everything behind the items) */
    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int x, int y)
    {
        super.drawGuiContainerBackgroundLayer(par1, x, y);

        this.mc.renderEngine.bindTexture(TERMINAL_TEXTURE);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        // drawTexturedModalRect(this.containerWidth, this.containerHeight, 0, 0, this.xSize,
        // this.ySize);

        for (int xSlot = 0; xSlot < 4; xSlot++)
        {
            for (int ySlot = 0; ySlot < 3; ySlot++)
            {
                this.drawSlot(95 + 18 * xSlot - 1, 18 + 18 * ySlot - 1);
            }
        }

        for (int xSlot = 0; xSlot < 4; xSlot++)
        {
            this.drawSlot(95 + 18 * xSlot - 1, 89);
        }
    }
}