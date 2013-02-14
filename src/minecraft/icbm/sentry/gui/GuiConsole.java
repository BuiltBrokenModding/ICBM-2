package icbm.sentry.gui;

import icbm.sentry.ICBMSentry;
import icbm.sentry.terminal.CmdHandler;
import icbm.sentry.terminal.TileEntityConsole;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.StringTranslate;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * The console GUI used by turrets.
 * 
 * @author Darkguardsman, Calclavia
 * 
 */
@SideOnly(Side.CLIENT)
public class GuiConsole extends GuiBase
{
	private TileEntityConsole tileEntity;
	private GuiTextField commandLine;
	private EntityPlayer entityPlayer;
	private String linkErrorA = "";
	private IInventory iInventory;
	private String cmdInput;
	private int scroll = 0;
	private int usedLines = 0;
	private String[] printLine = new String[400];

	public GuiConsole(EntityPlayer invPlayer, TileEntityConsole tileEntity)
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
		this.addToConsole("Sentry Console");
		this.addToConsole("---------------------");
		this.commandLine.setText("/");
	}

	/**
	 * Adds a line to the console.
	 * 
	 * @param msg
	 */
	public void addToConsole(String msg)
	{
		// TODO limit to 21 chars
		for (int i = 0; i < printLine.length; i++)
		{
			String ll = printLine[i];
			boolean flag = false;
			if (ll == null)
			{
				flag = true;
			}
			else if (ll.equalsIgnoreCase(""))
			{
				flag = true;
			}
			if (flag)
			{
				if (msg.length() > 23)
				{
					msg = msg.substring(0, 22);
				}
				printLine[i] = msg;
				++usedLines;
				if (usedLines > 10)
				{
					this.scroll++;
				}
				break;
			}
			else
			{
				/**
				 * if reached max of array move the array down 50, removing the first 50 in the
				 * process
				 */
				if (i == this.printLine.length - 1)
				{
					/**
					 * Reset the search by 50,+2 to be since some lines, just freed up
					 */
					i = Math.max(printLine.length - 52, 0);
					for (int l = 0; l < printLine.length; l++)
					{
						printLine[i] = printLine[i + 50];
					}
				}
			}

		}
	}

	@Override
	public void updateScreen()
	{
		this.commandLine.updateCursorCounter();
		cmdInput = commandLine.getText();
	}

	/**
	 * Fired when a control is clicked. This is the equivalent of
	 * ActionListener.actionPerformed(ActionEvent e).
	 */
	@Override
	protected void actionPerformed(GuiButton par1GuiButton)
	{
		this.cmdInput = commandLine.getText();

		switch (par1GuiButton.id)
		{
			case 0:
			{
				// Arrow Up
				if (this.scroll > 0)
				{
					this.scroll--;
				}
				else
				{
					this.scroll = 0;
				}
				break;
			}
			case 1:
			{
				// Arrow Down
				if (this.printLine.length > (this.scroll + 1))
				{
					this.scroll++;
				}
				break;
			}
		}
	}

	@Override
	protected void keyTyped(char par1, int par2)
	{
		if (par2 == Keyboard.KEY_ESCAPE)
		{
			this.mc.thePlayer.closeScreen();
		}

		this.commandLine.textboxKeyTyped(par1, par2);

		if (par2 == 28)
		{
			CmdHandler.onCmd(this.entityPlayer, tileEntity, this, this.cmdInput);
			this.cmdInput = "";
			this.commandLine.setText("");
		}
	}

	@Override
	protected void mouseClicked(int par1, int par2, int par3)
	{
		super.mouseClicked(par1, par2, par3);
		this.commandLine.mouseClicked(par1, par2, par3);
	}

	@Override
	public void onGuiClosed()
	{
		super.onGuiClosed();
		Keyboard.enableRepeatEvents(false);
	}

	@Override
	protected void drawForegroundLayer()
	{
		this.drawConsole(10, 10, 14);
	}

	public void drawConsole(int x, int y, int lines)
	{
		int hight = y;
		int width = x;
		int spacing = 10;
		int color = 14737632;
		String[] display = new String[lines];
		// writes the current page to display array
		try
		{
			for (int u = 0; u < lines; u++)
			{
				int currentLine = u + this.scroll;
				String ll = printLine[((int) u + this.scroll)];
				if (ll != null && currentLine < printLine.length && !ll.equalsIgnoreCase(""))
				{
					display[u] = ll;
				}
				else
				{
					display[u] = "-";
				}
			}
			// draws display array to screen
			for (int i = 0; i < display.length; i++)
			{
				this.fontRenderer.drawString(display[i], width, spacing * i + hight, color);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	protected void drawBackgroundLayer(float var1, int var2, int var3)
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