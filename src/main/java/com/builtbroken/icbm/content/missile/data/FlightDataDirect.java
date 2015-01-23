package com.builtbroken.icbm.content.missile.data;

import com.builtbroken.icbm.content.missile.EntityMissile;
import net.minecraft.util.MathHelper;

/**
 * Created by robert on 1/22/2015.
 */
public class FlightDataDirect extends FlightData
{
    public FlightDataDirect(EntityMissile missile)
    {
        super(missile);
    }

    @Override
    public void updatePath()
    {
        missile.motionX = (double)(-MathHelper.sin( missile.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos( missile.rotationPitch / 180.0F * (float)Math.PI));
        missile.motionZ = (double)(MathHelper.cos( missile.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos( missile.rotationPitch / 180.0F * (float)Math.PI));
        missile.motionY = (double)(-MathHelper.sin( missile.rotationPitch / 180.0F * (float)Math.PI));
    }
}
