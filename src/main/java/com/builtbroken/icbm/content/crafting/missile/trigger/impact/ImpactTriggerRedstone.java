package com.builtbroken.icbm.content.crafting.missile.trigger.impact;

import com.builtbroken.icbm.content.crafting.missile.trigger.Triggers;
import net.minecraft.item.ItemStack;

/**
 * Pressure plate driven trigger that activates the warhead by redstone
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/31/2016.
 */
public class ImpactTriggerRedstone extends ImpactTrigger
{
    //TODO add handling for shacking of the missile
    //TODO add handling for entity bumping into the missile
    public ImpactTriggerRedstone(ItemStack item)
    {
        super(item, Triggers.REDSTONE_IMPACT);
    }

    @Override
    public boolean enableTrigger(boolean yes)
    {
        return true;
    }

    @Override
    public boolean canToogleTriggerEnabled()
    {
        return false;
    }

    @Override
    public boolean isTriggerEnabled()
    {
        return true;
    }
}
