package com.builtbroken.icbm.api.warhead;

import net.minecraft.item.ItemStack;

/**
 * Applied to warheads that can accept a trigger, by default the trigger is normally an impact sensor.
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/21/2016.
 */
public interface ITriggerAccepter
{
    /**
     * Gets the main trigger
     *
     * @return trigger, or null i none are installed
     */
    ITrigger getTrigger();

    /**
     * Sets the trigger directly with an object
     *
     * @param trigger
     * @return true if the trigger was accepted.
     */
    boolean setTrigger(ITrigger trigger);

    /**
     * Sets the trigger indirectly as an ItemStack
     *
     * @param stack - stack, items should be consumed
     * @return previous trigger or null if accepted, returns
     * exact stack if rejected.
     */
    ItemStack setTrigger(ItemStack stack);
}
