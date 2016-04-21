package com.builtbroken.icbm.content.launcher.controller.remote.antenna.wireless;

import com.builtbroken.mc.api.map.radio.IRadioWaveReceiver;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;

import java.util.ArrayList;
import java.util.Iterator;
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
        if(!receivers.contains(receiver) && receiver.getRadioReceiverRange() != null)
        {
            isInvalid = false;
            coverageArea.add(receiver.getRadioReceiverRange());
            return receivers.add(receiver);
        }
        return false;
    }

    public boolean removeReceiver(IRadioWaveReceiver receiver)
    {
        if(receivers.contains(receiver))
        {
            if(this.receivers.size() == 0)
            {
                isInvalid = true;
            }
            return receivers.remove(receiver);
        }
        return false;
    }

    @Override
    public void doCleanUp()
    {
        super.doCleanUp();
        Iterator<IRadioWaveReceiver> itRec = receivers.iterator();
        while(itRec.hasNext())
        {
            IRadioWaveReceiver receiver = itRec.next();
            if(receiver.getRadioReceiverRange() == null)
            {
                itRec.remove();
            }
            else if(receiver instanceof Entity && !((Entity) receiver).isEntityAlive())
            {
                itRec.remove();
            }
            else if(receiver instanceof TileEntity && ((TileEntity) receiver).isInvalid())
            {
                itRec.remove();
            }
        }
        if(receivers.size() == 0)
        {
            isInvalid = true;
        }
        else
        {
            coverageArea.clear();
            for(IRadioWaveReceiver receiver : receivers)
            {
                coverageArea.add(receiver.getRadioReceiverRange());
            }
        }
    }
}
