package com.builtbroken.icbm.content.crafting.missile.trigger;

import com.builtbroken.icbm.api.warhead.ITrigger;
import com.builtbroken.icbm.content.crafting.AbstractModule;
import net.minecraft.item.ItemStack;

/**
 * Prefab for all triggers implemented in ICBM
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/21/2016.
 */
public abstract class Trigger extends AbstractModule implements ITrigger
{
    public Trigger(ItemStack item, String name)
    {
        super(item, name);
    }
}
