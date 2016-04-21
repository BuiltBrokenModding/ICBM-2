package com.builtbroken.icbm.content.launcher.controller.remote.antenna.wireless;

import com.builtbroken.mc.api.map.radio.IRadioWaveSender;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;

import java.util.ArrayList;
import java.util.Iterator;
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
        if (!senders.contains(sender) && sender.getRadioSenderRange() != null)
        {
            isInvalid = false;
            coverageArea.add(sender.getRadioSenderRange());
            return senders.add(sender);
        }
        return false;
    }

    public boolean removeSender(IRadioWaveSender sender)
    {
        if (senders.contains(sender))
        {
            if (this.senders.size() == 0)
            {
                isInvalid = true;
            }
            return senders.remove(sender);
        }
        return false;
    }

    @Override
    public void doCleanUp()
    {
        super.doCleanUp();
        Iterator<IRadioWaveSender> itRec = senders.iterator();
        while (itRec.hasNext())
        {
            IRadioWaveSender sender = itRec.next();
            if (sender.getRadioSenderRange() == null)
            {
                itRec.remove();
            }
            else if (sender instanceof Entity && !((Entity) sender).isEntityAlive())
            {
                itRec.remove();
            }
            else if (sender instanceof TileEntity && ((TileEntity) sender).isInvalid())
            {
                itRec.remove();
            }
        }
        if (senders.size() == 0)
        {
            isInvalid = true;
        }
        else
        {
            coverageArea.clear();
            for (IRadioWaveSender sender : senders)
            {
                coverageArea.add(sender.getRadioSenderRange());
            }
        }
    }
}
