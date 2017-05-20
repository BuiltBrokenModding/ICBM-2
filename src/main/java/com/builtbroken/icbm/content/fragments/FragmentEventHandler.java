package com.builtbroken.icbm.content.fragments;

import com.builtbroken.icbm.content.blast.fragment.BlastFragments;
import com.builtbroken.mc.api.event.blast.BlastEventDestroyBlock;
import com.builtbroken.mc.imp.transform.vector.Pos;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.world.chunk.Chunk;

import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/17/2016.
 */
public final class FragmentEventHandler
{
    public static final FragmentEventHandler INSTANCE = new FragmentEventHandler();

    @SubscribeEvent
    public void blockDestroyedEvent(BlastEventDestroyBlock.Post event)
    {
        if (event.destructionType == BlastEventDestroyBlock.DestructionType.FORCE)
        {
            Chunk chunk = event.world.getChunkFromBlockCoords(event.x, event.z);
            if (numberOfFragments(chunk) < 100)
            {
                if (event.blast.getYield() < 10 || event.world.rand.nextFloat() > Math.max(0.9, 0.3 + (event.blast.getYield() / 100))) //TODO improve calculation to scale better with volume
                {
                    final Pos center = new Pos(event.x + 0.5, event.y + 0.5, event.z + 0.5);
                    BlastFragments.spawnFragments(event.world, center, 3, FragmentType.BLOCK, event.block); //TODO correct angle to increase likely hood of airborne fragments
                }
            }
            else
            {
                //todo queue for spawn or sim damage
            }
        }
    }

    protected int numberOfFragments(Chunk chunk)
    {
        int fragements = 0;
        for (List list : chunk.entityLists)
        {
            for (Object object : list)
            {
                if (object instanceof EntityFragment)
                {
                    fragements++;
                }
            }
        }
        return fragements;
    }
}
