package com.builtbroken.icbm.content.blast.fragment;

import com.builtbroken.mc.api.edit.IWorldEdit;
import com.builtbroken.mc.api.explosive.IExplosiveHandler;
import com.builtbroken.mc.lib.helper.MathUtility;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.prefab.explosive.blast.Blast;
import net.minecraft.entity.projectile.EntityArrow;

/**
 * Created by robert on 2/25/2015.
 */
public class BlastArrows extends Blast<BlastArrows>
{
    public BlastArrows(IExplosiveHandler handler)
    {
        super(handler);
    }

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

                    //Motion
                    arrow.motionX = pos.x();
                    arrow.motionY = pos.y();
                    arrow.motionZ = pos.z();

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

    @Override
    public void displayEffectForEdit(IWorldEdit blocks)
    {

    }

    @Override
    public void playAudioForEdit(IWorldEdit blocks)
    {

    }
}
