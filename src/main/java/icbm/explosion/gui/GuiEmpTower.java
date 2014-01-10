package icbm.explosion.gui;

import icbm.Reference;
import icbm.core.ICBMCore;
import icbm.explosion.machines.TileEMPTower;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import universalelectricity.api.energy.UnitDisplay;
import universalelectricity.api.energy.UnitDisplay.Unit;
import calclavia.lib.gui.GuiBase;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.PacketDispatcher;

public class GuiEmpTower extends GuiBase
{
	public static final ResourceLocation TEXTURE = new ResourceLocation(Reference.DOMAIN, Reference.GUI_PATH + "gui_empty.png");

	private TileEMPTower tileEntity;
	private GuiTextField textFieldBanJing;

	private int containerWidth;
	private int containerHeight;

	public GuiEmpTower(TileEMPTower tileEntity)
	{
		this.tileEntity = tileEntity;
	}

	/** Adds the buttons (and other controls) to the screen in question. */
	@Override
	public void initGui()
	{
		super.initGui();
		this.buttonList.clear();

		this.buttonList.add(new GuiButton(0, this.width / 2 - 77, this.height / 2 - 10, 50, 20, "Missiles"));
		this.buttonList.add(new GuiButton(1, this.width / 2 - 25, this.height / 2 - 10, 65, 20, "Electricity"));
		this.buttonList.add(new GuiButton(2, this.width / 2 + 43, this.height / 2 - 10, 35, 20, "Both"));

		this.textFieldBanJing = new GuiTextField(fontRenderer, 72, 28, 30, 12);
		this.textFieldBanJing.setMaxStringLength(3);
		this.textFieldBanJing.setText(this.tileEntity.empRadius + "");
	}

	/**
	 * Fired when a control is clicked. This is the equivalent of
	 * ActionListener.actionPerformed(ActionEvent e).
	 */
	@Override
	protected void actionPerformed(GuiButton par1GuiButton)
	{
		switch (par1GuiButton.id)
		{
			case 0:
				this.tileEntity.empMode = 1;
				break;
			case 1:
				this.tileEntity.empMode = 2;
				break;
			case 2:
				this.tileEntity.empMode = 0;
				break;
		}

		PacketDispatcher.sendPacketToServer(ICBMCore.PACKET_TILE.getPacket(this.tileEntity, 3, this.tileEntity.empMode));
	}

	/** Call this method from you GuiScreen to process the keys into textbox. */
	@Override
	public void keyTyped(char par1, int par2)
	{
		super.keyTyped(par1, par2);
		this.textFieldBanJing.textboxKeyTyped(par1, par2);

		try
		{
			int radius = Math.min(Math.max(Integer.parseInt(this.textFieldBanJing.getText()), 10), TileEMPTower.MAX_RADIUS);
			this.tileEntity.empRadius = radius;
			PacketDispatcher.sendPacketToServer(ICBMCore.PACKET_TILE.getPacket(this.tileEntity, 2, this.tileEntity.empRadius));
		}
		catch (NumberFormatException e)
		{

		}
	}

	/** Args: x, y, buttonClicked */
	@Override
	public void mouseClicked(int x, int y, int par3)
	{
		super.mouseClicked(x, y, par3);
		this.textFieldBanJing.mouseClicked(x - containerWidth, y - containerHeight, par3);
	}

	/** Draw the foreground layer for the GuiContainer (everything in front of the items) */
	@Override
	protected void drawForegroundLayer(int var2, int var3, float var1)
	{
		this.fontRenderer.drawString("\u00a77EMP Tower", 65, 6, 4210752);

		this.fontRenderer.drawString("EMP Radius:          blocks", 12, 30, 4210752);
		this.textFieldBanJing.drawTextBox();

		this.fontRenderer.drawString("EMP Effect:", 12, 55, 4210752);

		// Shows the EMP mode of the EMP Tower
		String mode = "Debilitate Electronics";

		if (this.tileEntity.empMode == 1)
		{
			mode = "Disrupt Missiles";
		}
		else if (this.tileEntity.empMode == 2)
		{
			mode = "Deplete Electricity";
		}

		this.fontRenderer.drawString("Mode: " + mode, 12, 105, 4210752);

		// Shows the status of the EMP Tower
		String color = "\u00a74";
		String status = "Idle";

		if (this.tileEntity.energy.checkExtract())
		{
			status = "Insufficient electricity!";
		}
		else
		{
			color = "\u00a72";
			status = "Ready to blast!";
		}

		this.fontRenderer.drawString(color + "Status: " + status, 12, 120, 4210752);
		this.fontRenderer.drawString("Voltage: " + this.tileEntity.getVoltageInput(null) + "v", 12, 135, 4210752);
		this.fontRenderer.drawString(UnitDisplay.getDisplayShort(this.tileEntity.energy.getEnergy(), Unit.JOULES) + "/" + UnitDisplay.getDisplayShort(this.tileEntity.energy.getEnergyCapacity(), Unit.JOULES), 12, 150, 4210752);
	}

	/** Draw the background layer for the GuiContainer (everything behind the items) */
	@Override
	protected void drawBackgroundLayer(int var2, int var3, float var1)
	{
		FMLClientHandler.instance().getClient().renderEngine.bindTexture(TEXTURE);

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		containerWidth = (this.width - this.xSize) / 2;
		containerHeight = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(containerWidth, containerHeight, 0, 0, this.xSize, this.ySize);
	}

	@Override
	public void updateScreen()
	{
		super.updateScreen();

		if (!this.textFieldBanJing.isFocused())
			this.textFieldBanJing.setText(this.tileEntity.empRadius + "");
	}
}
