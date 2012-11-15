package icbm.gui;

import icbm.ZhuYao;
import icbm.api.ICBM;
import icbm.jiqi.TDianCiQi;
import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiTextField;

import org.lwjgl.opengl.GL11;

import universalelectricity.core.electricity.ElectricInfo;
import universalelectricity.core.electricity.ElectricInfo.ElectricUnit;
import universalelectricity.prefab.network.PacketManager;
import cpw.mods.fml.common.network.PacketDispatcher;

public class GDianCiQi extends ICBMGui
{
	private TDianCiQi tileEntity;
	private GuiTextField textFieldBanJing;

	private int containerWidth;
	private int containerHeight;

	public GDianCiQi(TDianCiQi tileEntity)
	{
		this.tileEntity = tileEntity;
	}

	/**
	 * Adds the buttons (and other controls) to
	 * the screen in question.
	 */
	@Override
	public void initGui()
	{
		super.initGui();
		this.controlList.clear();

		this.controlList.add(new GuiButton(0, this.width / 2 - 77, this.height / 2 - 10, 50, 20, "Missiles"));
		this.controlList.add(new GuiButton(1, this.width / 2 - 25, this.height / 2 - 10, 65, 20, "Electricity"));
		this.controlList.add(new GuiButton(2, this.width / 2 + 43, this.height / 2 - 10, 35, 20, "Both"));

		this.textFieldBanJing = new GuiTextField(fontRenderer, 72, 28, 20, 12);
		this.textFieldBanJing.setMaxStringLength(2);
		this.textFieldBanJing.setText(this.tileEntity.banJing + "");
		PacketDispatcher.sendPacketToServer(PacketManager.getPacket(ZhuYao.CHANNEL, this.tileEntity, (int) -1, true));
	}

	@Override
	public void onGuiClosed()
	{
		super.onGuiClosed();
		PacketDispatcher.sendPacketToServer(PacketManager.getPacket(ZhuYao.CHANNEL, this.tileEntity, (int) -1, false));
	}

	/**
	 * Fired when a control is clicked. This is
	 * the equivalent of
	 * ActionListener.actionPerformed(ActionEvent
	 * e).
	 */
	@Override
	protected void actionPerformed(GuiButton par1GuiButton)
	{
		switch (par1GuiButton.id)
		{
			case 0:
				this.tileEntity.muoShi = 1;
				break;
			case 1:
				this.tileEntity.muoShi = 2;
				break;
			case 2:
				this.tileEntity.muoShi = 0;
				break;
		}

		PacketDispatcher.sendPacketToServer(PacketManager.getPacket(ZhuYao.CHANNEL, this.tileEntity, (int) 3, this.tileEntity.muoShi));
	}

	/**
	 * Call this method from you GuiScreen to
	 * process the keys into textbox.
	 */
	@Override
	public void keyTyped(char par1, int par2)
	{
		super.keyTyped(par1, par2);
		this.textFieldBanJing.textboxKeyTyped(par1, par2);

		try
		{
			int radius = Math.min(Math.max(Integer.parseInt(this.textFieldBanJing.getText()), 10), this.tileEntity.MAX_RADIUS);
			this.tileEntity.banJing = radius;
			PacketDispatcher.sendPacketToServer(PacketManager.getPacket(ZhuYao.CHANNEL, this.tileEntity, (int) 2, this.tileEntity.banJing));
		}
		catch (NumberFormatException e)
		{

		}
	}

	/**
	 * Args: x, y, buttonClicked
	 */
	@Override
	public void mouseClicked(int par1, int par2, int par3)
	{
		super.mouseClicked(par1, par2, par3);
		this.textFieldBanJing.mouseClicked(par1 - containerWidth, par2 - containerHeight, par3);
	}

	/**
	 * Draw the foreground layer for the
	 * GuiContainer (everything in front of the
	 * items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer()
	{
		this.fontRenderer.drawString("EMP Tower", 65, 6, 4210752);

		this.fontRenderer.drawString("EMP Radius:       blocks", 12, 30, 4210752);
		this.textFieldBanJing.drawTextBox();

		this.fontRenderer.drawString("EMP Effect:", 12, 55, 4210752);

		// Shows the EMP mode of the EMP Tower
		String mode = "Debilitate Electronics";

		if (this.tileEntity.muoShi == 1)
		{
			mode = "Disrupt Missiles";
		}
		else if (this.tileEntity.muoShi == 2)
		{
			mode = "Deplete Electricity";
		}

		this.fontRenderer.drawString("Mode: " + mode, 12, 105, 4210752);

		// Shows the status of the EMP Tower
		String color = "\u00a74";
		String status = "Idle";

		if (this.tileEntity.isDisabled())
		{
			status = "Disabled";
		}
		else if (this.tileEntity.getJoules() < this.tileEntity.getMaxJoules())
		{
			status = "Insufficient electricity!";
		}
		else
		{
			color = "\u00a72";
			status = "Ready to blast!";
		}

		this.fontRenderer.drawString(color + "Status: " + status, 12, 120, 4210752);
		this.fontRenderer.drawString("Voltage: " + this.tileEntity.getVoltage() + "v", 12, 135, 4210752);
		this.fontRenderer.drawString(ElectricInfo.getDisplayShort(this.tileEntity.getJoules(), ElectricUnit.JOULES) + "/" + ElectricInfo.getDisplayShort(this.tileEntity.getMaxJoules(), ElectricUnit.JOULES), 12, 150, 4210752);
	}

	/**
	 * Draw the background layer for the
	 * GuiContainer (everything behind the items)
	 */
	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
	{
		int var4 = this.mc.renderEngine.getTexture(ICBM.TEXTURE_FILE_PATH + "EmptyGUI.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture(var4);
		containerWidth = (this.width - this.xSize) / 2;
		containerHeight = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(containerWidth, containerHeight, 0, 0, this.xSize, this.ySize);
	}

	@Override
	public void updateScreen()
	{
		super.updateScreen();

		if (!this.textFieldBanJing.isFocused())
			this.textFieldBanJing.setText(this.tileEntity.banJing + "");
	}
}
