package com.builtbroken.icbm.api.warhead;

import com.builtbroken.icbm.api.modules.IWarhead;
import com.builtbroken.mc.api.items.explosives.IExplosiveContainerItem;
import com.builtbroken.mc.api.items.explosives.IExplosiveItem;
import com.builtbroken.mc.api.modules.IModuleItem;
import net.minecraft.item.ItemStack;

/**
 * Applied to items that are warheads
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/12/2016.
 */
public interface IWarheadItem extends IExplosiveItem, IExplosiveContainerItem, IModuleItem
{
    @Override
    IWarhead getModule(ItemStack stack);
}
