package com.builtbroken.icbm.content.blast.util;

import com.builtbroken.mc.lib.transform.vector.Pos;

/**
 * Regens the chunk at the target location
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/11/2015.
 */
public class BlastRegenLocal extends BlastRegen
{
    @Override
    protected boolean shouldRegenLocation(int x, int y, int z)
    {
        return new Pos(this.x, this.y, this.z).distance(x, y, z) <= size;
    }

    @Override
    public void doEffectOther(boolean beforeBlocksPlaced)
    {
        //TODO support chunk generators using block set event to capture changed blocks
    }
}
