package com.builtbroken.icbm.content.blast.thaum;

import com.builtbroken.mc.lib.transform.vector.Location;
import com.builtbroken.mc.lib.world.edit.BlockEdit;
import com.builtbroken.mc.prefab.explosive.blast.BlastSimplePath;

/**
 * Captures a node in a jar, very useful for hungry nodes.
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/15/2015.
 */
public class BlastNodeJar extends BlastSimplePath
{
    @Override
    public BlockEdit changeBlock(Location location)
    {
        return null;
    }
}
