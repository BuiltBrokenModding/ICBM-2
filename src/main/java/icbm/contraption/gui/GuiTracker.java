package icbm.contraption.gui;

import icbm.Reference;
import icbm.api.IItemFrequency;
import icbm.core.ICBMCore;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import calclavia.lib.gui.GuiBase;
import calclavia.lib.utility.LanguageUtility;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.PacketDispatcher;

public class GuiTracker extends GuiBase
{
	public static final ResourceLocation TEXTURE = new ResourceLocation(Reference.DOMAIN, Reference.GUI_PATH + "gui_empty.png");

	private ItemStack itemStack;

	private GuiTextField textFieldFrequency;

	private int containerWidth;
	private int containerHeight;
	private EntityPlayer player;

	public GuiTracker(EntityPlayer player, ItemStack par1ItemStack)
	{
		this.player = player;
		this.itemStack = par1ItemStack;
	}

	/** Adds the buttons (and other controls) to the screen in question. */
	@Override
	public void initGui()
	{
		super.initGui();
		this.textFieldFrequency = new GuiTextField(fontRenderer, 80, 50, 40, 12);
		this.textFieldFrequency.setMaxStringLength(4);
		this.textFieldFrequency.setText(((IItemFrequency) this.itemStack.getItem()).getFrequency(this.itemStack) + "");
	}

	/** Call this method from you GuiScreen to process the keys into textbox. */
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
				PacketDispatcher.sendPacketToServer(ICBMCore.PACKET_ITEM.getPacket(this.player, -1, newFrequency));
			}
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
		this.textFieldFrequency.mouseClicked(par1 - containerWidth, par2 - containerHeight, par3);
	}

	/** Draw the foreground layer for the GuiContainer (everything in front of the items) */
	@Override
	protected void drawForegroundLayer(int var2, int var3, float var1)
	{
		this.fontRenderer.drawString("\u00a77"+LanguageUtility.getLocal("gui.tracker.freq"), 62, 6, 4210752);
		this.fontRenderer.drawString(LanguageUtility.getLocal("gui.tracker.freq") + ":", 15, 52, 4210752);
		this.textFieldFrequency.drawTextBox();
	}

	/** Draw the background layer for the GuiContainer (everything behind the items) */
	@Override
	protected void drawBackgroundLayer(int var2, int var3, float var1)
	{
		FMLClientHandler.instance().getClient().renderEngine.bindTexture(TEXTURE);

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		this.containerWidth = (this.width - this.xSize) / 2;
		this.containerHeight = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(containerWidth, containerHeight, 0, 0, this.xSize, this.ySize);
	}

}
