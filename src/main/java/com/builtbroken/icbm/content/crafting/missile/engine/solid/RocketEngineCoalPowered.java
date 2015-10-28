package com.builtbroken.icbm.content.crafting.missile.engine.solid;

import com.builtbroken.icbm.api.modules.IMissileModule;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Engine that runs off of burnable fuel items. Very ineffective but works at short range.
 * Created by robert on 12/28/2014.
 */
public class RocketEngineCoalPowered extends RocketEngineSolid
{
    public static float VALUE_OF_COAL = 20f;

    public RocketEngineCoalPowered(ItemStack item)
    {
        super(item, "engine.coal");
    }

    @Override
    public float getMaxDistance(IMissileModule missile)
    {
        ItemStack stack = getInventory().getStackInSlot(0);
        if (stack != null)
        {
            if (stack.getItem() == Items.coal)
            {
                return stack.stackSize * VALUE_OF_COAL;
            }
            else if (stack.getItem() == Item.getItemFromBlock(Blocks.coal_block))
            {
                return stack.stackSize * VALUE_OF_COAL * 10;
            }
            int[] ids = OreDictionary.getOreIDs(stack);
            for (int i = 0; i < ids.length; i++)
            {
                String id = OreDictionary.getOreName(i);
                if (id.equalsIgnoreCase("blockCoal"))
                {
                    return stack.stackSize * VALUE_OF_COAL * 10;
                }
                else if (id.equalsIgnoreCase("coal") || id.equalsIgnoreCase("charcoal"))
                {
                    return stack.stackSize * VALUE_OF_COAL;
                }
            }
        }
        return 0;
    }

    @Override
    public boolean canStore(ItemStack stack, int slot, ForgeDirection side)
    {
        if (stack != null)
        {
            if (stack.getItem() == Items.coal || stack.getItem() == Item.getItemFromBlock(Blocks.coal_block))
            {
                return true;
            }
            int[] ids = OreDictionary.getOreIDs(stack);
            for (int i = 0; i < ids.length; i++)
            {
                String id = OreDictionary.getOreName(i);
                if (id.equalsIgnoreCase("blockCoal") || id.equalsIgnoreCase("coal") || id.equalsIgnoreCase("charcoal"))
                {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void initFuel()
    {
        getInventory().setInventorySlotContents(0, new ItemStack(Items.coal, 64));
    }
}
