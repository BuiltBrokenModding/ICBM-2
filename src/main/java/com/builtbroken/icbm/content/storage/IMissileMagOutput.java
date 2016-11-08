package com.builtbroken.icbm.content.storage;

import com.builtbroken.icbm.api.modules.IMissile;

/**
 * Tiles that can be used as outputs for the mag system
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/8/2016.
 */
public interface IMissileMagOutput
{
    /**
     * Called to check if the missile can be stored.
     *
     * @param missile - missile
     * @return true if the missile can be stored
     */
    boolean canAcceptMissile(IMissile missile);

    /**
     * Called to store the missile
     *
     * @param missile - missile, can be converted to an item
     * @return true if it was stored
     */
    boolean storeMissile(IMissile missile);
}
