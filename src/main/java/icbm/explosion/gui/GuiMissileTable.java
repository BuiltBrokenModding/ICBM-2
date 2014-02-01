package icbm.explosion.gui;

import calclavia.lib.utility.LanguageUtility;
import cpw.mods.fml.client.FMLClientHandler;
import icbm.Reference;
import icbm.explosion.container.ContainerMissileTable;
import icbm.explosion.missile.modular.TileMissileAssembler;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

public class GuiMissileTable extends GuiContainer
{
    public static final ResourceLocation TEXTURE = new ResourceLocation(Reference.DOMAIN, Reference.GUI_PATH + "gui_launcher.png");

    private int containerWidth;
    private int containerHeight;

    private TileMissileAssembler tileEntity;

    public GuiMissileTable(InventoryPlayer par1InventoryPlayer, TileMissileAssembler tileEntity)
    {
        super(new ContainerMissileTable(par1InventoryPlayer, tileEntity));
        this.tileEntity = tileEntity;
    }

    /** Draw the foreground layer for the GuiContainer (everything in front of the items) */
    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2)
    {
        this.fontRenderer.drawString("\u00a77" + tileEntity.getInvName(), 48, 6, 4210752);
        this.fontRenderer.drawString(LanguageUtility.getLocal("gui.launcherBase.place"), 63, 28, 4210752);
        this.fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
    }

    /** Draw the background layer for the GuiContainer (everything behind the items) */
    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
    {
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(TEXTURE);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        containerWidth = (this.width - this.xSize) / 2;
        containerHeight = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(containerWidth, containerHeight, 0, 0, this.xSize, this.ySize);
    }
}
