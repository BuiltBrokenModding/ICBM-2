package icbm.electronics;

import icbm.ICBM;
import icbm.ICBMCommonProxy;
import icbm.extend.IItemFrequency;

import java.util.List;

import net.minecraft.src.Entity;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ModLoader;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.World;
import universalelectricity.extend.ItemElectric;

public class ItemSignalDisrupter extends ItemElectric implements IItemFrequency
{	
    public ItemSignalDisrupter(String name, int par1, int par2)
    {
    	super(par1);
        this.iconIndex = par2;
        this.setItemName(name);
    }
    
    @Override
	public String getTextureFile()
    {
        return ICBM.ITEM_TEXTURE_FILE;
    }
    
    /**
     * Allows items to add custom lines of information to the mouseover description
     */
    @Override
	public void addInformation(ItemStack par1ItemStack, List par2List)
    {
    	super.addInformation(par1ItemStack, par2List);
    	par2List.add("Frequency: "+this.getFrequency(par1ItemStack));
    }
    
    @Override
    public short getFrequency(ItemStack par1ItemStack)
    {
    	if (par1ItemStack.stackTagCompound == null)
		{
    		return 0;
		}
    	return par1ItemStack.stackTagCompound.getShort("frequency");		
    }
	
	@Override
	public void setFrequency(ItemStack par1ItemStack, short frequency)
	{
		if (par1ItemStack.stackTagCompound == null)
		{
			 par1ItemStack.setTagCompound(new NBTTagCompound());
		}

		par1ItemStack.stackTagCompound.setShort("frequency", frequency);
	}
    
	@Override
    public void onUpdate(ItemStack par1ItemStack, World par2World, Entity par3Entity, int par4, boolean par5)
    {
    	if(this.getElectricityStored(par1ItemStack) > 1)
    	{
        	this.onUseElectricity(0.01F, par1ItemStack);
    	}
    }
    
	@Override
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
		par3EntityPlayer.openGui(ICBM.instance, ICBMCommonProxy.GUI_FREQUENCY, par2World, (int)par3EntityPlayer.posX, (int)par3EntityPlayer.posY, (int)par3EntityPlayer.posZ);
    	return par1ItemStack;
    }

	@Override
	public float getVoltage()
	{
		return 20;
	}
	
	@Override
	public float getElectricityCapacity() 
	{
		return 4000;
	}

	@Override
	public float getTransferRate()
	{
		return 25;
	}
}
