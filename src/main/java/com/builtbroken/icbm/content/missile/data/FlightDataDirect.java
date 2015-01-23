package com.builtbroken.icbm.content.missile.data;

import com.builtbroken.icbm.content.missile.EntityMissile;
import net.minecraft.util.MathHelper;

/**
 * Created by robert on 1/22/2015.
 */
public class FlightDataDirect extends FlightData
{
    boolean doOnce = false;
    public FlightDataDirect(EntityMissile missile)
    {
        super(missile);
    }

    @Override
    public void updatePath()
    {
        if(!doOnce)
        {
            doOnce = true;
            setMotionToRotation(1);
        }
    }
}
