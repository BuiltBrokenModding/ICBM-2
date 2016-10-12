package com.builtbroken.icbm.api.modules;

import com.builtbroken.mc.api.event.TriggerCause;
import com.builtbroken.mc.api.explosive.IExplosive;
import com.builtbroken.mc.api.explosive.IExplosiveContainer;
import com.builtbroken.mc.lib.world.edit.WorldChangeHelper;
import com.builtbroken.mc.prefab.inventory.InventoryUtility;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Interface applied to all modules that act like warheads.
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/28/2015.
 */
public interface IWarhead extends IMissileModule, IExplosive, IExplosiveContainer
{
    /**
     * Called to trigger the explosive in the warhead. Should just call the internal explosive code or explosive handle.
     *
     * @param triggerCause - what caused the explosive to go off
     * @param world        - current world the explosion will go off in
     * @param x            - location x
     * @param y            - location y
     * @param z            - location z
     * @return the result of the explosion, see enum for details
     */
    WorldChangeHelper.ChangeResult trigger(TriggerCause triggerCause, World world, double x, double y, double z);

    /**
     * Gets the max stack size allowed for the contained explosive.
     * -1, will denote unlimited capacity.
     *
     * @return stack limit for explosives, normally returns inventory size
     */
    int getMaxExplosives();

    /**
     * Helper method for checking if the explosive can be inserted.
     * Does not check the stack size of the explosive but should
     * check if a minimal of 1 size can be inserted.
     *
     * @param explosiveStack - explosive being inserted
     * @return true if at least 1 can be inserted
     */
    default boolean hasSpaceForExplosives(ItemStack explosiveStack)
    {
        return getExplosiveStack() == null || InventoryUtility.stacksMatch(getExplosiveStack(), explosiveStack) && getSpaceForExplosives() > 0;
    }

    /**
     * Calculates how much space is left to
     * store explosives items.
     * <p>
     * If value returned is negative assume
     * something is broken or forced stack size
     * to be larger than possible. If possible
     * try to remove extra explosives by
     * ejecting the items into the world. If removal
     * will result in loss of items ignore or
     * print out an error.
     *
     * @return space left 0 to {@link #getMaxExplosives()}
     */
    default int getSpaceForExplosives()
    {
        return getExplosiveStack() == null ? getMaxExplosives() : getMaxExplosives() - getExplosiveStack().stackSize;
    }
}
