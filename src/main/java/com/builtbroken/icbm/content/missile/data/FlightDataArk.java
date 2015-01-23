package com.builtbroken.icbm.content.missile.data;

import com.builtbroken.icbm.content.missile.EntityMissile;
import com.builtbroken.mc.lib.transform.rotation.EulerAngle;
import com.builtbroken.mc.lib.transform.vector.Pos;

/**
 * Created by robert on 1/22/2015.
 */
public class FlightDataArk extends FlightData
{
    double start_distance = 0;
    boolean down = false;
    boolean doOnce = false;

    public FlightDataArk(EntityMissile missile)
    {
        super(missile);
    }

    @Override
    public void updatePath()
    {
        super.updatePath();
        if(ticks == 0)
        {
            start_distance = total_distance;
        }


        if(missile.target_pos != null && !down && (missile.posY >= 1000 || total_distance <= (start_distance / 2)))
        {
            down = true;
            //Start to ark down
            EulerAngle angle = new Pos(missile).toEulerAngle(missile.target_pos);
            missile.rotationYaw = (float)angle.yaw();
            missile.rotationPitch = (float)angle.pitch();
            doOnce = false;
        }
        if(!doOnce)
        {
            doOnce = true;
            setMotionToRotation(1);
        }
    }
}
