package icbm.sentry.gui;

import icbm.sentry.ICBMSentry;
import icbm.sentry.platform.TileEntityTurretPlatform;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.InventoryPlayer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import universalelectricity.core.electricity.ElectricInfo;
import universalelectricity.core.electricity.ElectricInfo.ElectricUnit;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiPlatformSlots extends GuiPlatformContainer
{
	public GuiPlatformSlots(InventoryPlayer inventoryPlayer, TileEntityTurretPlatform tileEntity)
	{
		super(inventoryPlayer, tileEntity);
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y)
	{
		this.fontRenderer.drawString("Ammunition", 8, 30, 4210752);

		// Shows the status of the EMP Tower
		String color = "\u00a74";

		if (!this.tileEntity.isDisabled() && this.tileEntity.wattsReceived >= this.tileEntity.getWattBuffer())
		{
			color = "\u00a7a";
		}

		this.fontRenderer.drawString("Energy Per Shot", 85, 43, 4210752);
		this.fontRenderer.drawString(color + ElectricInfo.getDisplayShort(this.tileEntity.wattsReceived, ElectricUnit.JOULES) + "/" + ElectricInfo.getDisplayShort(this.tileEntity.getWattBuffer(), ElectricUnit.JOULES), 87, 53, 4210752);
		this.fontRenderer.drawString("Upgrades", 87, 66, 4210752);
		super.drawGuiContainerForegroundLayer(x, y);
	}

	/**
	 * Draw the background layer for the GuiContainer (everything behind the items)
	 */
	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int x, int y)
	{
		super.drawGuiContainerBackgroundLayer(par1, x, y);
		int var4 = this.mc.renderEngine.getTexture(ICBMSentry.TEXTURE_PATH + "gui_platform_slot.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture(var4);
		int containerWidth = (this.width - this.xSize) / 2;
		int containerHeight = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(containerWidth, containerHeight, 0, 0, this.xSize, this.ySize);
	}
}
