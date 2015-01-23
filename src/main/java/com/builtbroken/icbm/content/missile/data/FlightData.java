package com.builtbroken.icbm.content.missile.data;

import com.builtbroken.icbm.content.missile.EntityMissile;

/**
 * Created by robert on 1/22/2015.
 */
public class FlightData
{
    protected EntityMissile missile;

    protected double distance_x = 0;
    protected double distance_y = 0;
    protected double distance_z = 0;
    protected double total_distance = 0;

    public FlightData(EntityMissile missile)
    {
        this.missile = missile;
    }

    public void updatePath()
    {
        distance_x = missile.target_pos.x() - missile.sourceOfProjectile.x();
        distance_y = missile.target_pos.y() - missile.sourceOfProjectile.y();
        distance_z = missile.target_pos.z() - missile.sourceOfProjectile.z();
        total_distance = missile.sourceOfProjectile.distance(missile.target_pos);
    }
}
