package icbm.sentry.platform.gui;

import icbm.sentry.platform.TileTurretPlatform;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.StatCollector;
import universalelectricity.api.energy.UnitDisplay.Unit;
import calclavia.lib.gui.GuiContainerBase;

public class GuiTurretPlatform extends GuiContainerBase
{
	private TileTurretPlatform tileEntity;

	public GuiTurretPlatform(InventoryPlayer par1InventoryPlayer, TileTurretPlatform tileEntity)
	{
		super(new ContainerTurretPlatform(par1InventoryPlayer, tileEntity));
		this.tileEntity = tileEntity;
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	@Override
	public void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		this.fontRenderer.drawString(tileEntity.getInvName(), 52, 6, 4210752);

		this.renderUniversalDisplay(110, 122, tileEntity.getVoltageInput(null), mouseX, mouseY, Unit.VOLTAGE);

	}

	/**
	 * Draw the background layer for the GuiContainer (everything behind the items)
	 */
	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int x, int y)
	{
		super.drawGuiContainerBackgroundLayer(par1, x, y);

		for (int xSlot = 0; xSlot < 4; xSlot++)
			for (int ySlot = 0; ySlot < 5; ySlot++)
				this.drawSlot(95 + 18 * xSlot - 1, 18 + 18 * ySlot - 1);
	}
}