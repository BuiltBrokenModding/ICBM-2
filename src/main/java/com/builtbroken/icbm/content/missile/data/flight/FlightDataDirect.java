package com.builtbroken.icbm.content.missile.data.flight;

import com.builtbroken.icbm.content.missile.entity.EntityMissile;

/**
 * Created by robert on 1/22/2015.
 */
public class FlightDataDirect extends FlightData
{
    boolean doOnce = true;
    public FlightDataDirect(EntityMissile missile)
    {
        super(missile);
    }

    @Override
    public void updatePath()
    {
        if(!doOnce)
        {
            doOnce = false;
            calculateMotionForRotationAndPower(1);
        }
        missile.motionX = motionX;
        missile.motionY = motionY;
        missile.motionZ = motionZ;
    }
}
