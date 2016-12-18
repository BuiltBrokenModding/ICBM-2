package com.builtbroken.icbm.content.blast.fragment;

import com.builtbroken.mc.api.edit.IWorldEdit;
import com.builtbroken.mc.api.explosive.IExplosiveHandler;
import com.builtbroken.mc.lib.transform.rotation.EulerAngle;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.prefab.explosive.blast.Blast;
import net.minecraft.entity.projectile.EntityArrow;

/**
 * Handles the arrow blast
 * Created by robert on 2/25/2015.
 */
public class BlastArrows extends Blast<BlastArrows>
{
    /** Number of arrows per size */
    public static final int ARROWS = 10;

    public BlastArrows(IExplosiveHandler handler)
    {
        super(handler);
    }

    @Override
    public void doEffectOther(boolean beforeBlocksPlaced)
    {
        if (!beforeBlocksPlaced)
        {
            final Pos center = new Pos((int)x + 0.5, (int)y + 0.5, (int)z + 0.5);
            int rotations = (int) Math.ceil(Math.sqrt(size * 10));
            double degrees = 360 / rotations;

            for (int yaw = 0; yaw < rotations; yaw++)
            {
                for (int pitch = 0; pitch < rotations; pitch++)
                {
                    EulerAngle rotation = new EulerAngle(yaw * degrees + (world.rand.nextFloat() * 2), pitch * degrees + (world.rand.nextFloat() * 2));
                    Pos velocity = rotation.toPos().multiply(1 + world.rand.nextFloat());
                    Pos pos = center.add(rotation.toPos()).addRandom(world.rand, 0.3);
                    if (pos.isAirBlock(world)) //TODO add proper collision check
                    {
                        EntityArrow arrow = new EntityArrow(world);
                        arrow.setPosition(pos.x(), pos.y(), pos.z());

                        //Random chance for fragment to be on fire
                        if (world.rand.nextBoolean())
                        {
                            arrow.setFire(3 + world.rand.nextInt(60));
                        }

                        //Motion
                        arrow.motionX = velocity.x();
                        arrow.motionY = velocity.y();
                        arrow.motionZ = velocity.z();

                        world.spawnEntityInWorld(arrow);
                    }
                }
            }
        }
    }

    @Override
    public int shouldThreadAction()
    {
        return -1;
    }

    @Override
    public void displayEffectForEdit(IWorldEdit blocks)
    {

    }

    @Override
    public void playAudioForEdit(IWorldEdit blocks)
    {

    }
}
