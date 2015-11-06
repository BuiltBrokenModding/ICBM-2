package com.builtbroken.icbm.content.crafting.missile.engine.solid;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.content.crafting.missile.engine.Engines;
import com.builtbroken.mc.prefab.inventory.InventoryUtility;
import com.builtbroken.mc.prefab.items.ItemStackWrapper;
import net.minecraft.inventory.InventoryCrafting;
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
        ItemStack fuelStack = null;
        ItemStack engine = null;
        for (int i = 0; i < grid.getSizeInventory(); i++)
        {
            ItemStack stack = grid.getStackInSlot(i);
            if (stack != null)
            {
                if (fuelStack == null && isFuel(stack))
                {
                    fuelStack = stack.copy();
                    fuelStack.stackSize = 1;
                }
                else if (fuelStack != null && InventoryUtility.stacksMatch(fuelStack, stack))
                {
                    fuelStack.stackSize += 1;
                }
                else if (stack.getItem() == ICBM.itemEngineModules && stack.getItemDamage() == Engines.COAL_ENGINE.ordinal())
                {
                    if (engine != null)
                    {
                        return null;
                    }
                    engine = stack.copy();
                    engine.stackSize = 1;
                }
                else
                {
                    //Should never happen
                    return null;
                }
            }
        }

        if (engine != null && fuelStack != null)
        {
            //Load engine from ItemStack NBT
            RocketEngineCoalPowered rocketEngine = new RocketEngineCoalPowered(engine.copy());
            rocketEngine.load();

            //Compare fuel item to current fuel store in engine
            ItemStack slotStack = rocketEngine.getInventory().getStackInSlot(0);
            if (slotStack == null || InventoryUtility.stacksMatch(slotStack, fuelStack))
            {
                //Add existing fuel to fuel stack
                if (slotStack != null)
                    fuelStack.stackSize += slotStack.stackSize;

                //Ensure max stack size is maintained
                if (fuelStack.stackSize <= fuelStack.getMaxStackSize())
                {
                    rocketEngine.getInventory().setInventorySlotContents(0, fuelStack);
                    return rocketEngine.toStack();
                }
            }
        }
        return null;
    }

    /**
     * Used to check if the stack is fule for a coal power engine
     *
     * @param stack - ItemStack to check
     * @return true if the stack is fuel
     */
    public static boolean isFuel(ItemStack stack)
    {
        return stack != null && stack.stackSize >= 1 && RocketEngineCoalPowered.FUEL_DISTANCE_VALUE.containsKey(new ItemStackWrapper(stack));
    }

    @Override
    public boolean matches(InventoryCrafting grid, World world)
    {
        return getCraftingResult(grid) != null;
    }
}