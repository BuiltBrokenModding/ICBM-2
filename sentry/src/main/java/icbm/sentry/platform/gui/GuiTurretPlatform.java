package icbm.sentry.platform.gui;

import icbm.Reference;
import icbm.sentry.platform.TileTurretPlatform;
import icbm.sentry.turret.block.TileTurret;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeDirection;

import org.lwjgl.opengl.GL11;

import universalelectricity.api.energy.UnitDisplay.Unit;
import calclavia.lib.gui.GuiContainerBase;
import calclavia.lib.render.EnumColor;

public class GuiTurretPlatform extends GuiContainerBase
{
	public static final ResourceLocation TERMINAL_TEXTURE = new ResourceLocation(Reference.DOMAIN, Reference.GUI_PATH + "gui_platform_terminal.png");

	private TileTurretPlatform tile;

	public GuiTurretPlatform(InventoryPlayer par1InventoryPlayer, TileTurretPlatform tile)
	{
		super(new ContainerTurretPlatform(par1InventoryPlayer, tile));
		this.tile = tile;
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	@Override
	public void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		this.fontRenderer.drawString(tile.getInvName(), 52, 6, 4210752);

		// TODO: Add different directions in the future.
		TileTurret turret = tile.getTurret(ForgeDirection.UP);

		if (turret != null)
		{
			fontRenderer.drawString("Position: " + ForgeDirection.UP, 8, 20, 4210752);
			fontRenderer.drawString(EnumColor.BRIGHT_GREEN + "Energy", 8, 30, 4210752);
			renderUniversalDisplay(8, 40, turret.getTurret().energy.getEnergy(), mouseX, mouseY, Unit.JOULES);
			fontRenderer.drawString(EnumColor.BRIGHT_GREEN + "Required Energy", 8, 50, 4210752);
			renderUniversalDisplay(8, 60, turret.getTurret().energy.getEnergyCapacity(), mouseX, mouseY, Unit.JOULES);
		}
	}

	/**
	 * Draw the background layer for the GuiContainer (everything behind the items)
	 */
	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int x, int y)
	{
		super.drawGuiContainerBackgroundLayer(par1, x, y);

		this.mc.renderEngine.bindTexture(TERMINAL_TEXTURE);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		// drawTexturedModalRect(this.containerWidth, this.containerHeight, 0, 0, this.xSize,
		// this.ySize);

		for (int xSlot = 0; xSlot < 4; xSlot++)
			for (int ySlot = 0; ySlot < 5; ySlot++)
				this.drawSlot(95 + 18 * xSlot - 1, 18 + 18 * ySlot - 1);
	}
}