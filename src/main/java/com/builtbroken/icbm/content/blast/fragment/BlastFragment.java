package com.builtbroken.icbm.content.blast.fragment;

import com.builtbroken.mc.lib.helper.MathUtility;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.prefab.explosive.blast.Blast;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;

/**
 * Created by robert on 2/25/2015.
 */
public class BlastFragment extends Blast
{
    @Override
    public void doEffectOther(boolean beforeBlocksPlaced)
    {
        if (!beforeBlocksPlaced)
        {
            for (int i = 0; i < 10 * size; i++)
            {
                Pos pos = new Pos(x(), y(), z()).addRandom(MathUtility.rand, 2);
                if(pos.isAirBlock(world()))
                {
                    EntityArrow arrow = new EntityArrow(world);
                    arrow.setPosition(pos.x(), pos.y(), pos.z());
                    pos = new Pos().addRandom(MathUtility.rand, 2);
                    arrow.setVelocity(pos.x(), pos.y(), pos.z());
                    world.spawnEntityInWorld(arrow);
                }
            }
        }
    }

    @Override
    public int shouldThreadAction()
    {
        return -1;
    }
}
