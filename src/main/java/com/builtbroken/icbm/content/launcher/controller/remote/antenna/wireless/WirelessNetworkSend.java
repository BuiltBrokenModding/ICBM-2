package com.builtbroken.icbm.content.launcher.controller.remote.antenna.wireless;

import com.builtbroken.mc.api.map.radio.IRadioWaveSender;

import java.util.ArrayList;
import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/21/2016.
 */
public class WirelessNetworkSend extends WirelessNetwork
{
    protected List<IRadioWaveSender> senders = new ArrayList();

    public WirelessNetworkSend()
    {
        super(false);
    }

    public boolean addSender(IRadioWaveSender sender)
    {
        if(!senders.contains(sender))
        {
            //TODO update coverage area
            return senders.add(sender);
        }
        return false;
    }

    public boolean removeSender(IRadioWaveSender sender)
    {
        if(senders.contains(sender))
        {
            //TODO update coverage area
            return senders.remove(sender);
        }
        return false;
    }
}
