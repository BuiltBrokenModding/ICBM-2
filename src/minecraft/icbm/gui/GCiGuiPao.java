package icbm.gui;

import icbm.ZhuYao;
import icbm.jiqi.TCiGuiPao;
import icbm.rongqi.CCiGuiPao;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.opengl.GL11;

import universalelectricity.core.electricity.ElectricInfo;
import universalelectricity.core.electricity.ElectricInfo.ElectricUnit;
import universalelectricity.prefab.network.PacketManager;
import cpw.mods.fml.common.network.PacketDispatcher;

public class GCiGuiPao extends GuiContainer
{
	private TCiGuiPao tileEntity;
	private EntityPlayer player;

	private int containerWidth;
	private int containerHeight;

	private int GUITicks = 0;

	public GCiGuiPao(TCiGuiPao tileEntity, EntityPlayer player)
	{
		super(new CCiGuiPao(player.inventory, tileEntity));
		this.tileEntity = tileEntity;
		this.player = player;
	}

	/**
	 * Adds the buttons (and other controls) to the screen in question.
	 */
	@Override
	public void initGui()
	{
		super.initGui();
		this.controlList.clear();

		this.controlList.add(new GuiButton(0, this.width / 2 - 40, this.height / 2 - 50, 46, 20, "Mount"));
	}

	@Override
	public void onGuiClosed()
	{
		super.onGuiClosed();
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
				PacketDispatcher.sendPacketToServer(PacketManager.getPacket(ZhuYao.CHANNEL, this.tileEntity, (int) 2));
				this.tileEntity.mount(this.mc.thePlayer);
				this.mc.thePlayer.closeScreen();
				break;
		}
	}

	/**
	 * Call this method from you GuiScreen to process the keys into textbox.
	 */
	@Override
	public void keyTyped(char par1, int par2)
	{
		super.keyTyped(par1, par2);
	}

	/**
	 * Args: x, y, buttonClicked
	 */
	@Override
	public void mouseClicked(int par1, int par2, int par3)
	{
		super.mouseClicked(par1, par2, par3);
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2)
	{
		this.fontRenderer.drawString(this.tileEntity.getInvName(), 73, 6, 4210752);

		this.fontRenderer.drawString("Ammo Input", 65, 20, 4210752);

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

		this.fontRenderer.drawString(color + "Status: " + status, 8, 60, 4210752);

		this.fontRenderer.drawString(this.tileEntity.getVoltage() + "v", 130, 70, 4210752);
		this.fontRenderer.drawString(ElectricInfo.getDisplayShort(this.tileEntity.getJoules(), ElectricUnit.JOULES) + "/" + ElectricInfo.getDisplayShort(this.tileEntity.getMaxJoules(), ElectricUnit.JOULES), 8, 70, 4210752);
	}

	/**
	 * Draw the background layer for the GuiContainer (everything behind the items)
	 */
	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
	{
		int var4 = this.mc.renderEngine.getTexture(ZhuYao.TEXTURE_FILE_PATH + "gui_railgun.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture(var4);
		containerWidth = (this.width - this.xSize) / 2;
		containerHeight = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(containerWidth, containerHeight, 0, 0, this.xSize, this.ySize);
	}
}
