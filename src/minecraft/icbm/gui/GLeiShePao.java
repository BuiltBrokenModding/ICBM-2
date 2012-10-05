package icbm.gui;

import icbm.ICBM;
import icbm.ICBMPacketManager;
import icbm.jiqi.TLeiShePao;
import icbm.rongqi.CCiGuiPao;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiContainer;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.network.PacketDispatcher;

import universalelectricity.electricity.ElectricInfo;
import universalelectricity.electricity.ElectricInfo.ElectricUnit;
import universalelectricity.network.PacketManager;

public class GLeiShePao extends ICBMGui
{
    private TLeiShePao tileEntity;
    private int containerWidth;
    private int containerHeight;
    
	private int GUITicks = 0;

    public GLeiShePao(TLeiShePao tileEntity)
    {
        this.tileEntity = tileEntity;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    @Override
	public void initGui()
    {
    	super.initGui();
        this.controlList.clear();
        
        this.controlList.add(new GuiButton(0, this.width / 2 - 60,  this.height / 2 + 10, 55, 20, "Mount"));
        this.controlList.add(new GuiButton(1, this.width / 2 + 10,  this.height / 2 + 10, 55, 20, "Mode"));
    	PacketDispatcher.sendPacketToServer(PacketManager.getPacket(ICBM.CHANNEL, this.tileEntity, (int)-1, true));
    }
    
    
    @Override
    public void onGuiClosed()
    {
    	super.onGuiClosed();
    	PacketDispatcher.sendPacketToServer(PacketManager.getPacket(ICBM.CHANNEL, this.tileEntity, (int)-1, false));
    }
    
    /**
     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
     */
    @Override
    protected void actionPerformed(GuiButton par1GuiButton)
    {
        switch(par1GuiButton.id)
        {
	        case 0: PacketDispatcher.sendPacketToServer(PacketManager.getPacket(ICBM.CHANNEL, this.tileEntity, (int)2));
	        this.tileEntity.mount(this.mc.thePlayer); this.mc.thePlayer.closeScreen(); 
	        break;
	        case 1: PacketDispatcher.sendPacketToServer(PacketManager.getPacket(ICBM.CHANNEL, this.tileEntity, (int)3));
	        this.tileEntity.autoMode = !this.tileEntity.autoMode;
	        break;
        }
    }

    /**
     * Call this method from you GuiScreen to process the keys into textbox.
     */
    @Override
	public void keyTyped(char par1, int par2)
    {
        super.keyTyped(par1, par2);
    }

    /**
     * Args: x, y, buttonClicked
     */
    @Override
	public void mouseClicked(int par1, int par2, int par3)
    {
        super.mouseClicked(par1, par2, par3);
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    @Override
	protected void drawGuiContainerForegroundLayer()
    {
    	this.fontRenderer.drawString("Laser Turret", 59, 6, 4210752);
    	    	        
        //Shows the status of the EMP Tower
        String color = "\u00a74";
        String status = "Idle";
            	
        if(this.tileEntity.isDisabled())
    	{
        	status = "Disabled";
    	}
        else if(this.tileEntity.dian >= this.tileEntity.YAO_DIAN)
    	{
        	color = "\u00a72";
    		status = "Ready to blast!";
    	}
    	else
    	{
    		status = "Insufficient electricity!";
    	}
        
        this.fontRenderer.drawString("Automatic Mode: "+this.tileEntity.autoMode, 12, 125, 4210752);
        this.fontRenderer.drawString(color+"Status: "+status, 12, 137, 4210752);
    	this.fontRenderer.drawString("Requirement: "+ElectricInfo.getDisplay(this.tileEntity.YAO_DIAN*20, ElectricUnit.WATT), 12, 150, 4210752);
    }

    /**
     * Draw the background layer for the GuiContainer (everything behind the items)
     */
    @Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
    {
        int var4 = this.mc.renderEngine.getTexture(ICBM.TEXTURE_FILE_PATH+"EmptyGUI.png");
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(var4);
        containerWidth = (this.width - this.xSize) / 2;
        containerHeight = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(containerWidth, containerHeight, 0, 0, this.xSize, this.ySize);
    }
}
