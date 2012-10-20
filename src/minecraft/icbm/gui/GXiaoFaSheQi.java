package icbm.gui;

import icbm.ZhuYao;
import icbm.api.ICBM;
import icbm.jiqi.TXiaoFaSheQi;
import icbm.rongqi.CXiaoFaSheQi;
import net.minecraft.src.GuiContainer;
import net.minecraft.src.GuiTextField;
import net.minecraft.src.InventoryPlayer;
import net.minecraft.src.StatCollector;

import org.lwjgl.opengl.GL11;

import universalelectricity.electricity.ElectricInfo;
import universalelectricity.electricity.ElectricInfo.ElectricUnit;
import universalelectricity.network.PacketManager;
import universalelectricity.prefab.Vector3;
import cpw.mods.fml.common.network.PacketDispatcher;

public class GXiaoFaSheQi extends GuiContainer
{
    private TXiaoFaSheQi tileEntity;
    private GuiTextField textFieldX;
    private GuiTextField textFieldZ;
    private GuiTextField textFieldY;
    private GuiTextField textFieldFreq;
    
    private int containerWidth;
    private int containerHeight;

    public GXiaoFaSheQi(InventoryPlayer par1InventoryPlayer, TXiaoFaSheQi tileEntity)
    {
        super(new CXiaoFaSheQi(par1InventoryPlayer, tileEntity));
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
        this.textFieldY.setMaxStringLength(6);
                
        this.textFieldFreq.setText(this.tileEntity.getFrequency()+"");
        
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
            PacketDispatcher.sendPacketToServer(PacketManager.getPacket(ZhuYao.CHANNEL, this.tileEntity, (int)2, this.tileEntity.getTarget().x, this.tileEntity.getTarget().y, this.tileEntity.getTarget().z));
        }
        catch (NumberFormatException e)
        {
        }
        
        try
        {
        	short newFrequency = (short)Math.max(Short.parseShort(this.textFieldFreq.getText()), 0);
        	this.tileEntity.setFrequency(newFrequency);
            PacketDispatcher.sendPacketToServer(PacketManager.getPacket(ZhuYao.CHANNEL, this.tileEntity, (int)1, this.tileEntity.getFrequency()));        	
        }
        catch (NumberFormatException e)
        {
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
        this.fontRenderer.drawString(ElectricInfo.getDisplayShort(this.tileEntity.getJoules(), ElectricUnit.JOULES), 108, 60, 4210752);
        this.fontRenderer.drawString("Max: " +ElectricInfo.getDisplayShort(this.tileEntity.getMaxJoules(), ElectricUnit.JOULES), 105, 70, 4210752);
        
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
        
        if(!this.textFieldX.isFocused())
        	this.textFieldX.setText(Math.round(this.tileEntity.getTarget().x) + "");
        if(!this.textFieldZ.isFocused())
        	this.textFieldZ.setText(Math.round(this.tileEntity.getTarget().z) + "");
        if(!this.textFieldY.isFocused())
        	this.textFieldY.setText(Math.round(this.tileEntity.getTarget().y) + "");
        if(!this.textFieldFreq.isFocused())
        	this.textFieldFreq.setText(this.tileEntity.getFrequency() + "");
    }
}
