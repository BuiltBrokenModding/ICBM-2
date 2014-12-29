package com.builtbroken.icbm.content.crafting.missile.engine.solid;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Created by robert on 12/28/2014.
 */
public class EngineCoalPowered extends EngineSolid
{
    public EngineCoalPowered(ItemStack item)
    {
        super(item);
    }

    @Override
    public boolean canStore(ItemStack stack, int slot, ForgeDirection side)
    {
        if(stack != null)
        {
            if(stack.getItem() == Items.coal || stack.getItem() == Item.getItemFromBlock(Blocks.coal_block))
            {
                return true;
            }
            int[] ids = OreDictionary.getOreIDs(stack);
            for(int i = 0; i < ids.length; i++)
            {
                String id = OreDictionary.getOreName(i);
                if(id.equalsIgnoreCase("blockCoal") || id.equalsIgnoreCase("coal") || id.equalsIgnoreCase("charcoal"))
                {
                    return true;
                }
            }
        }
        return false;
    }
}
