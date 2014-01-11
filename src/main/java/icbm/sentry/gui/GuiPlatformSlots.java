package icbm.sentry.gui;

import icbm.Reference;
import icbm.sentry.platform.ContainerTurretPlatform;
import icbm.sentry.platform.TileTurretPlatform;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import universalelectricity.api.energy.UnitDisplay;
import universalelectricity.api.energy.UnitDisplay.Unit;
import calclavia.lib.gui.GuiContainerBase;
import calclavia.lib.utility.LanguageUtility;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiPlatformSlots extends GuiContainerBase
{
    public static final ResourceLocation TEXTURE = new ResourceLocation(Reference.DOMAIN, Reference.GUI_PATH + "gui_platform_slot.png");
    private TileTurretPlatform tileEntity;

    public GuiPlatformSlots(InventoryPlayer inventoryPlayer, TileTurretPlatform tileEntity)
    {
        super(new ContainerTurretPlatform(inventoryPlayer, tileEntity));
        this.tileEntity = tileEntity;
    }

    /** Draw the foreground layer for the GuiContainer (everything in front of the items) */
    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y)
    {
        this.fontRenderer.drawString(LanguageUtility.getLocal("gui.platform.ammo"), 8, 30, 4210752);

        String color = "\u00a74";

        if (this.tileEntity.isFunctioning())
        {
            color = "\u00a7a";
        }

        this.fontRenderer.drawString(color + UnitDisplay.getDisplayShort(this.tileEntity.energy.getEnergy(), Unit.JOULES), 87, 43, 4210752);
        this.fontRenderer.drawString(color + LanguageUtility.getLocal("gui.platform.joules").replaceAll("%j", UnitDisplay.getDisplayShort(this.tileEntity.energy.getEnergyCapacity(), Unit.JOULES)), 87, 53, 4210752);

        this.fontRenderer.drawString(LanguageUtility.getLocal("gui.platform.upgrades"), 87, 66, 4210752);
        super.drawGuiContainerForegroundLayer(x, y);
    }

    /** Draw the background layer for the GuiContainer (everything behind the items) */
    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int x, int y)
    {
        super.drawGuiContainerBackgroundLayer(par1, x, y);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(TEXTURE);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        int containerWidth = (this.width - this.xSize) / 2;
        int containerHeight = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(containerWidth, containerHeight, 0, 0, this.xSize, this.ySize);
    }
}
