package com.builtbroken.icbm.content.warhead;

import com.builtbroken.icbm.api.modules.IWarhead;
import com.builtbroken.icbm.content.crafting.missile.MissileModuleBuilder;
import com.builtbroken.icbm.content.crafting.missile.warhead.Warhead;
import com.builtbroken.icbm.content.crafting.missile.warhead.WarheadCasings;
import com.builtbroken.mc.api.explosive.IExplosiveHandler;
import com.builtbroken.mc.api.modules.IModule;
import com.builtbroken.mc.api.modules.IModuleItem;
import com.builtbroken.mc.lib.world.explosive.ExplosiveRegistry;
import com.builtbroken.mc.prefab.inventory.InventoryUtility;
import com.builtbroken.mc.prefab.recipe.item.RecipeShapelessTool;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;

/**
 * Simple recipe for crafting warheads with explosive materials. Designed to prevent overriding the explosive in the warhead by mistake.
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/6/2015.
 */
public class WarheadRecipe extends RecipeShapelessTool
{
    private final Warhead craftingResult;

    public WarheadRecipe(WarheadCasings casing, IExplosiveHandler ex, Object... recipe)
    {
        this(MissileModuleBuilder.INSTANCE.buildWarhead(casing, ex), recipe);
    }

    public WarheadRecipe(Warhead warhead, Object... recipe)
    {
        super(warhead.toStack(), recipe);
        craftingResult = warhead;
    }

    @Override
    public boolean itemMatches(ItemStack target, ItemStack input, boolean strict)
    {
        if (!super.itemMatches(target, input, strict))
        {
            if (target != null && target.getItem() instanceof IModuleItem && input != null && input.getItem() instanceof IModuleItem)
            {
                IModule targetModule = ((IModuleItem) target.getItem()).getModule(target);
                IModule inputModule = ((IModuleItem) input.getItem()).getModule(input);
                if (targetModule instanceof IWarhead && inputModule instanceof IWarhead)
                {
                    return ((IWarhead) targetModule).getExplosive() == ((IWarhead) inputModule).getExplosive();
                }
            }
            return false;
        }
        return true;
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
}
