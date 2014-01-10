package icbm.sentry.gui;

import icbm.Reference;
import icbm.sentry.platform.ContainerTurretPlatform;
import icbm.sentry.platform.TileEntityTurretPlatform;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeDirection;

import org.lwjgl.opengl.GL11;

import universalelectricity.api.energy.UnitDisplay;
import universalelectricity.api.energy.UnitDisplay.Unit;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiPlatformSlots extends GuiPlatformBase
{
    public static final ResourceLocation TEXTURE = new ResourceLocation(Reference.DOMAIN, Reference.GUI_PATH + "gui_platform_slot.png");

    public GuiPlatformSlots(InventoryPlayer inventoryPlayer, TileEntityTurretPlatform tileEntity)
    {
        super(inventoryPlayer, new ContainerTurretPlatform(inventoryPlayer, tileEntity), tileEntity);
    }

    /** Draw the foreground layer for the GuiContainer (everything in front of the items) */
    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y)
    {
        this.fontRenderer.drawString("Ammunition", 8, 30, 4210752);

        String color = "\u00a74";

        if (((TileEntityTurretPlatform) this.tileEntity).isFunctioning())
        {
            color = "\u00a7a";
        }
       
        this.fontRenderer.drawString(color + UnitDisplay.getDisplayShort(Math.min(((TileEntityEnergyMachine) this.tileEntity).getEnergyStored(), ((TileEntityTurretPlatform) this.tileEntity).getEnergyStored()), Unit.JOULES), 87, 43, 4210752);
        this.fontRenderer.drawString(color + "of " + UnitDisplay.getDisplayShort(((TileEntityTurretPlatform) this.tileEntity).getEnergyCapacity(ForgeDirection.UNKNOWN), Unit.JOULES), 87, 53, 4210752);

        this.fontRenderer.drawString("Upgrades", 87, 66, 4210752);
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
