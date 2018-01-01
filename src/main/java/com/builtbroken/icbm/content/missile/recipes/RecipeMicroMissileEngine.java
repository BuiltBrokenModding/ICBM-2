package com.builtbroken.icbm.content.missile.recipes;

import com.builtbroken.icbm.api.ICBM_API;
import com.builtbroken.icbm.api.crafting.IModularMissileItem;
import com.builtbroken.icbm.api.missile.IMissileItem;
import com.builtbroken.icbm.api.modules.IMissile;
import com.builtbroken.icbm.api.modules.IRocketEngine;
import com.builtbroken.icbm.content.missile.data.missile.MissileSize;
import com.builtbroken.icbm.content.missile.parts.MissileModuleBuilder;
import com.builtbroken.mc.api.modules.IModule;
import com.builtbroken.mc.api.modules.IModuleItem;
import com.builtbroken.mc.framework.recipe.item.RecipeShapelessOre;
import com.builtbroken.mc.prefab.inventory.InventoryUtility;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/6/2015.
 */
public class RecipeMicroMissileEngine extends RecipeShapelessOre
{
    public RecipeMicroMissileEngine()
    {
        super(new ItemStack(ICBM_API.itemMissile, 1, MissileSize.MICRO.ordinal()),
                new ItemStack(ICBM_API.itemMissile, 1, MissileSize.MICRO.ordinal()),
                ICBM_API.itemEngineModules);
    }

    @Override
    protected boolean itemMatches(ItemStack target, ItemStack input, boolean strict)
    {
        if (!super.itemMatches(target, input, strict))
        {
            if (target != null && input != null)
            {
                if (target != null && input != null && target.getItem() instanceof IModularMissileItem && input.getItem() instanceof IModularMissileItem)
                {
                    ItemStack engineA = ((IModularMissileItem) target.getItem()).getEngine(target);
                    ItemStack engineB = ((IModularMissileItem) input.getItem()).getEngine(input);
                    return InventoryUtility.stacksMatch(engineA, engineB);
                }
                else if (target.getItem() instanceof IModuleItem && input.getItem() instanceof IModuleItem)
                {
                    return ((IModuleItem) target.getItem()).getModule(target) instanceof IRocketEngine
                            && ((IModuleItem) input.getItem()).getModule(input) instanceof IRocketEngine;
                }
            }
            return false;
        }
        return true;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting var1)
    {
        ItemStack missileStack = null;
        ItemStack engineStack = null;
        //Find engine and missile stacks
        for (int i = 0; i < var1.getSizeInventory(); i++)
        {
            final ItemStack slot = var1.getStackInSlot(i);
            if (slot != null)
            {
                //Find missile stack
                if (slot.getItem() instanceof IModularMissileItem)
                {
                    //Only one missile
                    if (missileStack != null)
                    {
                        return null;
                    }
                    missileStack = slot.copy();
                }
                //Find engine stack
                else if (slot.getItem() instanceof IModuleItem)
                {
                    IModule module = ((IModuleItem) slot.getItem()).getModule(slot);
                    if (module instanceof IRocketEngine)
                    {
                        //Only one warhead
                        if (engineStack != null)
                        {
                            return null;
                        }
                        engineStack = slot.copy();
                    }
                }
                //Anything else is wrong
                else
                {
                    return null;
                }
            }
        }
        //Check if stacks are valid
        if (missileStack != null && engineStack != null)
        {
            //Generate objects from stack
            IMissile missile = missileStack.getItem() instanceof IMissileItem ? ((IMissileItem) missileStack.getItem()).toMissile(missileStack) : null;
            IRocketEngine engine = MissileModuleBuilder.INSTANCE.buildEngine(engineStack);
            //Validate
            if (missile != null && engine != null && missile.getEngine() == null)
            {
                //Insert warhead and convert back into stack
                missile.setEngine(engine);
                return missile.toStack();
            }
        }
        return null;
    }
}
