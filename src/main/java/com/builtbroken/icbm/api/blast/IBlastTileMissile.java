package com.builtbroken.icbm.api.blast;

import com.builtbroken.icbm.api.missile.IMissileEntity;
import com.builtbroken.icbm.api.modules.IMissile;
import com.builtbroken.mc.api.explosive.IBlast;

/**
 * Applied to blasts that are static and remain in place for as a tile missile
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/30/2016.
 */
public interface IBlastTileMissile extends IBlast
{
    /**
     * Should we spawn a tile on impact with the ground
     *
     * @param missile - missile object data
     * @param entity  - entity hosting the missile data
     * @return true if a tile should be spawned
     */
    default boolean spawnTile(IMissile missile, IMissileEntity entity)
    {
        return false;
    }
}
