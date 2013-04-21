package icbm.wanyi.gui;

import icbm.api.IItemFrequency;
import icbm.core.ZhuYao;
import icbm.wanyi.WanYiPacketGuanLi.WanYiPacketType;
import icbm.wanyi.ZhuYaoWanYi;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import universalelectricity.prefab.GuiBase;
import universalelectricity.prefab.network.PacketManager;
import cpw.mods.fml.common.network.PacketDispatcher;

public class GShengBuo extends GuiBase
{
	private ItemStack itemStack;

	private GuiTextField textFieldFrequency;

	private int containerWidth;
	private int containerHeight;

	public GShengBuo(ItemStack par1ItemStack)
	{
		this.itemStack = par1ItemStack;
	}

	/**
	 * Adds the buttons (and other controls) to the screen in question.
	 */
	@Override
	public void initGui()
	{
		super.initGui();
		this.textFieldFrequency = new GuiTextField(fontRenderer, 80, 50, 40, 12);
		this.textFieldFrequency.setMaxStringLength(4);
		this.textFieldFrequency.setText(((IItemFrequency) this.itemStack.getItem()).getFrequency(this.itemStack) + "");
	}

	/**
	 * Call this method from you GuiScreen to process the keys into textbox.
	 */
	@Override
	public void keyTyped(char par1, int par2)
	{
		super.keyTyped(par1, par2);
		this.textFieldFrequency.textboxKeyTyped(par1, par2);

		try
		{
			short newFrequency = (short) Math.max(0, Short.parseShort(this.textFieldFrequency.getText()));
			this.textFieldFrequency.setText(newFrequency + "");

			if (((IItemFrequency) this.itemStack.getItem()).getFrequency(this.itemStack) != newFrequency)
			{
				((IItemFrequency) this.itemStack.getItem()).setFrequency(newFrequency, this.itemStack);
				PacketDispatcher.sendPacketToServer(PacketManager.getPacketWithID(ZhuYaoWanYi.CHANNEL, WanYiPacketType.HUO_LUAN.ordinal(), newFrequency));
			}
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
		this.textFieldFrequency.mouseClicked(par1 - containerWidth, par2 - containerHeight, par3);
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	@Override
	protected void drawForegroundLayer(int var2, int var3, float var1)
	{
		this.fontRenderer.drawString("\u00a77Frequency", 62, 6, 4210752);
		this.fontRenderer.drawString("Frequency:", 15, 52, 4210752);
		this.textFieldFrequency.drawTextBox();
	}

	/**
	 * Draw the background layer for the GuiContainer (everything behind the items)
	 */
	@Override
	protected void drawBackgroundLayer(int var2, int var3, float var1)
	{
		this.mc.renderEngine.bindTexture(ZhuYao.GUI_PATH + "gui_empty.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		this.containerWidth = (this.width - this.xSize) / 2;
		this.containerHeight = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(containerWidth, containerHeight, 0, 0, this.xSize, this.ySize);
	}
}
