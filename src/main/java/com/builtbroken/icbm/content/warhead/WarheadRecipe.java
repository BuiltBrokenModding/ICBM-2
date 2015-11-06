package com.builtbroken.icbm.content.warhead;

import com.builtbroken.icbm.api.modules.IWarhead;
import com.builtbroken.icbm.content.crafting.missile.MissileModuleBuilder;
import com.builtbroken.icbm.content.crafting.missile.warhead.Warhead;
import com.builtbroken.icbm.content.crafting.missile.warhead.WarheadCasings;
import com.builtbroken.mc.api.explosive.IExplosiveHandler;
import com.builtbroken.mc.api.modules.IModule;
import com.builtbroken.mc.api.modules.IModuleItem;
import com.builtbroken.mc.lib.world.explosive.ExplosiveRegistry;
import com.builtbroken.mc.prefab.recipe.item.RecipeShapelessTool;
import net.minecraft.item.ItemStack;

/**
 * Simple recipe for crafting warheads with explosive materials. Designed to prevent overriding the explosive in the warhead by mistake.
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/6/2015.
 */
public class WarheadRecipe extends RecipeShapelessTool
{
    public WarheadRecipe(WarheadCasings size, String ex, Object... recipe)
    {
        this(MissileModuleBuilder.INSTANCE.buildWarhead(size, ExplosiveRegistry.get(ex)), recipe);
    }

    public WarheadRecipe(WarheadCasings size, IExplosiveHandler ex, Object... recipe)
    {
        this(MissileModuleBuilder.INSTANCE.buildWarhead(size, ex), recipe);
    }

    public WarheadRecipe(Warhead warhead, Object... recipe)
    {
        super(warhead.toStack(), recipe);
    }

    public WarheadRecipe(ItemStack result, Object... recipe)
    {
        super(result, recipe);
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
}
