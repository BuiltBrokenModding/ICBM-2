package com.builtbroken.icbm.content.blast.explosive;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.mc.lib.transform.vector.Location;
import com.builtbroken.mc.lib.world.edit.BlockEdit;
import com.builtbroken.mc.prefab.explosive.blast.BlastSimplePath;
import net.minecraft.init.Blocks;

/**
 * Created by robert on 3/25/2015.
 */
public class BlastAntimatter extends BlastSimplePath
{
    @Override
    public BlockEdit changeBlock(Location location)
    {
        if(location.getBlock() == Blocks.air)
            return null;
        return new BlockEdit(location).set(Blocks.air, 0, false, false);
    }

    @Override
    public boolean shouldPath(Location location)
    {
        if(!ICBM.ANTIMATTER_BREAK_UNBREAKABLE && location.getHardness() < 0)
            return false;
        return super.shouldPath(location);
    }
}
