package com.builtbroken.icbm.content.launcher.controller.remote.antenna.wireless;

/**
 * Applied to tiles that connect to the grid and want information about what the grid is doing.
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/21/2016.
 */
public interface IWirelessGridConnector
{
    void onCoverageAreaChanged(WirelessNetwork network);

    /**
     * Called when a major changed happens to the
     * grid. Such as merging several networks
     * together changing the coverage area.
     */
    void resetNetworkCache();
}
