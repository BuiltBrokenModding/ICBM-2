package icbm.explosion.blast;

import net.minecraft.util.EnumFacing;
import resonant.lib.transform.vector.Vector3;
import resonant.lib.world.BlockEdit;

import java.util.HashMap;

/**
 * Created by robert on 12/3/2014.
 */
public class BlastSided extends BlastInvert
{
    EnumFacing facing = EnumFacing.NORTH;

    @Override
    protected void triggerPathFinder(HashMap<BlockEdit, Float> map, BlockEdit vec, float energy)
    {
        //Start pathfinder
        expand(map, vec, energy, facing, 0);
    }

    public EnumFacing getDirection()
    {
        return facing;
    }

    public BlastSided setDirection(EnumFacing direction)
    {
        facing = direction;
        return this;
    }
}
