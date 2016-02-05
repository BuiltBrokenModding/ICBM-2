package com.builtbroken.icbm.api.modules;

import com.builtbroken.mc.api.event.TriggerCause;
import com.builtbroken.mc.api.explosive.IExplosiveHolder;
import com.builtbroken.mc.api.items.IExplosiveHolderItem;
import com.builtbroken.mc.lib.world.edit.WorldChangeHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Interface applied to all modules that act like warheads.
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/28/2015.
 */
public interface IWarhead extends IMissileModule, IExplosiveHolder
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
     * Sets the explosive item contained in the warhead.
     * When this item is set other methods will
     * attempt to use it for explosive callbacks.
     *
     * @param stack - explosive item, if {@link IExplosiveHolderItem}
     *              will be used in place of most methods. Will
     *              copy entire stack including stacksize when
     *              called. So ensure that stacksize is correct
     *              when calling this method.
     * @return true if the item was added
     */
    boolean setExplosive(ItemStack stack);
}
