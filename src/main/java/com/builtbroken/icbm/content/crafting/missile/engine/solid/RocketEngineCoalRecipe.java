package com.builtbroken.icbm.content.crafting.missile.engine.solid;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.content.crafting.missile.engine.Engines;
import com.builtbroken.mc.prefab.inventory.InventoryUtility;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class RocketEngineCoalRecipe extends ShapelessOreRecipe
{
    //TODO abstract
    public RocketEngineCoalRecipe(ItemStack result, Object... recipe)
    {
        super(result, recipe);
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting grid)
    {
        int coal = 0;
        int charCoal = 0;
        int coalBlock = 0;
        ItemStack engine = null;
        for (int i = 0; i < grid.getSizeInventory(); i++)
        {
            ItemStack stack = grid.getStackInSlot(i);
            if (stack != null)
            {
                if (stack.getItem() == Items.coal && stack.getItemDamage() == 0)
                {
                    if (coalBlock > 0 || charCoal > 0)
                        return null;
                    coal += 1;
                }
                else if (stack.getItem() == Items.coal && stack.getItemDamage() == 0)
                {
                    if (coal > 0 || coalBlock > 0)
                        return null;
                    charCoal += 1;
                }
                else if (stack.getItem() == Item.getItemFromBlock(Blocks.coal_block))
                {
                    if (coal > 0 || charCoal > 0)
                        return null;
                    coalBlock += 1;
                }
                else if (stack.getItem() == ICBM.itemEngineModules && stack.getItemDamage() == Engines.GUNPOWDER_ENGINE.ordinal())
                {
                    if (engine == null)
                    {
                        engine = stack;
                    }
                    else
                    {
                        //Should never happen
                        return null;
                    }
                }
                else
                {
                    //Should never happen
                    return null;
                }
            }
        }

        if (engine != null && coal > 0)
        {
            RocketEngineCoalPowered rocketEngine = new RocketEngineCoalPowered(engine.copy());
            rocketEngine.load();

            ItemStack item;
            if (coal != 0)
                item = new ItemStack(Items.coal, coal, 0);
            else if (charCoal != 0)
                item = new ItemStack(Items.coal, coal, 1);
            else if (coalBlock != 0)
                item = new ItemStack(Blocks.coal_block, coal);
            else
                return null;

            if (rocketEngine.getInventory().getStackInSlot(0) == null)
            {
                rocketEngine.getInventory().setInventorySlotContents(0, item);
            }
            else if (InventoryUtility.stacksMatch(rocketEngine.getInventory().getStackInSlot(0), item))
            {
                item.stackSize += rocketEngine.getInventory().getStackInSlot(0).stackSize;
                if (item.stackSize > item.getMaxStackSize())
                {
                    return null;
                }
                else
                {
                    rocketEngine.getInventory().getStackInSlot(0).stackSize = coal;
                }
            }
            else
            {
                //Stack's don't match
                return null;
            }
            return rocketEngine.toStack();
        }
        return null;
    }

    @Override
    public boolean matches(InventoryCrafting grid, World world)
    {
        return getCraftingResult(grid) != null;
    }
}