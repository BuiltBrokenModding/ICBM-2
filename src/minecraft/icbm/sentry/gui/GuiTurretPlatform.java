package icbm.sentry.gui;

import icbm.sentry.CommonProxy;
import icbm.sentry.ICBMSentry;
import icbm.sentry.container.ContainerTurretPlatform;
import icbm.sentry.platform.TileEntityTurretPlatform;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;

import org.lwjgl.opengl.GL11;

import universalelectricity.core.electricity.ElectricInfo;
import universalelectricity.core.electricity.ElectricInfo.ElectricUnit;
import universalelectricity.prefab.TranslationHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiTurretPlatform extends GuiContainer
{
	private TileEntityTurretPlatform tileEntity;

	private int containerWidth;
	private int containerHeight;
	private EntityPlayer player;

	public GuiTurretPlatform(InventoryPlayer par1InventoryPlayer, TileEntityTurretPlatform tileEntity)
	{
		super(new ContainerTurretPlatform(par1InventoryPlayer, tileEntity));
		this.tileEntity = tileEntity;
		this.player = par1InventoryPlayer.player;
	}

	@Override
	public void initGui()
	{
		super.initGui();
		this.controlList.clear();
		this.controlList.add(new GuiButtonImage(1, (this.width - this.xSize) / 2 + 150, (this.height - this.ySize) / 2 + 5, 2));
	}

	@Override
	protected void actionPerformed(GuiButton button)
	{
		if (button.id == 1)
		{
			player.openGui(ICBMSentry.instance, CommonProxy.GUI_CONSOLE_ID, this.tileEntity.worldObj, this.tileEntity.xCoord, this.tileEntity.yCoord, this.tileEntity.zCoord);
		}
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y)
	{
		String title = TranslationHelper.getLocal("tile.turret." + this.tileEntity.getTurret().getBlockMetadata() + ".name");
		this.fontRenderer.drawString(title, (int) ((this.xSize / 2) - ((float) title.length() * 2.5)), 6, 4210752);
		this.fontRenderer.drawString("Ammunition", 8, 17, 4210752);

		// Shows the status of the EMP Tower
		String color = "\u00a74";

		if (!this.tileEntity.isDisabled() && this.tileEntity.wattsReceived >= this.tileEntity.getWattBuffer())
		{
			color = "\u00a72";
		}

		this.fontRenderer.drawString("Energy Per Shot", 85, 30, 4210752);
		this.fontRenderer.drawString(color + ElectricInfo.getDisplayShort(this.tileEntity.wattsReceived, ElectricUnit.JOULES) + "/" + ElectricInfo.getDisplayShort(this.tileEntity.getWattBuffer(), ElectricUnit.JOULES), 87, 40, 4210752);
		this.fontRenderer.drawString("Upgrades", 87, 50, 4210752);
	}

	/**
	 * Draw the background layer for the GuiContainer (everything behind the items)
	 */
	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
	{
		int var4 = this.mc.renderEngine.getTexture(ICBMSentry.TEXTURE_PATH + "gui_turret.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture(var4);
		this.containerWidth = (this.width - this.xSize) / 2;
		this.containerHeight = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(this.containerWidth, this.containerHeight, 0, 0, this.xSize, this.ySize);
	}
}
