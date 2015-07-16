package com.builtbroken.icbm.api;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

/** Applied to any ItemStack that can be turned into an EntityMissile
 *
 * Created by robert on 1/17/2015.
 */
public interface IMissileItem
{
    /** Gets the entity that is the missile for any generic usage
     * @param stack - item stack version of the missile
     * @return new entity that is an instanceof IMissile
     */
    Entity getMissileEntity(ItemStack stack);

    /** Gets the entity that is the missile for any generic usage
     * @param stack - item stack version of the missile
     * @param firedBy - entity that is going to fire the missile. Use the entity
     *                to line the missile up, and set any other info.
     * @return new entity that is an instanceof IMissile
     */
    Entity getMissileEntity(ItemStack stack, Entity firedBy);
}
