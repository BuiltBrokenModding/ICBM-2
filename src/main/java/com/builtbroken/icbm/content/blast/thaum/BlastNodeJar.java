package com.builtbroken.icbm.content.blast.thaum;

import com.builtbroken.mc.api.edit.IWorldEdit;
import com.builtbroken.mc.lib.transform.vector.Location;
import com.builtbroken.mc.prefab.explosive.blast.BlastSimplePath;

/**
 * Captures a node in a jar, very useful for hungry nodes.
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/15/2015.
 */
public class BlastNodeJar extends BlastSimplePath<BlastNodeJar>
{
    boolean found = false;

    public boolean shouldPath(Location location)
    {
        if(super.shouldPath(location))
        {
            return !found;
        }
        return false;
    }

    @Override
    public IWorldEdit changeBlock(Location location)
    {
        if(!found)
        {
            return new BlockEditJar(location);
        }
        return null;
    }
}
