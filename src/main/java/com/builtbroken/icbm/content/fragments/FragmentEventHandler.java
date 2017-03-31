package com.builtbroken.icbm.content.fragments;

import com.builtbroken.icbm.content.blast.fragment.BlastFragments;
import com.builtbroken.mc.api.event.blast.BlastEventDestroyBlock;
import com.builtbroken.mc.imp.transform.vector.Pos;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

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
            final Pos center = new Pos(event.x + 0.5, event.y + 0.5, event.z + 0.5);
            BlastFragments.spawnFragments(event.world, center, 3, FragmentType.BLOCK, event.block);
        }
    }
}
