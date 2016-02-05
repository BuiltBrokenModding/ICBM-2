package com.builtbroken.icbm.content.crafting.missile;

import com.builtbroken.mc.api.modules.IModule;
import com.builtbroken.mc.api.modules.IModuleItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Prefab for implementing missile modules
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/26/2015.
 */
public class ItemAbstractModule extends Item implements IModuleItem
{
    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        IModule module = getModule(stack);
        if (module != null)
        {
            return module.getUnlocalizedName();
        }
        return super.getUnlocalizedName(stack);
    }

    @Override
    public IModule getModule(ItemStack stack)
    {
        if (stack != null)
        {
            ItemStack insert = stack.copy();
            insert.stackSize = 1;
            return MissileModuleBuilder.INSTANCE.build(insert);
        }
        return null;
    }
}
