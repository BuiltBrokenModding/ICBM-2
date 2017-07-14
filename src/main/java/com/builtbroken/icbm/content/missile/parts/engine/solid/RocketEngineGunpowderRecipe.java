package com.builtbroken.icbm.content.missile.parts.engine.solid;

import com.builtbroken.icbm.api.ICBM_API;
import com.builtbroken.icbm.content.missile.parts.engine.Engines;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class RocketEngineGunpowderRecipe extends ShapelessOreRecipe
{
    public RocketEngineGunpowderRecipe(ItemStack result, Object... recipe)
    {
        super(result, recipe);
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting grid)
    {
        int gunpowderCount = 0;
        ItemStack engine = null;
        for (int i = 0; i < grid.getSizeInventory(); i++)
        {
            ItemStack stack = grid.getStackInSlot(i);
            if (stack != null)
            {
                if (stack.getItem() == Items.gunpowder)
                {
                    gunpowderCount += 1;
                }
                else if (stack.getItem() == ICBM_API.itemEngineModules && stack.getItemDamage() == Engines.GUNPOWDER_ENGINE.ordinal())
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
        if (engine != null && gunpowderCount > 0)
        {
            RocketEngineGunpowder rocketEngine = new RocketEngineGunpowder(engine.copy());
            rocketEngine.load();
            if (rocketEngine.getInventory().getStackInSlot(0) == null)
            {
                rocketEngine.getInventory().setInventorySlotContents(0, new ItemStack(Items.gunpowder, gunpowderCount));
            }
            else if (rocketEngine.getInventory().getStackInSlot(0).getItem() == Items.gunpowder)
            {
                gunpowderCount += rocketEngine.getInventory().getStackInSlot(0).stackSize;
                if (gunpowderCount > Items.gunpowder.getItemStackLimit(rocketEngine.getInventory().getStackInSlot(0)))
                {
                    return null;
                }
                else
                {
                    rocketEngine.getInventory().getStackInSlot(0).stackSize = gunpowderCount;
                }
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