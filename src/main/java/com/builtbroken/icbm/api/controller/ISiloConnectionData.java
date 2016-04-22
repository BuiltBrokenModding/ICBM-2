package com.builtbroken.icbm.api.controller;

import com.builtbroken.mc.api.IWorldPosition;
import com.builtbroken.mc.api.map.radio.wireless.ConnectionStatus;

/**
 * Data pointer for information about a silo
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/21/2016.
 */
public interface ISiloConnectionData
{
    /**
     * User defined display name for the silo.
     *
     * @return name of the silo
     */
    String getSiloName();

    /**
     * Position data for the silo
     *
     * @return location, null is considered invalid data
     */
    IWorldPosition getSiloLocation();

    /**
     * First thing checked.
     * <p>
     * Gets the connection status for the silo. If
     * tile is missing return OFFLINE and attempt
     * to see if the tile still exists.
     *
     * @return status
     */
    ConnectionStatus getSiloStatus();
}
