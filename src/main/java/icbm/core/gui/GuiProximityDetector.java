package icbm.core.gui;

import icbm.core.blocks.TileProximityDetector;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import resonant.engine.ResonantEngine;
import resonant.lib.network.discriminator.PacketTile;
import resonant.lib.transform.vector.Vector3;
import resonant.lib.utility.LanguageUtility;

public class GuiProximityDetector extends GuiICBM
{
    private TileProximityDetector tileEntity;

    private GuiTextField textFieldFreq;

    private GuiTextField textFieldminX;
    private GuiTextField textFieldminY;
    private GuiTextField textFieldminZ;

    private GuiTextField textFieldmaxX;
    private GuiTextField textFieldmaxY;
    private GuiTextField textFieldmaxZ;

    public GuiProximityDetector(TileProximityDetector tileEntity)
    {
        super(tileEntity);
        this.tileEntity = tileEntity;
        ySize = 166;
    }

    /** Adds the buttons (and other controls) to the screen in question. */
    @Override
    public void initGui()
    {
        super.initGui();

        this.buttonList.clear();

        String mode = LanguageUtility.getLocal("gui.detector.all");

        if (this.tileEntity.mode == 1)
        {
            mode = LanguageUtility.getLocal("gui.detector.players");
        }
        else if (this.tileEntity.mode == 2)
        {
            mode = LanguageUtility.getLocal("gui.detector.mobs");
        }

        this.buttonList.add(new GuiButton(0, this.width / 2 - 15, this.height / 2 + 32, 45, 20, mode));

        this.textFieldFreq = new GuiTextField(fontRendererObj, 75, 100, 40, 12);
        this.textFieldFreq.setMaxStringLength(4);
        this.textFieldFreq.setText(this.tileEntity.getFrequency() + "");

        // Min
        this.textFieldminX = new GuiTextField(fontRendererObj, 75, 50, 20, 12);
        this.textFieldminX.setMaxStringLength(2);
        this.textFieldminX.setText(this.tileEntity.minCoord.xi() + "");

        this.textFieldminY = new GuiTextField(fontRendererObj, 75, 67, 20, 12);
        this.textFieldminY.setMaxStringLength(2);
        this.textFieldminY.setText(this.tileEntity.minCoord.yi() + "");

        this.textFieldminZ = new GuiTextField(fontRendererObj, 75, 82, 20, 12);
        this.textFieldminZ.setMaxStringLength(2);
        this.textFieldminZ.setText(this.tileEntity.minCoord.zi() + "");

        // Max
        this.textFieldmaxX = new GuiTextField(fontRendererObj, 130, 50, 20, 12);
        this.textFieldmaxX.setMaxStringLength(2);
        this.textFieldmaxX.setText(this.tileEntity.maxCoord.xi() + "");

        this.textFieldmaxY = new GuiTextField(fontRendererObj, 130, 67, 20, 12);
        this.textFieldmaxY.setMaxStringLength(2);
        this.textFieldmaxY.setText(this.tileEntity.maxCoord.yi() + "");

        this.textFieldmaxZ = new GuiTextField(fontRendererObj, 130, 82, 20, 12);
        this.textFieldmaxZ.setMaxStringLength(2);
        this.textFieldmaxZ.setText(this.tileEntity.maxCoord.zi() + "");

        ResonantEngine.instance.packetHandler.sendToServer(new PacketTile(this.tileEntity, -1, true));
    }

    /** Fired when a control is clicked. This is the equivalent of
     * ActionListener.actionPerformed(ActionEvent e). */
    @Override
    protected void actionPerformed(GuiButton par1GuiButton)
    {
        if (par1GuiButton.id == 0)
        {
            tileEntity.mode = (byte) ((tileEntity.mode + 1) % 3);
            ResonantEngine.instance.packetHandler.sendToServer(new PacketTile(this.tileEntity, 1, this.tileEntity.mode));
        }
    }

    /** Call this method from you GuiScreen to process the keys into textbox. */
    @Override
    public void keyTyped(char par1, int par2)
    {
        super.keyTyped(par1, par2);
        this.textFieldminX.textboxKeyTyped(par1, par2);
        this.textFieldminY.textboxKeyTyped(par1, par2);
        this.textFieldminZ.textboxKeyTyped(par1, par2);
        this.textFieldmaxX.textboxKeyTyped(par1, par2);
        this.textFieldmaxY.textboxKeyTyped(par1, par2);
        this.textFieldmaxZ.textboxKeyTyped(par1, par2);
        this.textFieldFreq.textboxKeyTyped(par1, par2);

        try
        {
            int newFrequency = Math.max(0, Integer.parseInt(this.textFieldFreq.getText()));
            this.tileEntity.setFrequency(newFrequency);
            ResonantEngine.instance.packetHandler.sendToServer(new PacketTile(this.tileEntity, 2, this.tileEntity.getFrequency()));
        }
        catch (Exception e)
        {

        }

        try
        {
            Vector3 newMinCoord = new Vector3(Integer.parseInt(this.textFieldminX.getText()), Integer.parseInt(this.textFieldminY.getText()), Integer.parseInt(this.textFieldminZ.getText()));
            this.tileEntity.minCoord = newMinCoord;
            ResonantEngine.instance.packetHandler.sendToServer(new PacketTile(this.tileEntity, 3, this.tileEntity.minCoord.xi(), this.tileEntity.minCoord.yi(), this.tileEntity.minCoord.zi()));
        }
        catch (Exception e)
        {
        }

        try
        {
            Vector3 newMaxCoord = new Vector3(Integer.parseInt(this.textFieldmaxX.getText()), Integer.parseInt(this.textFieldmaxY.getText()), Integer.parseInt(this.textFieldmaxZ.getText()));

            this.tileEntity.maxCoord = newMaxCoord;
            ResonantEngine.instance.packetHandler.sendToServer(new PacketTile(this.tileEntity, 4, this.tileEntity.maxCoord.xi(), this.tileEntity.maxCoord.yi(), this.tileEntity.maxCoord.zi()));
        }
        catch (Exception e)
        {

        }
    }

    /** Args: x, y, buttonClicked */
    @Override
    public void mouseClicked(int par1, int par2, int par3)
    {
        super.mouseClicked(par1, par2, par3);
        this.textFieldminX.mouseClicked(par1 - containerWidth, par2 - containerHeight, par3);
        this.textFieldminY.mouseClicked(par1 - containerWidth, par2 - containerHeight, par3);
        this.textFieldminZ.mouseClicked(par1 - containerWidth, par2 - containerHeight, par3);
        this.textFieldmaxX.mouseClicked(par1 - containerWidth, par2 - containerHeight, par3);
        this.textFieldmaxY.mouseClicked(par1 - containerWidth, par2 - containerHeight, par3);
        this.textFieldmaxZ.mouseClicked(par1 - containerWidth, par2 - containerHeight, par3);
        this.textFieldFreq.mouseClicked(par1 - containerWidth, par2 - containerHeight, par3);
    }

    /** Draw the foreground layer for the GuiContainer (everything in front of the items) */
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        this.fontRendererObj.drawString("\u00a77" + LanguageUtility.getLocal("gui.detector.name"), 48, 6, 4210752);

        this.fontRendererObj.drawString(LanguageUtility.getLocal("gui.detector.range"), 12, 25, 4210752);

        this.fontRendererObj.drawString(LanguageUtility.getLocal("gui.detector.min"), 75, 40, 4210752);
        this.fontRendererObj.drawString(LanguageUtility.getLocal("gui.detector.max"), 130, 40, 4210752);

        this.fontRendererObj.drawString(LanguageUtility.getLocal("gui.misc.XCoord"), 15, 51, 4210752);
        this.fontRendererObj.drawString(LanguageUtility.getLocal("gui.misc.YCoord"), 15, 68, 4210752);
        this.fontRendererObj.drawString(LanguageUtility.getLocal("gui.misc.ZCoord"), 15, 83, 4210752);

        this.textFieldminX.drawTextBox();
        this.textFieldminY.drawTextBox();
        this.textFieldminZ.drawTextBox();

        this.textFieldmaxX.drawTextBox();
        this.textFieldmaxY.drawTextBox();
        this.textFieldmaxZ.drawTextBox();

        this.fontRendererObj.drawString(LanguageUtility.getLocal("gui.misc.freq"), 15, 102, 4210752);

        if (!this.tileEntity.isInverted)
        {
            this.fontRendererObj.drawString(LanguageUtility.getLocal("gui.detector.exclude"), 120, 102, 4210752);
        }
        else
        {
            this.fontRendererObj.drawString(LanguageUtility.getLocal("gui.detector.include"), 120, 102, 4210752);
        }
        this.fontRendererObj.drawString(LanguageUtility.getLocal("gui.detector.target"), 15, 120, 4210752);

        this.textFieldFreq.drawTextBox();

        String color = "\u00a74";
        String status = LanguageUtility.getLocal("gui.misc.idle");

        if (!this.tileEntity.getEnergyStorage().checkExtract())
        {
            status = LanguageUtility.getLocal("gui.misc.nopower");
        }
        else
        {
            color = "\u00a72";
            status = LanguageUtility.getLocal("gui.detector.on");
        }

        this.fontRendererObj.drawString(color + LanguageUtility.getLocal("gui.detector.status") + " " + status, 12, 138, 4210752);
        //this.fontRendererObj.drawString(UnitDisplay.getDisplay(this.tileEntity.energy().getEnergu(), Unit.JOULES) + " " + UnitDisplay.getDisplay(this.tileEntity.getVoltageInput(null), Unit.VOLTAGE), 12, 150, 4210752);
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();

        String mode = LanguageUtility.getLocal("gui.detector.all");

        if (this.tileEntity.mode == 1)
        {
            mode = LanguageUtility.getLocal("gui.detector.players");
        }
        else if (this.tileEntity.mode == 2)
        {
            mode = LanguageUtility.getLocal("gui.detector.mobs");
        }

        ((GuiButton) this.buttonList.get(0)).displayString = mode;

        if (!this.textFieldminX.isFocused())
            this.textFieldminX.setText(this.tileEntity.minCoord.xi() + "");
        if (!this.textFieldminY.isFocused())
            this.textFieldminY.setText(this.tileEntity.minCoord.yi() + "");
        if (!this.textFieldminZ.isFocused())
            this.textFieldminZ.setText(this.tileEntity.minCoord.zi() + "");

        if (!this.textFieldmaxX.isFocused())
            this.textFieldmaxX.setText(this.tileEntity.maxCoord.xi() + "");
        if (!this.textFieldmaxY.isFocused())
            this.textFieldmaxY.setText(this.tileEntity.maxCoord.yi() + "");
        if (!this.textFieldmaxZ.isFocused())
            this.textFieldmaxZ.setText(this.tileEntity.maxCoord.zi() + "");

        if (!this.textFieldFreq.isFocused())
            this.textFieldFreq.setText(this.tileEntity.getFrequency() + "");
    }
}
