//=======================================================
//DISCLAIMER: THIS IS A GENERATED CLASS FILE
//THUS IS PROVIDED 'AS-IS' WITH NO WARRANTY
//FUNCTIONALITY CAN NOT BE GUARANTIED IN ANY WAY 
//USE AT YOUR OWN RISK 
//-------------------------------------------------------
//Built on: Rober
//=======================================================
package com.builtbroken.icbm.content.items.gen;

import com.builtbroken.icbm.content.items.ItemLinkTool;
import com.builtbroken.mc.api.IWorldPosition;
import com.builtbroken.mc.api.items.tools.IWorldPosItem;
import com.builtbroken.mc.framework.item.ItemBase;
import com.builtbroken.mc.framework.item.logic.ItemNode;
import com.builtbroken.mc.imp.transform.vector.Location;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemWrapperLinkTool extends ItemBase implements IWorldPosItem
{
	public ItemWrapperLinkTool()
	{
		super(new ItemLinkTool());
	}

	//============================
	//==Methods:IWorldPos
	//============================


    @Override
    public Location getLocation(ItemStack stack)
    {
        if (stack.getTagCompound() != null && stack.getTagCompound().hasKey("linkPos"))
        {
            return new Location(stack.getTagCompound().getCompoundTag("linkPos"));
        }
        return null;
    }

    @Override
    public void setLocation(ItemStack stack, IWorldPosition loc)
    {
        if (stack.getTagCompound() == null)
        {
            stack.setTagCompound(new NBTTagCompound());
        }
        stack.getTagCompound().setTag("linkPos", loc.toLocation().toNBT());
    }

    @Override
    public boolean canAccessLocation(ItemStack stack, Object obj)
    {
        if (node instanceof IWorldPosItem)
        {
            return ((IWorldPosItem) node).canAccessLocation(stack, obj);
        }
        return false;
    }
    
}