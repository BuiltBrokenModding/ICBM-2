package com.builtbroken.icbm.content.missile.parts.engine.solid;

import com.builtbroken.icbm.api.ICBM_API;
import com.builtbroken.icbm.content.missile.parts.MissileModuleBuilder;
import com.builtbroken.icbm.content.missile.parts.engine.Engines;
import com.builtbroken.icbm.content.missile.parts.engine.RocketEngine;
import com.builtbroken.mc.lib.data.item.ItemStackWrapper;
import com.builtbroken.mc.prefab.inventory.InventoryUtility;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapelessOreRecipe;

/**
 * Recipe for handling shapelessly adding fuel items to engine
 * Created by robert
 */
public class RocketEngineCoalRecipe extends ShapelessOreRecipe
{
    //TODO create an abstract version of this class that can take any engine and add any solid fuel to it. Given that engine supports solid fuel and crafting refuel
    public RocketEngineCoalRecipe(ItemStack result, Object... recipe)
    {
        super(result, recipe);
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting grid)
    {
        ItemStack fuelStack = null;
        ItemStack engineStack = null;

        //Map inventory to expected data
        for (int i = 0; i < grid.getSizeInventory(); i++) //TODO maybe switch to an iterator?
        {
            final ItemStack slotStack = grid.getStackInSlot(i);

            //We only care about full slots
            if (slotStack != null)
            {
                //Init first fuel item
                if (fuelStack == null && isFuel(slotStack))
                {
                    //Copy stack and set to 1
                    fuelStack = slotStack.copy();
                    fuelStack.stackSize = 1;
                }
                //Match each next fuel item to first item
                else if (fuelStack != null && InventoryUtility.stacksMatch(fuelStack, slotStack))
                {
                    //Only increase by 1 since we can only consume 1 per slot
                    fuelStack.stackSize += 1;
                }
                //TODO why do we check item data here but module type later?
                else if (slotStack.getItem() == ICBM_API.itemEngineModules && slotStack.getItemDamage() == Engines.COAL_ENGINE.ordinal())
                {
                    if (engineStack != null)
                    {
                        return null;
                    }

                    //Copy stack and set to 1 item
                    engineStack = slotStack.copy();
                    engineStack.stackSize = 1;
                }
                //If not the same fuel, or not an engine then stop crafting
                else
                {
                    return null;
                }
            }
        }

        if (engineStack != null && fuelStack != null)
        {
            //Build engine instance
            RocketEngine rocketEngine = MissileModuleBuilder.INSTANCE.buildEngine(engineStack);
            if (rocketEngine != null)
            {
                //Load engine from ItemStack NBT
                rocketEngine.load(); //TODO is this needed?

                if (rocketEngine instanceof RocketEngineCoalPowered)
                {
                    //Get existing fuel in engine
                    ItemStack fuelStackInEngine = ((RocketEngineCoalPowered) rocketEngine).fuelStack();

                    //Make sure we can insert the fuel into the engine
                    if (fuelStackInEngine == null || InventoryUtility.stacksMatch(fuelStackInEngine, fuelStack)) //TODO maybe add a Engine#canUseFuel(ItemStack) check
                    {
                        //Add existing fuel to fuel stack if exists
                        if (fuelStackInEngine != null)
                        {
                            fuelStack.stackSize += fuelStackInEngine.stackSize;
                        }

                        //Ensure max stack size is maintained
                        if (fuelStack.stackSize <= fuelStack.getMaxStackSize())
                        {
                            //Update engine's fuel stack
                            ((RocketEngineCoalPowered) rocketEngine).getInventory().setInventorySlotContents(0, fuelStack);

                            //Return new engine stack
                            return rocketEngine.toStack();
                        }
                    }
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