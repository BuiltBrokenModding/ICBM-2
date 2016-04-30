package com.builtbroken.icbm.api.blast;

import com.builtbroken.icbm.api.missile.IMissileEntity;
import com.builtbroken.icbm.api.modules.IMissile;
import com.builtbroken.mc.api.explosive.IExplosiveHandler;

/**
 * Version of the explosive handler that spawns a missile tile on impact
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/30/2016.
 */
public interface IExHandlerTileMissile extends IExplosiveHandler
{

    /**
     * Should we spawn a tile on impact with the ground
     *
     * @param missile - missile object data
     * @param entity  - entity hosting the missile data, may be null if called by the tile
     * @return true if a tile should be spawned
     */
    boolean doesSpawnMissileTile(IMissile missile, IMissileEntity entity);
}
