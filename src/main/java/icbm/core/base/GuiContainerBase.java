package icbm.core.base;

import icbm.sentry.render.CalclaviaRenderHelper;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import universalelectricity.api.CompatibilityType;
import universalelectricity.api.energy.UnitDisplay;
import universalelectricity.api.energy.UnitDisplay.Unit;
import calclavia.lib.utility.LanguageUtility;

public class GuiContainerBase extends GuiContainer
{
	public ResourceLocation baseTexture;

	protected int meterX = 54;
	protected int meterHeight = 49;
	protected int meterWidth = 14;
	protected int meterEnd = meterX + meterWidth;

	protected int energyType = 0;

	public String tooltip = "";

	protected int containerWidth;
	protected int containerHeight;
	private float lastChangeFrameTime;

	public GuiContainerBase(Container container)
	{
		super(container);
		this.ySize = 217;
	}

	@Override
	public void onGuiClosed()
	{
		Keyboard.enableRepeatEvents(false);
		super.onGuiClosed();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int x, int y)
	{
		this.containerWidth = (this.width - this.xSize) / 2;
		this.containerHeight = (this.height - this.ySize) / 2;

		this.mc.renderEngine.bindTexture(this.baseTexture);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		this.drawTexturedModalRect(this.containerWidth, this.containerHeight, 0, 0, this.xSize, this.ySize);
	}

	protected void drawBulb(int x, int y, boolean isOn)
	{
		this.mc.renderEngine.bindTexture(this.baseTexture);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		if (isOn)
		{
			this.drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y, 161, 0, 6, 6);

		}
		else
		{
			this.drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y, 161, 4, 6, 6);
		}
	}

	protected void drawSlot(int x, int y, ItemStack itemStack)
	{
		this.mc.renderEngine.bindTexture(this.baseTexture);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		this.drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y, 0, 0, 18, 18);

		this.drawItemStack(itemStack, this.containerWidth + x, this.containerHeight + y);
	}

	protected void drawItemStack(ItemStack itemStack, int x, int y)
	{
		x += 1;
		y += 1;
		GL11.glTranslatef(0.0F, 0.0F, 32.0F);

		// drawTexturedModelRectFromIcon
		// GL11.glEnable(GL11.GL_BLEND);
		// GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		itemRenderer.renderItemAndEffectIntoGUI(this.fontRenderer, this.mc.renderEngine, itemStack, x, y);
		// GL11.glDisable(GL11.GL_BLEND);
	}

	protected void drawTextWithTooltip(String textName, String format, int x, int y, int mouseX, int mouseY)
	{
		this.drawTextWithTooltip(textName, format, x, y, mouseX, mouseY, 4210752);
	}

	protected void drawTextWithTooltip(String textName, String format, int x, int y, int mouseX, int mouseY, int color)
	{
		String name = LanguageUtility.getLocal("gui." + textName + ".name");
		String text = format.replaceAll("%1", name);
		this.fontRenderer.drawString(text, x, y, color);

		String tooltip = LanguageUtility.getLocal("gui." + textName + ".tooltip");

		if (tooltip != null && tooltip != "")
		{
			if (this.isPointInRegion(x, y, (int) (text.length() * 4.8), 12, mouseX, mouseY))
			{
				this.tooltip = tooltip;
			}
		}
	}

	protected void drawTextWithTooltip(String textName, int x, int y, int mouseX, int mouseY)
	{
		this.drawTextWithTooltip(textName, "%1", x, y, mouseX, mouseY);
	}
	public void renderUniversalDisplay(int x, int y, float energy, int mouseX, int mouseY, Unit unit)
	{
		String displaySuffix = "";

		if (unit == Unit.WATT)
		{
			displaySuffix = "/s";
		}

		String display = UnitDisplay.getDisplay(energy, unit);

		// Check different energy system types.
		if (unit == Unit.WATT || unit == Unit.JOULES)
		{
			switch (this.energyType)
			{
				case 1:
					display = UnitDisplay.roundDecimals(energy * CompatibilityType.BUILDCRAFT.ratio) + " MJ" + displaySuffix;
					break;
				case 2:
					display = UnitDisplay.roundDecimals(energy * CompatibilityType.INDUSTRIALCRAFT.ratio) + " EU" + displaySuffix;
					break;
				case 3:
					display = UnitDisplay.roundDecimals(energy * CompatibilityType.THERMAL_EXPANSION.ratio) + " RF" + displaySuffix;
					break;
			}
		}

		if (this.isPointInRegion(x, y, display.length() * 5, 9, mouseX, mouseY))
		{
			if (Mouse.isButtonDown(0) && this.lastChangeFrameTime <= 0)
			{
				this.energyType = (this.energyType + 1) % 4;
				this.lastChangeFrameTime = 30;
			}
			else
			{
				this.drawTooltip(mouseX - this.guiLeft, mouseY - this.guiTop + 10, "Click to change unit.");
			}
		}

		this.lastChangeFrameTime--;

		this.fontRenderer.drawString(display, x, y, 4210752);
	}

	public void drawTooltip(int x, int y, String... toolTips)
	{
		if (!GuiScreen.isShiftKeyDown())
		{
			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
			RenderHelper.disableStandardItemLighting();
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_DEPTH_TEST);

			if (toolTips != null)
			{
				int var5 = 0;
				int var6;
				int var7;

				for (var6 = 0; var6 < toolTips.length; ++var6)
				{
					var7 = this.fontRenderer.getStringWidth(toolTips[var6]);

					if (var7 > var5)
					{
						var5 = var7;
					}
				}

				var6 = x + 12;
				var7 = y - 12;

				int var9 = 8;

				if (toolTips.length > 1)
				{
					var9 += 2 + (toolTips.length - 1) * 10;
				}

				if (this.guiTop + var7 + var9 + 6 > this.height)
				{
					var7 = this.height - var9 - this.guiTop - 6;
				}

				this.zLevel = 300;
				int var10 = -267386864;
				this.drawGradientRect(var6 - 3, var7 - 4, var6 + var5 + 3, var7 - 3, var10, var10);
				this.drawGradientRect(var6 - 3, var7 + var9 + 3, var6 + var5 + 3, var7 + var9 + 4, var10, var10);
				this.drawGradientRect(var6 - 3, var7 - 3, var6 + var5 + 3, var7 + var9 + 3, var10, var10);
				this.drawGradientRect(var6 - 4, var7 - 3, var6 - 3, var7 + var9 + 3, var10, var10);
				this.drawGradientRect(var6 + var5 + 3, var7 - 3, var6 + var5 + 4, var7 + var9 + 3, var10, var10);
				int var11 = 1347420415;
				int var12 = (var11 & 16711422) >> 1 | var11 & -16777216;
				this.drawGradientRect(var6 - 3, var7 - 3 + 1, var6 - 3 + 1, var7 + var9 + 3 - 1, var11, var12);
				this.drawGradientRect(var6 + var5 + 2, var7 - 3 + 1, var6 + var5 + 3, var7 + var9 + 3 - 1, var11, var12);
				this.drawGradientRect(var6 - 3, var7 - 3, var6 + var5 + 3, var7 - 3 + 1, var11, var11);
				this.drawGradientRect(var6 - 3, var7 + var9 + 2, var6 + var5 + 3, var7 + var9 + 3, var12, var12);

				for (int var13 = 0; var13 < toolTips.length; ++var13)
				{
					String var14 = toolTips[var13];

					this.fontRenderer.drawStringWithShadow(var14, var6, var7, -1);
					var7 += 10;
				}

				this.zLevel = 0;

				GL11.glEnable(GL11.GL_DEPTH_TEST);
				GL11.glEnable(GL11.GL_LIGHTING);
				RenderHelper.enableGUIStandardItemLighting();
				GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			}
		}
	}

	/**
	 * Based on BuildCraft
	 */
	protected void displayGauge(int j, int k, int line, int col, int width, int squaled, FluidStack liquid)
	{
		squaled -= 1;

		if (liquid == null)
		{
			return;
		}

		int start = 0;

		Icon liquidIcon = null;
		Fluid fluid = liquid.getFluid();

		if (fluid != null && fluid.getStillIcon() != null)
		{
			liquidIcon = fluid.getStillIcon();
		}

		CalclaviaRenderHelper.setSpriteTexture(fluid.getSpriteNumber());

		if (liquidIcon != null)
		{
			while (true)
			{
				int x;

				if (squaled > 16)
				{
					x = 16;
					squaled -= 16;
				}
				else
				{
					x = squaled;
					squaled = 0;
				}

				this.drawTexturedModelRectFromIcon(j + col, k + line + 58 - x - start, liquidIcon, width, 16 - (16 - x));
				start = start + 16;

				if (x == 0 || squaled == 0)
				{
					break;
				}
			}
		}
	}
}
