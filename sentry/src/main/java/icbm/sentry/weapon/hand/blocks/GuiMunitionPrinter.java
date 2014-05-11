package icbm.sentry.weapon.hand.blocks;

import icbm.Reference;
import icbm.sentry.ICBMSentry;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import universalelectricity.api.energy.UnitDisplay;
import universalelectricity.api.energy.UnitDisplay.Unit;
import calclavia.lib.utility.LanguageUtility;
import cpw.mods.fml.client.FMLClientHandler;

public class GuiMunitionPrinter extends GuiContainer {
	public static final ResourceLocation TEXTURE = new ResourceLocation(Reference.DOMAIN, Reference.GUI_PATH + "gui_munition_printer.png");

	private TileMunitionPrinter tileEntity;

	private int containerWidth;
	private int containerHeight;

	public GuiMunitionPrinter(InventoryPlayer inventory, TileMunitionPrinter tileEntity) {
		super(new ContainerMunitionPrinter(inventory, tileEntity));
		this.tileEntity = tileEntity;
	}

	@Override
	public void initGui() {
		super.initGui();
		containerWidth = (this.width - this.xSize) / 2;
		containerHeight = (this.height - this.ySize) / 2;
		
		buttonList.clear();
		buttonList.add(new GuiWeaponButton(0, containerWidth + 8, containerHeight + 7, ICBMSentry.itemAssaultRifle));
		buttonList.add(new GuiWeaponButton(1, containerWidth + 8, containerHeight + 25, ICBMSentry.itemSniperRifle));
		buttonList.add(new GuiWeaponButton(2, containerWidth + 8, containerHeight + 43, ICBMSentry.itemShotgun));
	}

	@Override
	public void keyTyped(char par1, int par2) {
		super.keyTyped(par1, par2);
	}

	/** Args: x, y, buttonClicked */
	@Override
	public void mouseClicked(int par1, int par2, int par3) {
		super.mouseClicked(par1, par2, par3);
	}

	/** Draw the foreground layer for the GuiContainer (everything in front of the items) */
	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		this.fontRenderer.drawString("\u00a77" + tileEntity.getInvName(), 94, 6, 4210752);

		this.fontRenderer.drawString(this.tileEntity.getVoltageInput(null) + "v", 105, 60, 4210752);
		this.fontRenderer.drawString(UnitDisplay.getDisplayShort(this.tileEntity.getEnergyHandler().getEnergy(), Unit.JOULES) + "/"
				+ UnitDisplay.getDisplayShort(this.tileEntity.getEnergyHandler().getEnergyCapacity(), Unit.JOULES), 105, 70, 4210752);
	}

	/** Draw the background layer for the GuiContainer (everything behind the items) */
	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
		FMLClientHandler.instance().getClient().renderEngine.bindTexture(TEXTURE);

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		containerWidth = (this.width - this.xSize) / 2;
		containerHeight = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(containerWidth, containerHeight, 0, 0, this.xSize, this.ySize);
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
	}
}
