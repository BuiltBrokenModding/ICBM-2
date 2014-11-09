package icbm.explosion.gui;

import icbm.Reference;
import icbm.core.ICBMCore;
import icbm.explosion.container.ContainerCruiseLauncher;
import icbm.explosion.machines.TileCruiseLauncher;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import resonant.lib.utility.LanguageUtility;
import universalelectricity.api.energy.UnitDisplay;
import universalelectricity.api.energy.UnitDisplay.Unit;
import resonant.lib.transform.vector.Vector3;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.PacketDispatcher;

public class GuiCruiseLauncher extends GuiContainer
{
    public static final ResourceLocation TEXTURE = new ResourceLocation(Reference.DOMAIN, Reference.GUI_PATH + "gui_cruise_launcher.png");

    private TileCruiseLauncher tileEntity;
    private GuiTextField textFieldX;
    private GuiTextField textFieldZ;
    private GuiTextField textFieldY;
    private GuiTextField textFieldFreq;

    private int containerWidth;
    private int containerHeight;

    public GuiCruiseLauncher(InventoryPlayer par1InventoryPlayer, TileCruiseLauncher tileEntity)
    {
        super(new ContainerCruiseLauncher(par1InventoryPlayer, tileEntity));
        this.tileEntity = tileEntity;
    }

    @Override
    public void initGui()
    {
        super.initGui();
        this.textFieldX = new GuiTextField(fontRenderer, 20, 21, 35, 12);
        this.textFieldY = new GuiTextField(fontRenderer, 20, 37, 35, 12);
        this.textFieldZ = new GuiTextField(fontRenderer, 20, 52, 35, 12);
        this.textFieldFreq = new GuiTextField(fontRenderer, 70, 33, 35, 12);
        this.textFieldFreq.setMaxStringLength(4);
        this.textFieldX.setMaxStringLength(6);
        this.textFieldZ.setMaxStringLength(6);
        this.textFieldY.setMaxStringLength(6);

        this.textFieldFreq.setText(this.tileEntity.getFrequency() + "");

        if (this.tileEntity.getTarget() == null)
        {
            this.textFieldX.setText(Math.round(this.tileEntity.xCoord) + "");
            this.textFieldZ.setText(Math.round(this.tileEntity.zCoord) + "");
            this.textFieldY.setText(Math.round(this.tileEntity.yCoord) + "");
        }
        else
        {
            this.textFieldX.setText(Math.round(this.tileEntity.getTarget().x) + "");
            this.textFieldZ.setText(Math.round(this.tileEntity.getTarget().z) + "");
            this.textFieldY.setText(Math.round(this.tileEntity.getTarget().y) + "");
        }
    }

    @Override
    public void keyTyped(char par1, int par2)
    {
        super.keyTyped(par1, par2);
        this.textFieldX.textboxKeyTyped(par1, par2);
        this.textFieldZ.textboxKeyTyped(par1, par2);
        this.textFieldY.textboxKeyTyped(par1, par2);
        this.textFieldFreq.textboxKeyTyped(par1, par2);

        try
        {
            Vector3 newTarget = new Vector3(Integer.parseInt(this.textFieldX.getText()), Integer.parseInt(this.textFieldY.getText()), Integer.parseInt(this.textFieldZ.getText()));
            this.tileEntity.setTarget(newTarget);
            PacketDispatcher.sendPacketToServer(ICBMCore.PACKET_TILE.getPacket(tileEntity, 2, tileEntity.getTarget().intX(), this.tileEntity.getTarget().intY(), this.tileEntity.getTarget().intZ()));
        }
        catch (NumberFormatException e)
        {
        }

        try
        {
            short newFrequency = (short) Math.max(Short.parseShort(this.textFieldFreq.getText()), 0);
            this.tileEntity.setFrequency(newFrequency);
            PacketDispatcher.sendPacketToServer(ICBMCore.PACKET_TILE.getPacket(tileEntity, 1, tileEntity.getFrequency()));
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
        this.textFieldX.mouseClicked(par1 - containerWidth, par2 - containerHeight, par3);
        this.textFieldZ.mouseClicked(par1 - containerWidth, par2 - containerHeight, par3);
        this.textFieldY.mouseClicked(par1 - containerWidth, par2 - containerHeight, par3);
        this.textFieldFreq.mouseClicked(par1 - containerWidth, par2 - containerHeight, par3);
    }

    /** Draw the foreground layer for the GuiContainer (everything in front of the items) */
    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2)
    {
        this.fontRenderer.drawString("\u00a77" + tileEntity.getInvName(), 52, 6, 4210752);

        this.fontRenderer.drawString(LanguageUtility.getLocal("gui.misc.x"), 8, 23, 4210752);
        this.fontRenderer.drawString(LanguageUtility.getLocal("gui.misc.y"), 8, 39, 4210752);
        this.fontRenderer.drawString(LanguageUtility.getLocal("gui.misc.z"), 8, 54, 4210752);

        this.fontRenderer.drawString(LanguageUtility.getLocal("gui.misc.freq"), 70, 20, 4210752);

        this.textFieldX.drawTextBox();
        this.textFieldZ.drawTextBox();
        this.textFieldY.drawTextBox();
        this.textFieldFreq.drawTextBox();

        this.fontRenderer.drawString(this.tileEntity.getStatus(), 70, 50, 4210752);
        this.fontRenderer.drawString(this.tileEntity.getVoltageInput(null) + "v", 70, 60, 4210752);
        this.fontRenderer.drawString(UnitDisplay.getDisplayShort(this.tileEntity.getEnergyHandler().getEnergy(), Unit.JOULES) + "/" + UnitDisplay.getDisplayShort(this.tileEntity.getEnergyHandler().getEnergyCapacity(), Unit.JOULES), 70, 70, 4210752);

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

    @Override
    public void updateScreen()
    {
        super.updateScreen();

        if (!this.textFieldX.isFocused())
            this.textFieldX.setText(Math.round(this.tileEntity.getTarget().x) + "");
        if (!this.textFieldZ.isFocused())
            this.textFieldZ.setText(Math.round(this.tileEntity.getTarget().z) + "");
        if (!this.textFieldY.isFocused())
            this.textFieldY.setText(Math.round(this.tileEntity.getTarget().y) + "");
        if (!this.textFieldFreq.isFocused())
            this.textFieldFreq.setText(this.tileEntity.getFrequency() + "");
    }
}
