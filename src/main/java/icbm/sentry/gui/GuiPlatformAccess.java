package icbm.sentry.gui;

import icbm.Reference;
import icbm.sentry.platform.TileTurretPlatform;

import java.util.HashMap;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import universalelectricity.api.vector.Vector2;
import calclavia.lib.access.AccessUser;
import calclavia.lib.terminal.TileTerminal;
import calclavia.lib.utility.LanguageUtility;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * The GUI for user permissions and access.
 * 
 * @author Calclavia
 */
@SideOnly(Side.CLIENT)
public class GuiPlatformAccess extends GuiPlatformBase implements IScroll
{
	public static final ResourceLocation TEXTURE = new ResourceLocation(Reference.DOMAIN, Reference.GUI_PATH + "gui_platform_terminal.png");

	private GuiTextField commandLine;
	private int scroll = 0;
	private static final int SPACING = 10;
	private final HashMap<AccessUser, Vector2> outputMap = new HashMap<AccessUser, Vector2>();

	public GuiPlatformAccess(InventoryPlayer entityPlayer, TileTurretPlatform tileEntity)
	{
		super(entityPlayer, tileEntity);
	}

	@Override
	public void initGui()
	{
		super.initGui();
		int width = (this.width - this.xSize) / 2;
		int height = (this.height - this.ySize) / 2;

		this.commandLine = new GuiTextField(this.fontRenderer, width + 12, height + 165, 135, 11);
		this.commandLine.setMaxStringLength(30);

		this.buttonList.add(new GuiButtonArrow(MAX_BUTTON_ID + 1, width + 151, height + 21, false));
		this.buttonList.add(new GuiButtonArrow(MAX_BUTTON_ID + 2, width + 151, height + 152, true));
		Keyboard.enableRepeatEvents(true);
	}

	@Override
	public void onGuiClosed()
	{
		super.onGuiClosed();
		Keyboard.enableRepeatEvents(false);
	}

	@Override
	public void updateScreen()
	{
		super.updateScreen();
		this.commandLine.setFocused(true);
	}

	@Override
	public void handleMouseInput()
	{
		super.handleMouseInput();

		/** Scroll based on mouse wheel. */
		int wheel = Mouse.getEventDWheel();
		if (wheel > 0)
		{
			this.scroll(-2);
		}
		else if (wheel < 0)
		{
			this.scroll(2);
		}
	}

	@Override
	protected void actionPerformed(GuiButton button)
	{
		super.actionPerformed(button);

		switch (button.id)
		{
			case MAX_BUTTON_ID + 1:
			{
				// Arrow Up
				this.scroll(-1);
				break;
			}
			case MAX_BUTTON_ID + 2:
			{
				// Arrow Down
				this.scroll(1);
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
			this.scroll(-1);
		}
		else if (keycode == 208) // PAGE DOWN (no constant)
		{
			this.scroll(1);
		}
		else if (keycode == Keyboard.KEY_RETURN)
		{
			String command = "users add";
			String username = this.commandLine.getText();

			/*
			 * for (AccessUser access : this.tileEntity.getUsers())
			 * {
			 * if (access.getName().equalsIgnoreCase(username))
			 * {
			 * command = "users remove";
			 * break;
			 * }
			 * }
			 */

			((TileTerminal) this.tileEntity).sendCommandToServer(this.entityPlayer, command + " " + username);
			this.commandLine.setText("");
		}
		else
		{
			this.commandLine.textboxKeyTyped(character, keycode);
		}
	}

	@Override
	protected void drawForegroundLayer(int mouseX, int mouseY, float var1)
	{
		String title = LanguageUtility.getLocal("gui.platform.access");
		this.fontRenderer.drawString("\u00a77" + title, this.xSize / 2 - title.length() * 3, 4, 4210752);
		this.drawConsole(15, 25, TileTerminal.SCROLL_SIZE);
	}

	public void drawConsole(int x, int y, int lines)
	{
		int color = 14737632;
		outputMap.clear();

		// Draws Each Line
		/*
		 * for (int i = 0; i < lines; i++)
		 * {
		 * int currentLine = i + this.getScroll();
		 * if (currentLine < this.tileEntity.getUsers().size() && currentLine >= 0)
		 * {
		 * AccessUser accesInfo = this.tileEntity.getUsers().get(currentLine);
		 * String line = accesInfo.getName();
		 * if (line != null && line != "")
		 * {
		 * Vector2 drawPosition = new Vector2(x, SPACING * i + y);
		 * outputMap.put(accesInfo, drawPosition);
		 * this.fontRenderer.drawString(line, drawPosition.intX(), drawPosition.intY(), color);
		 * }
		 * }
		 * }
		 */
	}

	@Override
	protected void drawBackgroundLayer(int mouseX, int mouseY, float var1)
	{
		super.drawBackgroundLayer(mouseX, mouseY, var1);
		FMLClientHandler.instance().getClient().renderEngine.bindTexture(TEXTURE);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		int var5 = (this.width - this.xSize) / 2;
		int var6 = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(var5, var6, 0, 0, this.xSize, this.ySize);
		this.commandLine.drawTextBox();
	}

	@Override
	public void scroll(int amount)
	{
		this.setScroll(this.scroll + amount);
	}

	@Override
	public void setScroll(int length)
	{
		// this.scroll = Math.max(Math.min(length, this.tileEntity.getUsers().size()), 0);
	}

	@Override
	public int getScroll()
	{
		return this.scroll;
	}
}