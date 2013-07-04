package icbm.gangshao.shimian;

import icbm.core.ZhuYaoBase;
import icbm.gangshao.access.AccessLevel;
import icbm.gangshao.access.UserAccess;
import icbm.gangshao.platform.TPaoTaiZhan;
import icbm.gangshao.terminal.TileEntityTerminal;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import universalelectricity.core.vector.Vector2;
import universalelectricity.prefab.vector.Region2;
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
	private GuiTextField commandLine;
	private int scroll = 0;
	private static final int SPACING = 10;
	private final HashMap<UserAccess, Vector2> outputMap = new HashMap<UserAccess, Vector2>();

	public GuiPlatformAccess(EntityPlayer entityPlayer, TPaoTaiZhan tileEntity)
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

			for (UserAccess access : this.tileEntity.getUsers())
			{
				if (access.username.equalsIgnoreCase(username))
				{
					command = "users remove";
					break;
				}
			}

			this.tileEntity.sendCommandToServer(this.entityPlayer, command + " " + username);
			this.commandLine.setText("");
		}
		else
		{
			this.commandLine.textboxKeyTyped(character, keycode);
		}
	}

	@Override
	protected void mouseClicked(int x, int y, int type)
	{
		super.mouseClicked(x, y, type);

		if (type == 0)
		{
			Iterator<Entry<UserAccess, Vector2>> it = this.outputMap.entrySet().iterator();

			while (it.hasNext())
			{
				Entry<UserAccess, Vector2> entry = it.next();
				Vector2 minPos = entry.getValue();
				minPos.x -= 2;
				minPos.y -= 2;
				Vector2 maxPos = minPos.clone();
				maxPos.x += 132;
				maxPos.y += SPACING + 2;

				if (new Region2(minPos, maxPos).isIn(new Vector2(x - this.guiLeft, y - this.guiTop)))
				{
					UserAccess access = entry.getKey();
					int newLevelOrdinal = access.level.ordinal() + 1;

					if (newLevelOrdinal >= AccessLevel.values().length)
					{
						newLevelOrdinal -= AccessLevel.values().length;
					}

					if (newLevelOrdinal <= 0)
					{
						newLevelOrdinal = 1;
					}

					AccessLevel newLevel = AccessLevel.get(newLevelOrdinal);
					this.tileEntity.sendCommandToServer(this.entityPlayer, "access set " + access.username + " " + newLevel.displayName);
					break;
				}
			}
		}

		this.commandLine.mouseClicked(x, y, type);
	}

	@Override
	protected void drawForegroundLayer(int x, int y, float var1)
	{
		String title = "User Access";
		this.fontRenderer.drawString("\u00a77" + title, this.xSize / 2 - title.length() * 3, 4, 4210752);
		this.drawConsole(15, 25, TileEntityTerminal.SCROLL_SIZE);
		super.drawForegroundLayer(x, y, var1);
	}

	public void drawConsole(int x, int y, int lines)
	{
		int color = 14737632;
		outputMap.clear();

		// Draws Each Line
		for (int i = 0; i < lines; i++)
		{
			int currentLine = i + this.getScroll();

			if (currentLine < this.tileEntity.getUsers().size() && currentLine >= 0)
			{
				UserAccess accesInfo = this.tileEntity.getUsers().get(currentLine);
				String line = accesInfo.username + " (" + accesInfo.level.displayName + ")";

				if (line != null && line != "")
				{
					Vector2 drawPosition = new Vector2(x, SPACING * i + y);
					outputMap.put(accesInfo, drawPosition);
					this.fontRenderer.drawString(line, drawPosition.intX(), drawPosition.intY(), color);
				}
			}
		}
	}

	@Override
	protected void drawBackgroundLayer(int x, int y, float var1)
	{
		super.drawBackgroundLayer(x, y, var1);
		this.mc.renderEngine.bindTexture(ZhuYaoBase.GUI_PATH + "gui_platform_terminal.png");
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
		this.scroll = Math.max(Math.min(length, this.tileEntity.getUsers().size()), 0);
	}

	@Override
	public int getScroll()
	{
		return this.scroll;
	}
}