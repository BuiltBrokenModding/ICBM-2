package icbm.explosion.gui;

import icbm.Reference;
import icbm.core.ICBMCore;
import icbm.core.gui.GuiICBM;
import icbm.explosion.machines.TileEMPTower;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import resonant.engine.ResonantEngine;
import resonant.lib.network.discriminator.PacketTile;
import resonant.lib.utility.LanguageUtility;
import cpw.mods.fml.client.FMLClientHandler;

public class GuiEMPTower extends GuiICBM
{
    public static final ResourceLocation TEXTURE = new ResourceLocation(Reference.DOMAIN, Reference.GUI_PATH + "gui_empty.png");

    private TileEMPTower tileEntity;
    private GuiTextField textFieldBanJing;

    private int containerWidth;
    private int containerHeight;

    public GuiEMPTower(TileEMPTower tileEntity)
    {
        super(tileEntity);
        this.tileEntity = tileEntity;
        this.ySize = 166;
    }

    /** Adds the buttons (and other controls) to the screen in question. */
    @Override
    public void initGui()
    {
        super.initGui();
        this.buttonList.clear();

        this.buttonList.add(new GuiButton(0, this.width / 2 - 77, this.height / 2 - 10, 50, 20, LanguageUtility.getLocal("gui.empTower.missiles")));
        this.buttonList.add(new GuiButton(1, this.width / 2 - 25, this.height / 2 - 10, 65, 20, LanguageUtility.getLocal("gui.empTower.elec")));
        this.buttonList.add(new GuiButton(2, this.width / 2 + 43, this.height / 2 - 10, 35, 20, LanguageUtility.getLocal("gui.empTower.both")));

        this.textFieldBanJing = new GuiTextField(fontRendererObj, 72, 28, 30, 12);
        this.textFieldBanJing.setMaxStringLength(3);
        this.textFieldBanJing.setText(this.tileEntity.empRadius + "");
    }

    /** Fired when a control is clicked. This is the equivalent of
     * ActionListener.actionPerformed(ActionEvent e). */
    @Override
    protected void actionPerformed(GuiButton par1GuiButton)
    {
        switch (par1GuiButton.id)
        {
            case 0:
                this.tileEntity.empMode = 1;
                break;
            case 1:
                this.tileEntity.empMode = 2;
                break;
            case 2:
                this.tileEntity.empMode = 0;
                break;
        }

        ResonantEngine.instance.packetHandler.sendToServer(new PacketTile(this.tileEntity, 2, this.tileEntity.empMode));
    }

    /** Call this method from you GuiScreen to process the keys into textbox. */
    @Override
    public void keyTyped(char par1, int par2)
    {
        super.keyTyped(par1, par2);
        this.textFieldBanJing.textboxKeyTyped(par1, par2);

        try
        {
            int radius = Math.min(Math.max(Integer.parseInt(this.textFieldBanJing.getText()), 10), TileEMPTower.MAX_RADIUS);
            this.tileEntity.empRadius = radius;
            ResonantEngine.instance.packetHandler.sendToServer(new PacketTile(this.tileEntity, 1, this.tileEntity.empRadius));
        }
        catch (NumberFormatException e)
        {

        }
    }

    /** Args: x, y, buttonClicked */
    @Override
    public void mouseClicked(int x, int y, int par3)
    {
        super.mouseClicked(x, y, par3);
        this.textFieldBanJing.mouseClicked(x - containerWidth, y - containerHeight, par3);
    }

    /** Draw the foreground layer for the GuiContainer (everything in front of the items) */
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        this.fontRendererObj.drawString("\u00a77" + LanguageUtility.getLocal("gui.empTower.name"), 65, 6, 4210752);

        this.fontRendererObj.drawString(LanguageUtility.getLocal("gui.empTower.radius").replaceAll("%p", "        "), 12, 30, 4210752);
        this.textFieldBanJing.drawTextBox();

        this.fontRendererObj.drawString(LanguageUtility.getLocal("gui.empTower.effect"), 12, 55, 4210752);

        // Shows the EMP mode of the EMP Tower
        String mode = LanguageUtility.getLocal("gui.empTower.effectDebilitate");

        if (this.tileEntity.empMode == 1)
        {
            mode = LanguageUtility.getLocal("gui.empTower.effectDisrupt");
        }
        else if (this.tileEntity.empMode == 2)
        {
            mode = LanguageUtility.getLocal("gui.empTower.effectDeplete");
        }

        this.fontRendererObj.drawString(LanguageUtility.getLocal("gui.empTower.mode") + " " + mode, 12, 105, 4210752);

        // Shows the status of the EMP Tower
        String color = "\u00a74";
        String status = LanguageUtility.getLocal("gui.misc.idle");

        if (!this.tileEntity.energy().isFull())
        {
            status = LanguageUtility.getLocal("gui.misc.nopower");
        }
        else
        {
            color = "\u00a72";
            status = LanguageUtility.getLocal("gui.empTower.ready");
        }

        this.fontRendererObj.drawString(color + LanguageUtility.getLocal("gui.misc.status") + " " + status, 12, 120, 4210752);
        //this.fontRendererObj.drawString(LanguageUtility.getLocal("gui.misc.voltage") + " " + UnitDisplay.getDisplay(this.tileEntity.getVoltageInput(null), Unit.VOLTAGE), 12, 135, 4210752);
        //this.fontRendererObj.drawString(UnitDisplay.getDisplayShort(this.tileEntity.getEnergyHandler().getEnergy(), Unit.JOULES) + "/" + UnitDisplay.getDisplay(this.tileEntity.getEnergyHandler().getEnergyCapacity(), Unit.JOULES), 12, 150, 4210752);
    }

    /** Draw the background layer for the GuiContainer (everything behind the items) */
    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY)
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

        if (!this.textFieldBanJing.isFocused())
            this.textFieldBanJing.setText(this.tileEntity.empRadius + "");
    }
}
