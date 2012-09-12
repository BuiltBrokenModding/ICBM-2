package icbm;

import icbm.machines.TileEntityCruiseLauncher;
import net.minecraft.src.GuiContainer;
import net.minecraft.src.GuiTextField;
import net.minecraft.src.InventoryPlayer;
import net.minecraft.src.StatCollector;

import org.lwjgl.opengl.GL11;

import universalelectricity.Vector3;
import universalelectricity.electricity.ElectricInfo;
import universalelectricity.electricity.ElectricInfo.ElectricUnit;
import universalelectricity.network.PacketManager;

public class GuiCruiseLauncher extends GuiContainer
{
    private TileEntityCruiseLauncher tileEntity;
    private GuiTextField textFieldX;
    private GuiTextField textFieldZ;
    private GuiTextField textFieldY;
    private GuiTextField textFieldFreq;
    
    private int containerWidth;
    private int containerHeight;

    public GuiCruiseLauncher(InventoryPlayer par1InventoryPlayer, TileEntityCruiseLauncher tileEntity)
    {
        super(new ContainerCruiseLauncher(par1InventoryPlayer, tileEntity));
        this.tileEntity = tileEntity;
    }

    @Override
	public void initGui()
    {
    	super.initGui();
        this.textFieldX = new GuiTextField(fontRenderer, 20, 18, 35, 12);
        this.textFieldY = new GuiTextField(fontRenderer, 75, 18, 35, 12);
        this.textFieldZ = new GuiTextField(fontRenderer, 130, 18, 35, 12);
        this.textFieldFreq = new GuiTextField(fontRenderer, 20, 50, 35, 12);
        this.textFieldFreq.setMaxStringLength(4);
        this.textFieldX.setMaxStringLength(6);
        this.textFieldZ.setMaxStringLength(6);
        this.textFieldY.setMaxStringLength(2);
                
        this.textFieldFreq.setText(this.tileEntity.frequency+"");
        
        if (this.tileEntity.target == null)
        {
        	this.textFieldX.setText(Math.round(this.tileEntity.xCoord) + "");
            this.textFieldZ.setText(Math.round(this.tileEntity.zCoord) + "");
            this.textFieldY.setText("0");
        }
        else
        {
            this.textFieldX.setText(Math.round(this.tileEntity.target.x) + "");
            this.textFieldZ.setText(Math.round(this.tileEntity.target.z) + "");
            this.textFieldY.setText(Math.round(this.tileEntity.target.y) + "");
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
    }

    /**
     * Args: x, y, buttonClicked
     */
    @Override
	public void mouseClicked(int par1, int par2, int par3)
    {
        super.mouseClicked(par1, par2, par3);
        this.textFieldX.mouseClicked(par1 - containerWidth, par2 - containerHeight, par3);
        this.textFieldZ.mouseClicked(par1 - containerWidth, par2 - containerHeight, par3);
        this.textFieldY.mouseClicked(par1 - containerWidth, par2 - containerHeight, par3);
        this.textFieldFreq.mouseClicked(par1 - containerWidth, par2 - containerHeight, par3); 
    }
    
    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    @Override
	protected void drawGuiContainerForegroundLayer()
    {
        this.fontRenderer.drawString(tileEntity.getInvName(), 48, 6, 4210752);
        
        this.fontRenderer.drawString("X:", 8, 20, 4210752);
        this.fontRenderer.drawString("Y:", 65, 20, 4210752);
        this.fontRenderer.drawString("Z:", 120, 20, 4210752);
        this.fontRenderer.drawString("Frequency:", 10, 40, 4210752);
        
        this.textFieldX.drawTextBox();
        this.textFieldZ.drawTextBox();
        this.textFieldY.drawTextBox();
        this.textFieldFreq.drawTextBox();
        
        this.fontRenderer.drawString(this.tileEntity.getStatus(), 108, 40, 4210752);
        this.fontRenderer.drawString(this.tileEntity.getVoltage()+"v", 108, 50, 4210752);
        this.fontRenderer.drawString(ElectricInfo.getDisplayShort(this.tileEntity.electricityStored, ElectricUnit.WATT_HOUR)+ "/" +this.tileEntity.AMPS_REQUIRED/1000, 108, 60, 4210752);
        
        this.fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
    }

    /**
     * Draw the background layer for the GuiContainer (everything behind the items)
     */
    @Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
    {
        int var4 = this.mc.renderEngine.getTexture(ICBM.TEXTURE_FILE_PATH+"MissileLauncher.png");
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(var4);
        containerWidth = (this.width - this.xSize) / 2;
        containerHeight = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(containerWidth, containerHeight, 0, 0, this.xSize, this.ySize);
    }
    
    @Override
	public void updateScreen()
    {
        super.updateScreen();
        try
        {
        	Vector3 newTarget = new Vector3(Integer.parseInt(this.textFieldX.getText()), Math.max(Integer.parseInt(this.textFieldY.getText()), 0), Integer.parseInt(this.textFieldZ.getText()));
        	
        	if(this.tileEntity.target != newTarget)
        	{
            	this.tileEntity.target = newTarget;
    			PacketManager.sendTileEntityPacketToServer(this.tileEntity, "ICBM", (int)2, this.tileEntity.target.x, this.tileEntity.target.y, this.tileEntity.target.z);
        	}
        	
        	this.textFieldX.setText(Math.round(this.tileEntity.target.x) + "");
            this.textFieldZ.setText(Math.round(this.tileEntity.target.z) + "");
            this.textFieldY.setText(Math.round(this.tileEntity.target.y) + "");
        }
        catch (NumberFormatException e)
        {
            this.tileEntity.target = new Vector3();
        }
        
        try
        {
        	short newFrequency = (short)Math.max(Short.parseShort(this.textFieldFreq.getText()), 0);
        	
        	if(newFrequency != this.tileEntity.frequency)
        	{
            	this.tileEntity.frequency = newFrequency;
    			PacketManager.sendTileEntityPacketToServer(this.tileEntity, "ICBM", (int)1, this.tileEntity.frequency);
        	}
        	
        	this.textFieldFreq.setText(Math.round(this.tileEntity.frequency) + "");
        }
        catch (NumberFormatException e)
        {
            this.tileEntity.frequency = 0;
        }
    }
}
