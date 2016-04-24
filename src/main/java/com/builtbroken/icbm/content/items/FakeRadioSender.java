package com.builtbroken.icbm.content.items;

import com.builtbroken.mc.api.map.radio.IRadioWaveReceiver;
import com.builtbroken.mc.api.map.radio.IRadioWaveSender;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.lib.transform.region.Cube;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Wrapper used by items to act as a radio wave sender
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/24/2016.
 */
public class FakeRadioSender implements IRadioWaveSender
{
    public final EntityPlayer player;
    public final ItemStack item;
    Cube cube;

    public FakeRadioSender(EntityPlayer player, ItemStack item, int range)
    {
        this.player = player;
        this.item = item;
        this.cube = new Cube(-range, Math.max(-range, 0), -range, range, range, range).add(player.posX, player.posY, player.posZ);
    }

    @Override
    public void onMessageReceived(IRadioWaveReceiver receiver, float hz, String header, Object[] data)
    {
        if (Engine.runningAsDev)
        {
            //player.addChatComponentMessage(new ChatComponentText("Received message with header " + header + " on band " + hz + "hz with data " + data));
        }
    }

    @Override
    public Cube getRadioSenderRange()
    {
        return cube;
    }

    @Override
    public World world()
    {
        return player.worldObj;
    }

    @Override
    public double x()
    {
        return player.posX;
    }

    @Override
    public double y()
    {
        return player.posY;
    }

    @Override
    public double z()
    {
        return player.posZ;
    }
}
