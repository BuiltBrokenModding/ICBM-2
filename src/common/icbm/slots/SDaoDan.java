package icbm.slots;

import icbm.ICBM;
import icbm.daodan.ItDaoDan;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Slot;

public class SDaoDan extends Slot
{
    public SDaoDan(IInventory par2IInventory, int par3, int par4, int par5)
    {
        super(par2IInventory, par3, par4, par5);
    }

    /**
     * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
     */
    @Override
    public boolean isItemValid(ItemStack par1ItemStack)
    {
    	return itemValid(par1ItemStack);
    }
    
    public static boolean itemValid(ItemStack par1ItemStack)
    {
    	if(par1ItemStack.getItem() instanceof ItDaoDan)
    	{
    		if(par1ItemStack.itemID == ICBM.itemDaoDan.shiftedIndex)
    		{
    			return true;
    		}
    		else if(par1ItemStack.getItemDamage() > 0)
    		{
        		return true;
    		}
    	}
        return false;
    }
}
