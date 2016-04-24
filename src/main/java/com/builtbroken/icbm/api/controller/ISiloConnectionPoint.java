package com.builtbroken.icbm.api.controller;

import java.util.List;

/**
 * Applied to tiles that control several missiles launchers or silos
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/21/2016.
 */
public interface ISiloConnectionPoint
{
    /**
     * Gets a list of all connected silos to this tile
     *
     * @return list, or empty list
     */
    List<ISiloConnectionData> getSiloConnectionData();

    /**
     * Gets the name of the group this connector is label
     * as part of
     *
     * @return null if no group, or group name
     */
    String getConnectorGroupName();
}
