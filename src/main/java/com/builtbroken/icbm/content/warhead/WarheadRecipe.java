package com.builtbroken.icbm.content.warhead;

import com.builtbroken.icbm.api.modules.IWarhead;
import com.builtbroken.icbm.content.crafting.missile.MissileModuleBuilder;
import com.builtbroken.icbm.content.crafting.missile.warhead.Warhead;
import com.builtbroken.icbm.content.crafting.missile.warhead.WarheadCasings;
import com.builtbroken.mc.api.explosive.IExplosiveHandler;
import com.builtbroken.mc.api.items.IExplosiveHolderItem;
import com.builtbroken.mc.api.modules.IModule;
import com.builtbroken.mc.api.modules.IModuleItem;
import com.builtbroken.mc.lib.world.explosive.ExplosiveRegistry;
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

    public WarheadRecipe(WarheadCasings casing, String ex, double size, Object... recipe)
    {
        this(MissileModuleBuilder.INSTANCE.buildWarhead(casing, ExplosiveRegistry.get(ex)).setSize(size), recipe);
    }

    public WarheadRecipe(WarheadCasings casing, IExplosiveHandler ex, double size, Object... recipe)
    {
        this(MissileModuleBuilder.INSTANCE.buildWarhead(casing, ex).setSize(size), recipe);
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
        for (int i = 0; i < grid.getSizeInventory(); i++)
        {
            ItemStack slotStack = grid.getStackInSlot(i);
            if (slotStack != null)
            {
                if (slotStack.getItem() instanceof IModuleItem)
                {
                    IModule module = ((IModuleItem) slotStack.getItem()).getModule(slotStack);
                    if (module instanceof Warhead)
                    {
                        warhead = ((Warhead) module).clone();
                    }
                }
                else
                {
                    IExplosiveHandler handler = ExplosiveRegistry.get(slotStack);
                    if (handler == craftingResult.ex)
                    {
                        warhead.ex = handler;
                        warhead.explosive = slotStack.copy();
                        warhead.explosive.stackSize = 1;
                        if (slotStack.getItem() instanceof IExplosiveHolderItem)
                        {
                            warhead.size = ((IExplosiveHolderItem) slotStack.getItem()).getExplosiveSize(slotStack);
                        }
                        else if (ExplosiveRegistry.getExplosiveSize(slotStack) != 0)
                        {
                            warhead.size = ExplosiveRegistry.getExplosiveSize(slotStack);
                        }
                    }
                }
            }
        }
        return warhead != null ? warhead.toStack() : null;
    }
}
