package icbm.sentry.gui;

import icbm.core.ICBMCore;
import icbm.sentry.platform.TileEntityTurretPlatform;
import icbm.sentry.turret.TileEntityTurret;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import universalelectricity.core.electricity.ElectricityDisplay;
import universalelectricity.core.electricity.ElectricityDisplay.ElectricUnit;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiPlatformSlots extends GuiPlatformContainer
{
	public static final ResourceLocation TEXTURE = new ResourceLocation(ICBMCore.DOMAIN, ICBMCore.GUI_PATH + "gui_platform_slot.png");

	public GuiPlatformSlots(InventoryPlayer inventoryPlayer, TileEntityTurretPlatform tileEntity)
	{
		super(inventoryPlayer, tileEntity);
	}

	/** Draw the foreground layer for the GuiContainer (everything in front of the items) */
	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y)
	{
		this.fontRenderer.drawString("Ammunition", 8, 30, 4210752);

		TileEntityTurret turret = this.tileEntity.getTurret();

		// Render the turret energy
		if (turret != null && turret.getFiringRequest() > 0)
		{
			String color = "\u00a74";

			if (this.tileEntity.isRunning())
			{
				color = "\u00a7a";
			}

			this.fontRenderer.drawString("Energy Per Shot", 85, 33, 4210752);
			this.fontRenderer.drawString(color + ElectricityDisplay.getDisplayShort(Math.min(this.tileEntity.getEnergyStored(), turret.getFiringRequest()), ElectricUnit.JOULES), 87, 43, 4210752);
			this.fontRenderer.drawString(color + "of " + ElectricityDisplay.getDisplayShort(this.tileEntity.getTurret().getFiringRequest(), ElectricUnit.JOULES), 87, 53, 4210752);
		}

		this.fontRenderer.drawString("Upgrades", 87, 66, 4210752);
		super.drawGuiContainerForegroundLayer(x, y);
	}

	/** Draw the background layer for the GuiContainer (everything behind the items) */
	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int x, int y)
	{
		super.drawGuiContainerBackgroundLayer(par1, x, y);
		FMLClientHandler.instance().getClient().renderEngine.bindTexture(TEXTURE);

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		int containerWidth = (this.width - this.xSize) / 2;
		int containerHeight = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(containerWidth, containerHeight, 0, 0, this.xSize, this.ySize);
	}
}
