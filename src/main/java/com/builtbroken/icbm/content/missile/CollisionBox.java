package com.builtbroken.icbm.content.missile;

import com.builtbroken.mc.lib.transform.region.Cuboid;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;

/** A non-axis aligned version of the bounding box that allows for rotation
 * Created by robert on 12/13/2014.
 */
public class CollisionBox extends AxisAlignedBB
{
    protected Cuboid originalSize;
    protected CollisionBox(double x, double y, double z, double i, double j, double k)
    {
        super(x, y, z, i, j, k);
        this.originalSize = new Cuboid(x, y, z, i, j, k);
    }

    @Override
    public boolean intersectsWith(AxisAlignedBB box)
    {
        return super.intersectsWith(box);
    }

    @Override
    public boolean isVecInside(Vec3 v)
    {
        return super.isVecInside(v);
    }
}
