package com.builtbroken.icbm.content.warhead;

import com.builtbroken.icbm.api.crafting.IModularMissileItem;
import com.builtbroken.icbm.api.modules.IWarhead;
import com.builtbroken.icbm.content.crafting.missile.MissileModuleBuilder;
import com.builtbroken.icbm.content.crafting.missile.casing.Missile;
import com.builtbroken.icbm.content.crafting.missile.casing.MissileCasings;
import com.builtbroken.icbm.content.crafting.missile.warhead.Warhead;
import com.builtbroken.mc.api.explosive.IExplosiveHandler;
import com.builtbroken.mc.api.modules.IModule;
import com.builtbroken.mc.api.modules.IModuleItem;
import com.builtbroken.mc.prefab.inventory.InventoryUtility;
import com.builtbroken.mc.prefab.recipe.item.RecipeShapelessOre;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/6/2015.
 */
public class MicroMissileRecipe extends RecipeShapelessOre
{
    public MicroMissileRecipe(IExplosiveHandler ex, Object... recipe)
    {
        this(MissileModuleBuilder.INSTANCE.buildMissile(MissileCasings.MICRO, ex).toStack(), recipe);
    }

    public MicroMissileRecipe(ItemStack result, Object... recipe)
    {
        super(result, recipe);
    }

    @Override
    protected boolean itemMatches(ItemStack target, ItemStack input, boolean strict)
    {
        if (!super.itemMatches(target, input, strict))
        {
            if (target != null && input != null && target.getItem() instanceof IModularMissileItem && input.getItem() instanceof IModularMissileItem)
            {
                //Warhead in missile input need to match recipe warhead required
                return InventoryUtility.stacksMatch(((IModularMissileItem) target.getItem()).getWarhead(target), ((IModularMissileItem) input.getItem()).getWarhead(input));
            }
            return false;
        }
        return true;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting var1)
    {
        ItemStack missileStack = null;
        ItemStack warheadStack = null;
        //Find warhead and missile stacks
        for (int i = 0; i < var1.getSizeInventory(); i++)
        {
            ItemStack slot = var1.getStackInSlot(i);
            if (slot != null)
            {
                if (slot.getItem() instanceof IModularMissileItem)
                {
                    if (missileStack != null)
                        return null;
                    missileStack = slot.copy();
                }
                else if (slot.getItem() instanceof IModuleItem)
                {
                    IModule module = ((IModuleItem) slot.getItem()).getModule(slot);
                    if (module instanceof IWarhead)
                    {
                        if (warheadStack != null)
                            return null;
                        warheadStack = slot.copy();
                    }
                }
                else
                {
                    return null;
                }
            }
        }
        //Check if stacks are valid
        if (missileStack != null && warheadStack != null)
        {
            //Generate objects from stack
            Missile missile = MissileModuleBuilder.INSTANCE.buildMissile(missileStack);
            Warhead warhead = MissileModuleBuilder.INSTANCE.buildWarhead(warheadStack);
            //Validate
            if (missile != null && warhead != null && missile.getWarhead() == null)
            {
                //Insert warhead and convert back into stack
                missile.setWarhead(warhead);
                return missile.toStack();
            }
        }
        return null;
    }
}
