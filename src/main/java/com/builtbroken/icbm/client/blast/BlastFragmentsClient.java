package com.builtbroken.icbm.client.blast;

import com.builtbroken.icbm.content.blast.fragment.BlastFragments;
import com.builtbroken.icbm.content.blast.fragment.FragBlastType;
import com.builtbroken.jlib.helpers.MathHelper;
import com.builtbroken.mc.api.explosive.IExplosiveHandler;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.imp.transform.rotation.EulerAngle;
import com.builtbroken.mc.imp.transform.vector.Pos;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/17/2016.
 */
public class BlastFragmentsClient extends BlastFragments
{
    public BlastFragmentsClient(IExplosiveHandler handler, FragBlastType type)
    {
        super(handler, type);
    }

    @Override
    public void doStartDisplay()
    {
        if (this.size >= 2.0F)
        {
            Engine.minecraft.spawnParticle("hugeexplosion", world, x, y, z, 1.0D, 0.0D, 0.0D);
        }
        else
        {
            Engine.minecraft.spawnParticle("largeexplode", world, x, y, z, 1.0D, 0.0D, 0.0D);
        }

    }

    @Override
    public void doEndDisplay()
    {
        final Pos center = new Pos(x, y, z);
        int shells = Math.min((int) size, 10);
        for (int shell = 0; shell < shells; shell++)
        {
            final int rotations = Math.min(((shell + 1) * 4), 60);
            final int degreePerAngle = 360 / rotations;
            for (int yaw = 0; yaw < rotations; yaw++)
            {
                for (int pitch = 0; pitch < rotations; pitch++)
                {
                    EulerAngle rotation = new EulerAngle(yaw * degreePerAngle, pitch * degreePerAngle);
                    Pos velocity = rotation.toPos().multiply(((float) shells / (float) shells) * 1);
                    if (MathHelper.rand.nextBoolean())
                    {
                        Engine.minecraft.spawnParticle("flame", world, center.x(), center.y(), center.z(), velocity.x(), velocity.y(), velocity.z());
                    }
                    else
                    {
                        Engine.minecraft.spawnParticle("smoke", world, center.x(), center.y(), center.z(), velocity.x(), velocity.y(), velocity.z());
                    }
                }
            }
        }
    }
}
