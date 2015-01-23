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
       setMotionToRotation(1);
    }
}
