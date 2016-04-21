package com.builtbroken.icbm.content.launcher.controller.remote.antenna.wireless;

import com.builtbroken.mc.api.map.radio.IRadioWaveReceiver;

import java.util.ArrayList;
import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/21/2016.
 */
public class WirelessNetworkReceive extends WirelessNetwork
{
    protected List<IRadioWaveReceiver> receivers = new ArrayList();

    public WirelessNetworkReceive()
    {
        super(true);
    }

    public boolean addReceiver(IRadioWaveReceiver receiver)
    {
        if(!receivers.contains(receiver))
        {
            //TODO update coverage area
            return receivers.add(receiver);
        }
        return false;
    }

    public boolean removeReceiver(IRadioWaveReceiver receiver)
    {
        if(receivers.contains(receiver))
        {
            //TODO update coverage area
            return receivers.remove(receiver);
        }
        return false;
    }
}
