package icbm.explosion.gui;

import icbm.Reference;
import icbm.core.ICBMCore;
import icbm.core.prefab.render.GuiICBM;
import icbm.explosion.machines.TileLauncherScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import universalelectricity.api.energy.UnitDisplay;
import universalelectricity.api.energy.UnitDisplay.Unit;
import universalelectricity.api.vector.Vector3;
import calclavia.lib.utility.LanguageUtility;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.PacketDispatcher;

public class GuiLauncherScreen extends GuiICBM
{
	public static final ResourceLocation TEXTURE = new ResourceLocation(Reference.DOMAIN, Reference.GUI_PATH + "gui_empty.png");

	private TileLauncherScreen tileEntity;
	private GuiTextField tFX;
	private GuiTextField tFY;
	private GuiTextField tFZ;
	private GuiTextField tFFreq;
	private GuiTextField tFGaoDu;

	private int containerWidth;
	private int containerHeight;

	public GuiLauncherScreen(TileLauncherScreen tileEntity)
	{
		super(tileEntity);
		this.tileEntity = tileEntity;
	}

	/** Adds the buttons (and other controls) to the screen in question. */
	@Override
	public void initGui()
	{
		super.initGui();
		this.tFX = new GuiTextField(fontRenderer, 110, 37, 45, 12);
		this.tFZ = new GuiTextField(fontRenderer, 110, 52, 45, 12);
		this.tFY = new GuiTextField(fontRenderer, 110, 67, 45, 12);
		this.tFGaoDu = new GuiTextField(fontRenderer, 110, 82, 45, 12);
		this.tFFreq = new GuiTextField(fontRenderer, 110, 97, 45, 12);

		this.tFFreq.setMaxStringLength(4);
		this.tFX.setMaxStringLength(6);
		this.tFZ.setMaxStringLength(6);
		this.tFY.setMaxStringLength(3);
		this.tFGaoDu.setMaxStringLength(3);

		this.tFFreq.setText(this.tileEntity.getFrequency() + "");
		this.tFGaoDu.setText(this.tileEntity.gaoDu + "");

		if (this.tileEntity.getTarget() == null)
		{
			this.tFX.setText(Math.round(this.tileEntity.xCoord) + "");
			this.tFZ.setText(Math.round(this.tileEntity.zCoord) + "");
			this.tFY.setText("0");
		}
		else
		{
			this.tFX.setText(Math.round(this.tileEntity.getTarget().x) + "");
			this.tFZ.setText(Math.round(this.tileEntity.getTarget().z) + "");
			this.tFY.setText(Math.round(this.tileEntity.getTarget().y) + "");
		}
	}

	/** Call this method from you GuiScreen to process the keys into textbox. */
	@Override
	public void keyTyped(char par1, int par2)
	{
		super.keyTyped(par1, par2);
		this.tFX.textboxKeyTyped(par1, par2);
		this.tFZ.textboxKeyTyped(par1, par2);

		if (tileEntity.getTier() >= 1)
		{
			this.tFY.textboxKeyTyped(par1, par2);
			this.tFGaoDu.textboxKeyTyped(par1, par2);

			if (tileEntity.getTier() > 1)
			{
				this.tFFreq.textboxKeyTyped(par1, par2);
			}
		}

		try
		{
			Vector3 newTarget = new Vector3(Integer.parseInt(this.tFX.getText()), Math.max(Integer.parseInt(this.tFY.getText()), 0), Integer.parseInt(this.tFZ.getText()));

			this.tileEntity.setTarget(newTarget);
			PacketDispatcher.sendPacketToServer(ICBMCore.PACKET_TILE.getPacket(this.tileEntity, 2, this.tileEntity.getTarget().intX(), this.tileEntity.getTarget().intY(), this.tileEntity.getTarget().intZ()));
		}
		catch (NumberFormatException e)
		{

		}

		try
		{
			short newFrequency = (short) Math.max(Short.parseShort(this.tFFreq.getText()), 0);

			this.tileEntity.setFrequency(newFrequency);
			PacketDispatcher.sendPacketToServer(ICBMCore.PACKET_TILE.getPacket(this.tileEntity, 1, this.tileEntity.getFrequency()));
		}
		catch (NumberFormatException e)
		{

		}

		try
		{
			short newGaoDu = (short) Math.max(Math.min(Short.parseShort(this.tFGaoDu.getText()), Short.MAX_VALUE), 3);

			this.tileEntity.gaoDu = newGaoDu;
			PacketDispatcher.sendPacketToServer(ICBMCore.PACKET_TILE.getPacket(this.tileEntity, 3, this.tileEntity.gaoDu));
		}
		catch (NumberFormatException e)
		{

		}
	}

	/** Args: x, y, buttonClicked */
	@Override
	public void mouseClicked(int par1, int par2, int par3)
	{
		super.mouseClicked(par1, par2, par3);
		this.tFX.mouseClicked(par1 - containerWidth, par2 - containerHeight, par3);
		this.tFZ.mouseClicked(par1 - containerWidth, par2 - containerHeight, par3);

		if (tileEntity.getTier() >= 1)
		{
			this.tFY.mouseClicked(par1 - containerWidth, par2 - containerHeight, par3);
			this.tFGaoDu.mouseClicked(par1 - containerWidth, par2 - containerHeight, par3);

			if (tileEntity.getTier() > 1)
			{
				this.tFFreq.mouseClicked(par1 - containerWidth, par2 - containerHeight, par3);
			}
		}

	}

	/** Draw the foreground layer for the GuiContainer (everything in front of the items) */
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		this.tFX.drawTextBox();
		this.tFZ.drawTextBox();

		// Draw the air detonation GUI
		if (tileEntity.getTier() >= 1)
		{
			this.tFY.drawTextBox();
			this.fontRenderer.drawString(LanguageUtility.getLocal("gui.launcherScreen.detHeight"), 12, 68, 4210752);

			this.tFGaoDu.drawTextBox();
			this.fontRenderer.drawString(LanguageUtility.getLocal("gui.launcherScreen.lockHeight"), 12, 83, 4210752);

			if (tileEntity.getTier() > 1)
			{
				this.tFFreq.drawTextBox();
				this.fontRenderer.drawString(LanguageUtility.getLocal("gui.misc.freq"), 12, 98, 4210752);
			}
		}

		this.fontRenderer.drawString("", 45, 6, 4210752);
		this.fontRenderer.drawString("\u00a77" + LanguageUtility.getLocal("gui.launcherScreen.name"), 30, 6, 4210752);

		this.fontRenderer.drawString(LanguageUtility.getLocal("gui.launcherScreen.target"), 12, 25, 4210752);
		this.fontRenderer.drawString(LanguageUtility.getLocal("gui.misc.XCoord"), 25, 40, 4210752);
		this.fontRenderer.drawString(LanguageUtility.getLocal("gui.misc.ZCoord"), 25, 55, 4210752);

		int inaccuracy = 30;

		if (this.tileEntity.laucherBase != null)
		{
			if (this.tileEntity.laucherBase.supportFrame != null)
			{
				inaccuracy = this.tileEntity.laucherBase.supportFrame.getInaccuracy();
			}
		}

		this.fontRenderer.drawString(LanguageUtility.getLocal("gui.launcherScreen.inaccuracy").replaceAll("%p", "" + inaccuracy), 12, 113, 4210752);

		// Shows the status of the missile launcher
		this.fontRenderer.drawString(LanguageUtility.getLocal("gui.misc.status") + " " + this.tileEntity.getStatus(), 12, 125, 4210752);
		this.fontRenderer.drawString(LanguageUtility.getLocal("gui.misc.voltage") + " " + this.tileEntity.getVoltageInput(null) + "v", 12, 137, 4210752);
		this.fontRenderer.drawString(UnitDisplay.getDisplayShort(this.tileEntity.energy.getEnergy(), Unit.JOULES) + "/" + UnitDisplay.getDisplay(this.tileEntity.energy.getEnergyCapacity(), Unit.JOULES), 12, 150, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY)
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

		if (!this.tFX.isFocused())
			this.tFX.setText(Math.round(this.tileEntity.getTarget().x) + "");
		if (!this.tFZ.isFocused())
			this.tFZ.setText(Math.round(this.tileEntity.getTarget().z) + "");
		if (!this.tFY.isFocused())
			this.tFY.setText(Math.round(this.tileEntity.getTarget().y) + "");

		if (!this.tFGaoDu.isFocused())
			this.tFGaoDu.setText(this.tileEntity.gaoDu + "");

		if (!this.tFFreq.isFocused())
			this.tFFreq.setText(this.tileEntity.getFrequency() + "");
	}
}
