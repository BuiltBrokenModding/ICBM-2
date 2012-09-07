package icbm;

import icbm.machines.TileEntityRailgun;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiContainer;

import org.lwjgl.opengl.GL11;

import universalelectricity.electricity.ElectricUnit;
import universalelectricity.network.PacketManager;

public class GuiRailgun extends GuiContainer
{
    private TileEntityRailgun tileEntity;
	private EntityPlayer player;

    private int containerWidth;
    private int containerHeight;
    
	private int GUITicks = 0;

    public GuiRailgun(TileEntityRailgun tileEntity, EntityPlayer player)
    {
        super(new ContainerRailgun(player.inventory, tileEntity));
        this.tileEntity = tileEntity;
        this.player = player;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    @Override
	public void initGui()
    {
    	super.initGui();
        this.controlList.clear();
        
        this.controlList.add(new GuiButton(0, this.width / 2 + 90,  this.height / 2 - 80, 55, 20, "Mount"));
        
    	PacketManager.sendTileEntityPacketToServer(this.tileEntity, "ICBM", (int)-1, true);
    }
    
    
    @Override
    public void onGuiClosed()
    {
    	super.onGuiClosed();
    	PacketManager.sendTileEntityPacketToServer(this.tileEntity, "ICBM", (int)-1, false);
    }
    
    public void updateScreen()
    {
    	super.updateScreen();
    	
		if(GUITicks % 100 == 0)
    	{
    		PacketManager.sendTileEntityPacketToServer(this.tileEntity, "ICBM", (int)-1, true);
    	}
		
    	GUITicks ++;
    }
    
    
    /**
     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
     */
    @Override
    protected void actionPerformed(GuiButton par1GuiButton)
    {
        switch(par1GuiButton.id)
        {
	        case 0: ICBMPacketManager.sendTileEntityPacketToServer(this.tileEntity, "ICBM", (int)2); this.tileEntity.mount(this.mc.thePlayer); this.mc.thePlayer.closeScreen(); break;
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
    	this.fontRenderer.drawString(this.tileEntity.getInvName(), 65, 6, 4210752);
    	
    	this.fontRenderer.drawString("Ammo Input", 65, 23, 4210752);
    	        
        //Shows the status of the EMP Tower
        String color = "\u00a74";
        String status = "Idle";
            	
        if(this.tileEntity.isDisabled())
    	{
        	status = "Disabled";
    	}
        else if(this.tileEntity.electricityStored < this.tileEntity.WATTAGE_REQUIRED)
    	{
    		status = "Insufficient electricity!";
    	}
    	else
    	{
    		color = "\u00a72";
    		status = "Ready to blast!";
    	}
        
        this.fontRenderer.drawString(color+"Status: "+status, 8, 60, 4210752);
        
    	this.fontRenderer.drawString(this.tileEntity.getVoltage()+"v", 130, 70, 4210752);
        this.fontRenderer.drawString(ElectricUnit.getAmpHourDisplay(this.tileEntity.electricityStored, this.tileEntity.getVoltage())+ "/" +ElectricUnit.getAmpHourDisplay(this.tileEntity.WATTAGE_REQUIRED, this.tileEntity.getVoltage()), 8, 70, 4210752);
    }

    /**
     * Draw the background layer for the GuiContainer (everything behind the items)
     */
    @Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
    {
        int var4 = this.mc.renderEngine.getTexture(ICBM.TEXTURE_FILE_PATH+"RailgunGUI.png");
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(var4);
        containerWidth = (this.width - this.xSize) / 2;
        containerHeight = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(containerWidth, containerHeight, 0, 0, this.xSize, this.ySize);
    }
}
