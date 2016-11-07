package com.builtbroken.icbm.api.missile;

import com.builtbroken.icbm.api.modules.IMissile;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

/**
 * Applied to any ItemStack that can be turned into an EntityMissile
 * <p>
 * Created by robert on 1/17/2015.
 */
public interface IMissileItem
{
    /**
     * Gets the entity that is the missile for any generic usage
     *
     * @param stack - item stack version of the missile
     * @return new entity that is an instanceof IMissileEntity
     */
    Entity getMissileEntity(ItemStack stack);

    /**
     * Gets the entity that is the missile for any generic usage
     *
     * @param stack   - item stack version of the missile
     * @param firedBy - entity that is going to fire the missile. Use the entity
     *                to line the missile up, and set any other info.
     * @return new entity that is an instanceof IMissileEntity
     */
    Entity getMissileEntity(ItemStack stack, Entity firedBy);

    /**
     * Quick access way to convert stack to the missile.
     * Does a call to {@link com.builtbroken.icbm.content.crafting.missile.MissileModuleBuilder}
     * but also does some NPE checks and data inits for corrupted objects. Fixing
     * issues with mods like Project E which like to remove all NBT when
     * generating ItemStacks.
     *
     * @param stack
     * @return
     */
    IMissile toMissile(ItemStack stack);
}
