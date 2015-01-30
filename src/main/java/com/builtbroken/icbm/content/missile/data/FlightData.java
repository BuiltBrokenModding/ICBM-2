package com.builtbroken.icbm.content.missile.data;

import com.builtbroken.icbm.content.missile.EntityMissile;
import net.minecraft.util.MathHelper;

/**
 * Created by robert on 1/22/2015.
 */
public class FlightData
{
    protected EntityMissile missile;

    protected double distance_x = -1;
    protected double distance_y = -1;
    protected double distance_z = -1;
    protected double total_distance = -1;

    protected double motionX;
    protected double motionY;
    protected double motionZ;

    protected long ticks = -1;

    public FlightData(EntityMissile missile)
    {
        this.missile = missile;
    }

    public void updatePath()
    {
        ticks++;
        if(ticks >= Long.MAX_VALUE)
            ticks = 1;

        if(missile.target_pos != null && missile.sourceOfProjectile != null)
        {
            distance_x = missile.target_pos.x() - missile.sourceOfProjectile.x();
            distance_y = missile.target_pos.y() - missile.sourceOfProjectile.y();
            distance_z = missile.target_pos.z() - missile.sourceOfProjectile.z();
            total_distance = missile.sourceOfProjectile.distance(missile.target_pos);
        }
    }


    public void calculateMotionForRotationAndPower(double power)
    {
        motionX = (double)(-MathHelper.sin(missile.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos( missile.rotationPitch / 180.0F * (float)Math.PI));
        motionZ = (double)(MathHelper.cos( missile.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos( missile.rotationPitch / 180.0F * (float)Math.PI));
        motionY = (double)(-MathHelper.sin( missile.rotationPitch / 180.0F * (float)Math.PI));

        motionX *= power;
        motionY *= power;
        motionZ *= power;
    }
}
