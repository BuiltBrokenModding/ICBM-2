package com.builtbroken.icbm.content.missile.data.flight;

import com.builtbroken.icbm.content.missile.entity.EntityMissile;
import com.builtbroken.jlib.data.vector.IPos3D;
import com.builtbroken.mc.imp.transform.rotation.EulerAngle;
import com.builtbroken.mc.imp.transform.vector.Pos;

/**
 * Created by robert on 1/22/2015.
 */
public class FlightDataArk extends FlightDataDirect
{
    double start_distance = 0;
    double y_max = 0;
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
            y_max = Math.max(1000, total_distance / 2);
        }


        if(missile.target_pos != null && !down && (missile.posY >= y_max || total_distance <= (start_distance / 2)))
        {
            down = true;
            //Start to ark down
            EulerAngle angle = new Pos((IPos3D) missile).toEulerAngle(missile.target_pos);
            missile.rotationYaw = (float)angle.yaw();
            missile.rotationPitch = (float)angle.pitch();
            calculateMotionForRotationAndPower(1);
            doOnce = false;
        }
    }
}
