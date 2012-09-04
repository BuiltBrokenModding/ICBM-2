package icbm;

import icbm.ICBMGui;
import icbm.ItemSignalDisrupter;
import net.minecraft.src.GuiTextField;
import net.minecraft.src.ItemStack;

import org.lwjgl.opengl.GL11;

import universalelectricity.network.PacketManager;

public class GuiFrequency extends ICBMGui
{    
    private ItemStack itemStack;
    
    private GuiTextField textFieldFrequency;

    private int containerWidth;
    private int containerHeight;

    public GuiFrequency(ItemStack par1ItemStack)
    {
        this.itemStack = par1ItemStack;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    @Override
	public void initGui()
    {
    	super.initGui();
        this.textFieldFrequency = new GuiTextField(fontRenderer, 80, 50, 40, 12);
        this.textFieldFrequency.setMaxStringLength(4);
        this.textFieldFrequency.setText(((ItemSignalDisrupter)this.itemStack.getItem()).getFrequency(this.itemStack)+"");
    }
    
    /**
     * Call this method from you GuiScreen to process the keys into textbox.
     */
    @Override
	public void keyTyped(char par1, int par2)
    {
        super.keyTyped(par1, par2);
        this.textFieldFrequency.textboxKeyTyped(par1, par2);
    }

    /**
     * Args: x, y, buttonClicked
     */
    @Override
	public void mouseClicked(int par1, int par2, int par3)
    {
        super.mouseClicked(par1, par2, par3);
        this.textFieldFrequency.mouseClicked(par1 - containerWidth, par2 - containerHeight, par3);
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    @Override
	protected void drawGuiContainerForegroundLayer()
    {
    	this.fontRenderer.drawString("Frequency", 65, 6, 4210752);
    	this.fontRenderer.drawString("Frequency:", 15, 52, 4210752);
    	this.textFieldFrequency.drawTextBox();
    	
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

    @Override
	public void updateScreen()
    {
        super.updateScreen();
        
        try
        {
        	short newFrequency = (short) Math.max(0, Short.parseShort(this.textFieldFrequency.getText()));
        	this.textFieldFrequency.setText(newFrequency+"");

        	if(((ItemSignalDisrupter)this.itemStack.getItem()).getFrequency(this.itemStack) != newFrequency)
        	{
            	((ItemSignalDisrupter)this.itemStack.getItem()).setFrequency(this.itemStack, newFrequency);
            	
    			PacketManager.sendUnspecifiedPacketToServer("ICBM", ICBMPacketManager.SIGNAL_DISRUPTER_PACKET, newFrequency);
        	}
        }
        catch (NumberFormatException e)
        {
        }
    }
}
