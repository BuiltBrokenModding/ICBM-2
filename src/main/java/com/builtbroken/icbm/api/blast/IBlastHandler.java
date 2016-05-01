package com.builtbroken.icbm.api.blast;

import com.builtbroken.icbm.api.missile.IMissileEntity;
import com.builtbroken.icbm.api.modules.IMissile;
import com.builtbroken.icbm.api.modules.IWarhead;
import com.builtbroken.mc.api.explosive.IExplosiveHandler;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * ICBM version of the explosive handler with additional information
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 5/1/2016.
 */
public interface IBlastHandler extends IExplosiveHandler
{
    /**
     * Gets all drops to release after a missile has detonated or impacted
     *
     * @param entity      - missile entity
     * @param missile     - missile data
     * @param warhead     - warhead that held this explosive
     * @param warheadBlew - warhead did blow up
     * @param engineBlew  - engine did blow up
     * @return list or empty list, null will drop defaults
     */
    default List<ItemStack> getDropsForMissile(IMissileEntity entity, IMissile missile, IWarhead warhead, boolean warheadBlew, boolean engineBlew)
    {
        return null;
    }

    /**
     * Does the explosion vaporize everything contained in the missile
     *
     * @param entity      - missile entity
     * @param missile     - missile data
     * @param warhead     - warhead that held this explosive
     * @param warheadBlew - warhead did blow up
     * @param engineBlew  - engine did blow up
     * @return true to vaporize
     */
    default boolean doesVaporizeParts(IMissileEntity entity, IMissile missile, IWarhead warhead, boolean warheadBlew, boolean engineBlew)
    {
        return false;
    }

    /**
     * Does the warhead or engine blowing up cause damage to the missiles. If false then the missile will
     * attempt to drop itself as an entity or block into the ground. This way the missile can be
     * recovered by a player
     *
     * @param entity      - missile entity
     * @param missile     - missile data
     * @param warhead     - warhead that held this explosive
     * @param warheadBlew - warhead did blow up
     * @param engineBlew  - engine did blow up
     * @return true if the missile is damaged
     */
    default boolean doesDamageMissile(IMissileEntity entity, IMissile missile, IWarhead warhead, boolean warheadBlew, boolean engineBlew)
    {
        return engineBlew || warheadBlew;
    }

    /**
     * Can the missile drop at all, ignoring anything that happens to the missile.
     * <p>
     * Used for magic based explosives to prevent dropping the entity or block.
     *
     * @param entity  - missile entity
     * @param missile - missile data
     * @param warhead - warhead that held this explosive
     * @return true if allow the missile to drop
     */
    default boolean allowMissileToDrop(IMissileEntity entity, IMissile missile, IWarhead warhead)
    {
        return true;
    }
}
