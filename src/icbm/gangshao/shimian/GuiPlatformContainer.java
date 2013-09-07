package icbm.gangshao.shimian;

import icbm.core.ZhuYaoICBM;
import icbm.gangshao.CommonProxy;
import icbm.gangshao.ZhuYaoGangShao;
import icbm.gangshao.container.ContainerTurretPlatform;
import icbm.gangshao.platform.TPaoTaiZhan;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * A base class for all ICBM Sentry GUIs with containers.
 * 
 * @author Calclavia
 */
@SideOnly(Side.CLIENT)
public abstract class GuiPlatformContainer extends GuiContainer
{
	public static final ResourceLocation TEXTURE = new ResourceLocation(ZhuYaoICBM.DOMAIN, ZhuYaoICBM.GUI_PATH + "gui_base.png");

	protected static final int MAX_BUTTON_ID = 3;
	protected TPaoTaiZhan tileEntity;
	protected EntityPlayer entityPlayer;

	public GuiPlatformContainer(InventoryPlayer inventoryPlayer, TPaoTaiZhan tileEntity)
	{
		super(new ContainerTurretPlatform(inventoryPlayer, tileEntity));
		this.tileEntity = tileEntity;
		this.entityPlayer = inventoryPlayer.player;
		this.ySize = 380 / 2;
	}

	@Override
	public void initGui()
	{
		super.initGui();
		this.buttonList.clear();
		// Terminal
		this.buttonList.add(new GuiButtonImage(0, (this.width - this.xSize) / 2 - 22, (this.height - this.ySize) / 2 + 0, 3));
		// Access
		this.buttonList.add(new GuiButtonImage(1, (this.width - this.xSize) / 2 - 22, (this.height - this.ySize) / 2 + 22, 0));
		// Ammunition
		this.buttonList.add(new GuiButtonImage(2, (this.width - this.xSize) / 2 - 22, (this.height - this.ySize) / 2 + 44, 2));
		// Protection
		// this.buttonList.add(new GuiButtonImage(3, (this.width - this.xSize) /
		// 2 - 22,
		// (this.height - this.ySize) / 2 + 66, 1));
	}

	@Override
	public void updateScreen()
	{
		super.updateScreen();

		if (this.tileEntity.getTurret() == null)
		{
			this.mc.thePlayer.closeScreen();
		}
	}

	@Override
	protected void actionPerformed(GuiButton button)
	{
		if (tileEntity.getTurret() != null)
		{

			switch (button.id)
			{
				case 0:
				{
					this.entityPlayer.openGui(ZhuYaoGangShao.instance, CommonProxy.GUI_PLATFORM_TERMINAL_ID, this.tileEntity.worldObj, this.tileEntity.xCoord, this.tileEntity.yCoord, this.tileEntity.zCoord);
					break;
				}
				case 1:
				{
					this.entityPlayer.openGui(ZhuYaoGangShao.instance, CommonProxy.GUI_PLATFORM_ACCESS_ID, this.tileEntity.worldObj, this.tileEntity.xCoord, this.tileEntity.yCoord, this.tileEntity.zCoord);
					break;
				}
				case 2:
				{
					this.entityPlayer.openGui(ZhuYaoGangShao.instance, CommonProxy.GUI_PLATFORM_ID, this.tileEntity.worldObj, this.tileEntity.xCoord, this.tileEntity.yCoord, this.tileEntity.zCoord);
					break;
				}
				case 3:
				{
					// TODO: User Settings.
				}

			}
		}
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y)
	{
		if (this.tileEntity.getTurret() != null)
		{
			String title = this.tileEntity.getTurret().getName();
			this.fontRenderer.drawString("\u00a77" + title, (int) (this.xSize / 2 - title.length() * 2.5), 4, 4210752);

			/** Render Tool Tips */
			if (((GuiButtonImage) this.buttonList.get(0)).isIntersect(x, y))
			{
				this.drawTooltip(x - this.guiLeft, y - this.guiTop + 10, "Terminal");
			}
			else if (((GuiButtonImage) this.buttonList.get(1)).isIntersect(x, y))
			{
				this.drawTooltip(x - this.guiLeft, y - this.guiTop + 10, "Access");
			}
			else if (((GuiButtonImage) this.buttonList.get(2)).isIntersect(x, y))
			{
				this.drawTooltip(x - this.guiLeft, y - this.guiTop + 10, "Ammunition");
			}/*
			 * else if (((GuiButtonImage) this.buttonList.get(3)).isIntersect(x, y)) {
			 * this.drawTooltip(x - this.guiLeft, y - this.guiTop + 10, "Protection"); }
			 */
		}
	}

	/**
	 * Draw the background layer for the GuiContainer (everything behind the items)
	 */
	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int x, int y)
	{
		FMLClientHandler.instance().getClient().renderEngine.bindTexture(TEXTURE);

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		int containerWidth = (this.width - this.xSize) / 2;
		int containerHeight = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(containerWidth, containerHeight, 0, 0, this.xSize, this.ySize);
	}

	public void drawTooltip(int x, int y, String... toolTips)
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

			this.zLevel = 300.0F;
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
				String var14 = "\u00a77" + toolTips[var13];

				this.fontRenderer.drawStringWithShadow(var14, var6, var7, -1);

				if (var13 == 0)
				{
					var7 += 2;
				}

				var7 += 10;
			}

			this.zLevel = 0.0F;
		}
	}
}
