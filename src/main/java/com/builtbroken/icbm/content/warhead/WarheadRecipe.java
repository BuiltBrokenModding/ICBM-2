package com.builtbroken.icbm.content.warhead;

import com.builtbroken.icbm.content.crafting.missile.warhead.Warhead;
import com.builtbroken.icbm.content.crafting.parts.ItemExplosive;
import com.builtbroken.mc.api.explosive.IExplosiveHandler;
import com.builtbroken.mc.api.modules.IModule;
import com.builtbroken.mc.api.modules.IModuleItem;
import com.builtbroken.mc.lib.world.explosive.ExplosiveRegistry;
import com.builtbroken.mc.prefab.inventory.InventoryUtility;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

/**
 * Simple recipe for crafting warheads with explosive materials. Designed to prevent overriding the explosive in the warhead by mistake.
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/6/2015.
 */
public class WarheadRecipe implements IRecipe
{
    //TODO replace dependency on shapeless recipe with custom build using only interface
    private final Warhead craftingResult;
    private final ItemStack inputTarget;

    public WarheadRecipe(Warhead warhead, ItemStack inputTarget)
    {
        this.craftingResult = warhead;
        this.inputTarget = inputTarget;
    }

    @Override
    public boolean matches(InventoryCrafting grid, World world)
    {
        if(inputTarget.getItem() instanceof ItemExplosive && inputTarget.getItemDamage() == 4 && grid.getStackInSlot(1) != null && grid.getStackInSlot(1).getItemDamage() == 4)
        {
            int i = 1;
        }
        boolean warhead = false;
        boolean ex = false;
        for (int x = 0; x < grid.getSizeInventory(); x++)
        {
            ItemStack slotStack = grid.getStackInSlot(x);
            if (slotStack != null)
            {
                if (slotStack.getItem() instanceof IModuleItem)
                {
                    IModule module = ((IModuleItem) slotStack.getItem()).getModule(slotStack);
                    if (module instanceof Warhead)
                    {
                        if (warhead)
                        {
                            return false;
                        }
                        else if (((Warhead) module).getExplosive() == null || InventoryUtility.stacksMatch(((Warhead) module).getExplosiveStack(), inputTarget))
                        {
                            warhead = true;
                        }
                        else
                        {
                            //Warhead explosives do not match, return null to prevent overriding explosive values
                            return false;
                        }
                    }
                }
                else if (InventoryUtility.stacksMatch(slotStack, inputTarget))
                {
                    ex = true;
                }
                else
                {
                    return false;
                }
            }
        }
        return warhead && ex;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting grid)
    {
        Warhead warhead = null;
        ItemStack explosive = null;

        //Loop threw slots looking for expects items
        for (int i = 0; i < grid.getSizeInventory(); i++)
        {
            final ItemStack slotStack = grid.getStackInSlot(i);
            final IExplosiveHandler slotExplosiveHandler = ExplosiveRegistry.get(slotStack);

            if (slotStack != null)
            {
                //Find warhead
                if (slotStack.getItem() instanceof IModuleItem)
                {
                    IModule module = ((IModuleItem) slotStack.getItem()).getModule(slotStack);
                    if (module instanceof Warhead)
                    {
                        if (((Warhead) module).getExplosive() == null || ((Warhead) module).getExplosive() == craftingResult.getExplosive())
                        {
                            warhead = ((Warhead) module).clone();
                        }
                        else
                        {
                            //Warhead explosives do not match, return null to prevent overriding explosive values
                            return null;
                        }
                    }
                }
                //Count explosive items
                else if (slotExplosiveHandler == craftingResult.getExplosive())
                {
                    if (explosive == null)
                    {
                        explosive = slotStack.copy();
                        explosive.stackSize = 1;
                    }
                    //Does slot match expected
                    else if (InventoryUtility.stacksMatch(explosive, slotStack))
                    {
                        explosive.stackSize += 1;
                    }
                    else
                    {
                        //Items do not match expected value, or warhead is full
                        return null;
                    }
                }
            }
        }
        //Only set data if warhead is found and explosives are found
        if (warhead != null && explosive != null)
        {
            if (warhead.getExplosiveStack() == null)
            {
                warhead.setExplosive(explosive.copy());
                return warhead.toStack();
            }
            else if (warhead.getExplosiveStack().stackSize + explosive.stackSize <= warhead.getMaxExplosives())
            {
                warhead.getExplosiveStack().stackSize += explosive.stackSize;
                return warhead.toStack();
            }
        }
        return null;
    }

    @Override
    public int getRecipeSize()
    {
        return 2;
    }

    @Override
    public ItemStack getRecipeOutput()
    {
        return craftingResult.toStack();
    }
}
