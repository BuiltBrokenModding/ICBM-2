package icbm.zhapin.gui;

import icbm.core.GICBMContainer;
import icbm.zhapin.jiqi.TYinDaoQi;
import icbm.zhapin.rongqi.CYinDaoQi;
import mffs.api.card.ICoordLink;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.Direction;
import net.minecraft.util.MathHelper;
import universalelectricity.core.electricity.ElectricityDisplay;
import universalelectricity.core.vector.Vector2;
import universalelectricity.core.vector.Vector3;

public class GYinDaoQi extends GICBMContainer
{
	private TYinDaoQi tileEntity;
	private float animation = 0;

	public GYinDaoQi(InventoryPlayer par1InventoryPlayer, TYinDaoQi tileEntity)
	{
		super(new CYinDaoQi(par1InventoryPlayer, tileEntity));
		this.tileEntity = tileEntity;
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		this.fontRenderer.drawString("\u00a77" + tileEntity.getInvName(), 48, 6, 4210752);
		this.fontRenderer.drawString("Path Simulator", 50, 20, 4210752);
		this.fontRenderer.drawString("From:", 13, 30, 4210752);
		this.fontRenderer.drawString("To:", 134, 30, 4210752);

		if (this.tileEntity.getStackInSlot(0) != null && this.tileEntity.getStackInSlot(1) != null)
		{
			if (this.tileEntity.getStackInSlot(0).getItem() instanceof ICoordLink && this.tileEntity.getStackInSlot(1).getItem() instanceof ICoordLink)
			{
				Vector3 pos1 = ((ICoordLink) this.tileEntity.getStackInSlot(0).getItem()).getLink(this.tileEntity.getStackInSlot(0));
				Vector3 pos2 = ((ICoordLink) this.tileEntity.getStackInSlot(1).getItem()).getLink(this.tileEntity.getStackInSlot(1));

				double displacement = pos1.distanceTo(pos2);

				this.fontRenderer.drawString("Displacement: " + ElectricityDisplay.roundDecimals(displacement) + " Meters", 13, 65, 4210752);

				double w = Vector2.distance(pos1.toVector2(), pos2.toVector2());
				double h = 160 + (w * 3) - pos1.y;

				double distance = 0.5 * Math.sqrt(16 * (h * h) + (w * w)) + (((w * w) / (8 * h)) * (Math.log(4 * h + Math.sqrt(16 * (h * h) + (w * w))) - Math.log(w)));

				this.fontRenderer.drawString("Arc: " + ElectricityDisplay.roundDecimals(distance) + " Meters", 13, 75, 4210752);
				this.fontRenderer.drawString("Time: " + ElectricityDisplay.roundDecimals(Math.max(100, 2 * displacement) / 20) + " Seconds", 13, 85, 4210752);

				Vector3 delta = Vector3.subtract(pos1, pos2);
				double rotation = MathHelper.wrapAngleTo180_double(Math.toDegrees(Math.atan2(delta.z, delta.x))) - 90;
				int heading = MathHelper.floor_double(rotation * 4.0F / 360.0F + 0.5D) & 3;

				this.fontRenderer.drawString("Direction: " + ElectricityDisplay.roundDecimals(rotation) + " (" + Direction.directions[heading] + ")", 13, 95, 4210752);
			}
		}

		this.fontRenderer.drawString("More features coming soon!", 13, 120, 4210752);

		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int x, int y)
	{
		super.drawGuiContainerBackgroundLayer(f, x, y);
		this.drawSlot(15, 40);
		this.drawSlot(135, 40);

		this.drawBar(75, 40, 1 - this.animation);

		if (this.tileEntity.getStackInSlot(0) != null && this.tileEntity.getStackInSlot(1) != null)
		{
			this.animation = (this.animation + 0.005f * f) % 1;

		}
		else
		{
			this.animation = 1;
		}
	}
}
