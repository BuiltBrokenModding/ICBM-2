package icbm.sentry.gui;

import icbm.sentry.ICBMSentry;
import icbm.sentry.terminal.TileEntityTerminal;
import icbm.sentry.terminal.TileEntityTerminal.PacketType;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.StringTranslate;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import universalelectricity.prefab.GuiBase;
import universalelectricity.prefab.network.PacketManager;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * The console GUI used by turrets.
 * 
 * @author Darkguardsman, Edited by Calclavia
 * 
 */
@SideOnly(Side.CLIENT)
public class GuiTerminal extends GuiBase
{
	private TileEntityTerminal tileEntity;
	private GuiTextField commandLine;
	private EntityPlayer entityPlayer;
	private String linkErrorA = "";
	private IInventory iInventory;
	private int usedLines = 0;

	public GuiTerminal(EntityPlayer invPlayer, TileEntityTerminal tileEntity)
	{
		this.tileEntity = tileEntity;
		this.entityPlayer = invPlayer;
		this.xSize = 200;
	}

	@Override
	public void initGui()
	{
		super.initGui();
		StringTranslate var1 = StringTranslate.getInstance();
		this.controlList.clear();
		int wid = (this.width - this.xSize) / 2;
		int hig = (this.height - this.ySize) / 2;
		this.commandLine = new GuiTextField(this.fontRenderer, wid + 7, hig + 150, 135, 11);
		this.controlList.add(new GuiButtonArrow(0, wid + 146, hig + 7, false));
		this.controlList.add(new GuiButtonArrow(1, wid + 146, hig + 138, true));
		// ---------
		this.commandLine.setMaxStringLength(30);
		PacketDispatcher.sendPacketToServer(PacketManager.getPacket(ICBMSentry.CHANNEL, this.tileEntity, PacketType.GUI_EVENT.ordinal(), true));
		Keyboard.enableRepeatEvents(true);
	}

	@Override
	public void onGuiClosed()
	{
		super.onGuiClosed();
		PacketDispatcher.sendPacketToServer(PacketManager.getPacket(ICBMSentry.CHANNEL, this.tileEntity, PacketType.GUI_EVENT.ordinal(), false));
		Keyboard.enableRepeatEvents(false);
	}

	@Override
	public void updateScreen()
	{
		super.updateScreen();
		this.commandLine.setFocused(true);
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
			{
				// Arrow Up
				this.tileEntity.scrollUp(1);
				break;
			}
			case 1:
			{
				// Arrow Down
				this.tileEntity.scrollDown(1);
				break;
			}
		}
	}

	@Override
	protected void keyTyped(char character, int keycode)
	{
		if (keycode == Keyboard.KEY_ESCAPE)
		{
			this.mc.thePlayer.closeScreen();
		}
		else if (keycode == 200) // PAGE UP (no constant)
		{
			this.tileEntity.scrollUp(1);
		}
		else if (keycode == 208) // PAGE DOWN (no constant)
		{
			this.tileEntity.scrollDown(1);
		}
		else if (keycode == Keyboard.KEY_RETURN)
		{
			this.tileEntity.sendCommandToServer(this.entityPlayer, this.commandLine.getText());
			this.commandLine.setText("");
		}
		else
		{
			this.commandLine.textboxKeyTyped(character, keycode);
		}
	}

	@Override
	protected void mouseClicked(int par1, int par2, int par3)
	{
		super.mouseClicked(par1, par2, par3);
		this.commandLine.mouseClicked(par1, par2, par3);
	}

	@Override
	protected void drawForegroundLayer(int var2, int var3, float var1)
	{
		this.drawConsole(10, 10, TileEntityTerminal.SCROLL_SIZE);
	}

	public void drawConsole(int height, int width, int lines)
	{
		int spacing = 10;
		int color = 14737632;

		// Draws each line
		for (int i = 0; i < lines; i++)
		{
			int currentLine = i + this.tileEntity.getScroll();

			if (currentLine < this.tileEntity.getTerminalOuput().size() && currentLine >= 0)
			{
				String line = this.tileEntity.getTerminalOuput().get(currentLine);

				if (line != null && line != "")
				{
					this.fontRenderer.drawString(line, width, spacing * i + height, color);
				}
			}
		}
	}

	@Override
	protected void drawBackgroundLayer(int var2, int var3, float var1)
	{
		int var4 = this.mc.renderEngine.getTexture(ICBMSentry.TEXTURE_PATH + "gui_cmd.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture(var4);
		int var5 = (this.width - this.xSize) / 2;
		int var6 = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(var5, var6, 0, 0, this.xSize, this.ySize);
		this.commandLine.drawTextBox();
	}
}