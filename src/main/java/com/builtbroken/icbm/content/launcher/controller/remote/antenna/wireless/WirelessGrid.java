package com.builtbroken.icbm.content.launcher.controller.remote.antenna.wireless;

import com.builtbroken.mc.lib.transform.region.Cube;

import java.util.ArrayList;
import java.util.List;

/**
 * Wireless communication grid
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/20/2016.
 */
public class WirelessGrid
{
    public List<WirelessNetwork> networks = new ArrayList();

    public void connect(WirelessNetwork network)
    {
        for (WirelessNetwork net : networks)
        {
            if (net.overlaps(network))
            {
                networks.add(net);
            }
        }
    }

    public boolean overlaps(Cube cube)
    {
        for (WirelessNetwork network : networks)
        {
            if (network.overlaps(cube))
            {
                return true;
            }
        }
        return false;
    }
}
