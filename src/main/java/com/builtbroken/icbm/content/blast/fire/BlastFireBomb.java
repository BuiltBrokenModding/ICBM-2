package com.builtbroken.icbm.content.blast.fire;

import com.builtbroken.mc.lib.transform.vector.Location;
import com.builtbroken.mc.lib.world.edit.BlockEdit;
import com.builtbroken.mc.prefab.explosive.blast.BlastSimplePath;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Blast that creates a very small area of fire at the target.
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/6/2015.
 */
public class BlastFireBomb extends BlastSimplePath
{
    @Override
    public BlockEdit changeBlock(Location location)
    {
        //TODO spawn random fire particle that can set fire to blocks up to 20 away
        if (location.isAirBlock())
        {
            Location loc = location.add(0, -1, 0);
            if (!loc.isAirBlock() && loc.isSideSolid(ForgeDirection.UP))
            {
                BlockEdit edit = new BlockEdit(location);
                edit.set(Blocks.fire, 0, false, true);
                return edit;
            }
        }
        return null;
    }

    @Override
    public boolean shouldPathTo(Location last, Location next)
    {
        if (super.shouldPathTo(last, next))
        {
            if (last.isAirBlock() && next.isAirBlock())
                return last.sub(next).toForgeDirection() != ForgeDirection.UP;
            return true;
        }
        return false;
    }
}
