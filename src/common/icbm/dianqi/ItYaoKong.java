package icbm.dianqi;

import icbm.ICBM;
import icbm.zhapin.TZhaDan;
import icbm.zhapin.ZhaPin;

import java.util.List;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EnumMovingObjectType;
import net.minecraft.src.ItemStack;
import net.minecraft.src.MovingObjectPosition;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import universalelectricity.extend.ItemElectric;
import universalelectricity.network.PacketManager;

public class ItYaoKong extends ItemElectric
{
	public static final int RADIUS = 100;
	public static final int ELECTRICITY_REQUIRED = 150;
	
    public ItYaoKong(String name, int par1, int par2)
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
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    @Override
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {	    	
    	if(par2World.isRemote)
    	{
	    	MovingObjectPosition objectMouseOver = par3EntityPlayer.rayTrace(RADIUS, 1);
	    
	    	if (objectMouseOver != null && objectMouseOver.typeOfHit == EnumMovingObjectType.TILE)
	    	{
		    	TileEntity tileEntity = par2World.getBlockTileEntity(objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ);
		    	int blockID = par2World.getBlockId(objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ);
		    	int blockMetadata = par2World.getBlockMetadata(objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ);
		    	
		    	if(blockID == ICBM.blockJi1Qi4.blockID)
		        {
		        	return par1ItemStack;
		        }
		        else if(tileEntity instanceof TZhaDan && (blockMetadata == ZhaPin.Condensed.getID() || blockMetadata == ZhaPin.Breaching.getID()))
		        {
		        	//Check for electricity
		            if(this.getWattHoursStored(par1ItemStack) > ELECTRICITY_REQUIRED)
		        	{
       	    		 	PacketManager.sendTileEntityPacketToServer(tileEntity, "ICBM", (byte)2);
			            return par1ItemStack;
		        	}
		            else
			    	{
			    		par3EntityPlayer.addChatMessage("Remote out of electricity!");
			    	}
		        }
	        }
    	}
        
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
		return 2000;
	}

	@Override
	public float getTransferRate()
	{
		return 25;
	}

}
