package icbm.gui;

import icbm.ICBM;
import icbm.jiqi.TFaSheShiMuo;
import net.minecraft.src.GuiTextField;

import org.lwjgl.opengl.GL11;

import universalelectricity.electricity.ElectricInfo;
import universalelectricity.electricity.ElectricInfo.ElectricUnit;
import universalelectricity.network.PacketManager;
import universalelectricity.prefab.Vector3;

public class GFaSheShiMuo extends ICBMGui
{
    private TFaSheShiMuo tileEntity;
    private GuiTextField textFieldX;
    private GuiTextField textFieldZ;
    private GuiTextField textFieldY;
    private GuiTextField textFieldFreq;

    private int containerWidth;
    private int containerHeight;
    
    public GFaSheShiMuo(TFaSheShiMuo par2ICBMTileEntityMissileLauncher)
    {
        this.tileEntity = par2ICBMTileEntityMissileLauncher;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    @Override
	public void initGui()
    {
    	super.initGui();
        this.textFieldX = new GuiTextField(fontRenderer, 110, 37, 45, 12);
        this.textFieldZ = new GuiTextField(fontRenderer, 110, 52, 45, 12);
        this.textFieldY = new GuiTextField(fontRenderer, 110, 82, 45, 12);
        this.textFieldFreq = new GuiTextField(fontRenderer, 110, 97, 45, 12);
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
        
        PacketManager.sendTileEntityPacketToServer(this.tileEntity, "ICBM", (int)-1, true);
    }
    
    @Override
    public void onGuiClosed()
    {
    	super.onGuiClosed();
    	PacketManager.sendTileEntityPacketToServer(this.tileEntity, "ICBM", (int)-1, false);
    }

    /**
     * Call this method from you GuiScreen to process the keys into textbox.
     */
    @Override
	public void keyTyped(char par1, int par2)
    {
        super.keyTyped(par1, par2);
        this.textFieldX.textboxKeyTyped(par1, par2);
        this.textFieldZ.textboxKeyTyped(par1, par2);

        if(tileEntity.getTier() >= 1)
        {
            this.textFieldY.textboxKeyTyped(par1, par2);
            
            if(tileEntity.getTier() > 1)
            {
            	this.textFieldFreq.textboxKeyTyped(par1, par2);
            }
        }
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

        if(tileEntity.getTier() >= 1)
        {
        	 this.textFieldY.mouseClicked(par1 - containerWidth, par2 - containerHeight, par3);
        	 
        	 if(tileEntity.getTier() > 1)
             {
        		 this.textFieldFreq.mouseClicked(par1 - containerWidth, par2 - containerHeight, par3);
             }
        }
       
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    @Override
	public void drawGuiContainerForegroundLayer()
    {    	
        this.textFieldX.drawTextBox();
        this.textFieldZ.drawTextBox();

        //Draw the air detonation GUI
        if(tileEntity.getTier() >= 1)
        {
        	this.textFieldY.drawTextBox();
        	this.fontRenderer.drawString("Air Detonation", 12, 70, 4210752);
        	this.fontRenderer.drawString("Height:", 12, 85, 4210752);
        	
        	if(tileEntity.getTier() > 1)
        	{
        		this.textFieldFreq.drawTextBox();
        		this.fontRenderer.drawString("Frequency:", 12, 100, 4210752);
        	}
        }
        
        this.fontRenderer.drawString("", 45, 6, 4210752);
        this.fontRenderer.drawString("Launcher Control Panel", 30, 6, 4210752);
        
        this.fontRenderer.drawString("Missile Target", 12, 25, 4210752);
        this.fontRenderer.drawString("X-Coord:", 25, 40, 4210752);
        this.fontRenderer.drawString("Z-Coord:", 25, 55, 4210752);
        
        int inaccuracy = 30;
        
        if(this.tileEntity.connectedBase != null)
        {
        	if(this.tileEntity.connectedBase.connectedFrame != null)
        	{
        		inaccuracy = this.tileEntity.connectedBase.connectedFrame.getInaccuracy();
        	}
        }
        
        this.fontRenderer.drawString("Inaccuracy: "+inaccuracy+" blocks", 12, 113, 4210752);
        
        //Shows the status of the missile launcher
        this.fontRenderer.drawString("Status: "+this.tileEntity.getStatus(), 12, 125, 4210752);
    	this.fontRenderer.drawString("Voltage: "+this.tileEntity.getVoltage()+"v", 12, 137, 4210752);
        this.fontRenderer.drawString(ElectricInfo.getDisplayShort(this.tileEntity.dianXiaoShi, ElectricUnit.WATT_HOUR)+ "/" +ElectricInfo.getDisplayShort(this.tileEntity.getMaxWattHours(), ElectricUnit.WATT_HOUR), 12, 150, 4210752);
    }
    
    @Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3)
    {
    	int var4 = this.mc.renderEngine.getTexture(ICBM.TEXTURE_FILE_PATH+"EmptyGUI.png");
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
        int heightBeforeHit;

        try
        {
            heightBeforeHit = Math.max(Integer.parseInt(this.textFieldY.getText()), 0);
        }
        catch (NumberFormatException e)
        {
            heightBeforeHit = 0;
        }

        try
        {
        	Vector3 newTarget = new Vector3(Integer.parseInt(this.textFieldX.getText()), heightBeforeHit, Integer.parseInt(this.textFieldZ.getText()));
        	
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
            this.tileEntity.target = null;
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
