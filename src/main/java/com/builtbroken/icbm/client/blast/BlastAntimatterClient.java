package com.builtbroken.icbm.client.blast;

import com.builtbroken.icbm.content.blast.explosive.BlastAntimatter;
import com.builtbroken.mc.api.explosive.IExplosiveHandler;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.lib.render.fx.FXSmoke;
import com.builtbroken.mc.lib.transform.rotation.EulerAngle;
import com.builtbroken.mc.lib.transform.vector.Pos;
import net.minecraft.client.Minecraft;

import java.awt.*;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/15/2016.
 */
public class BlastAntimatterClient extends BlastAntimatter
{
    public BlastAntimatterClient(IExplosiveHandler handler)
    {
        super(handler);
    }

    @Override
    public void doStartDisplay()
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
                    Engine.proxy.spawnParticle("smoke", world, center.x(), center.y(), center.z(), velocity.x(), velocity.y(), velocity.z());
                }
            }
        }
    }

    @Override
    public void doEndDisplay()
    {
        double d6 = Minecraft.getMinecraft().renderViewEntity.posX - x;
        double d7 = Minecraft.getMinecraft().renderViewEntity.posY - y;
        double d8 = Minecraft.getMinecraft().renderViewEntity.posZ - z;
        double d9 = 20.0D;

        if (!(d6 * d6 + d7 * d7 + d8 * d8 > d9 * d9))
        {
            final Pos center = new Pos(x, y, z);
            int shells = Math.min((int) size, 10);
            for (int shell = 0; shell < shells; shell++)
            {
                final int rotations = Math.min(((shell + 1) * 4), 20);
                final int degreePerAngle = 360 / rotations;
                for (int yaw = 0; yaw < rotations; yaw++)
                {
                    for (int pitch = 0; pitch < rotations; pitch++)
                    {
                        EulerAngle rotation = new EulerAngle(yaw * degreePerAngle, pitch * degreePerAngle);
                        Pos velocity = rotation.toPos().multiply(((float) shells / (float) shells) * 0.8);

                        FXSmoke entity = new FXSmoke(world, center.add(velocity.multiply(1 + (shell / 4))), velocity, Color.RED.darker(), 1, 10, true);
                        entity.noClip = true;
                        Minecraft.getMinecraft().effectRenderer.addEffect(entity);
                    }
                }
            }
        }
    }
}
