package com.builtbroken.icbm.content.blast.explosive;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.mc.lib.transform.vector.Location;
import com.builtbroken.mc.lib.world.edit.BlockEdit;
import com.builtbroken.mc.prefab.explosive.blast.BlastSimplePath;
import net.minecraft.init.Blocks;

/**
 * Blast that will destroy everything ignoring resistance to explosives
 * as Antimatter + matter(everything) = nothing(energy but no objects). In
 * other words it doesn't actually destory the blocks using a normal blast. Instead
 * it simple null out the block at the location.
 * Created by robert on 3/25/2015.
 */
public class BlastAntimatter extends BlastSimplePath
{
    //TODO add delay secondary blast trigger that uses energy released to destroy more blocks

    @Override
    public BlockEdit changeBlock(Location location)
    {
        if (location.getBlock() == Blocks.air)
            return null;
        return new BlockEdit(location).set(Blocks.air, 0, false, false);
    }

    @Override
    public boolean shouldPath(Location location)
    {
        if (!ICBM.ANTIMATTER_BREAK_UNBREAKABLE && location.getHardness() < 0)
            return false;
        return super.shouldPath(location);
    }
}
