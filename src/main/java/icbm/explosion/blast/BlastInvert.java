package icbm.explosion.blast;

import icbm.api.explosion.TriggerCause;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import resonant.lib.transform.vector.Vector3;
import resonant.lib.world.Vector3Change;

import java.util.Collection;
import java.util.List;

/**
 * Created by robert on 12/1/2014.
 */
public class BlastInvert extends BlastBasic
{
    public BlastInvert(){}

    public BlastInvert(World world, int x, int y, int z, int size)
    {
        super(world, x, y, z, size);
    }

    @Override
    protected void effectBlock(Vector3Change vec, List<Vector3Change> list, ForgeDirection side)
    {
        if(!list.contains(vec) && vec.distance(this) < radius)
        {
            list.add(vec);
        }
    }

    protected Vector3Change changeBlockTo(Vector3Change change)
    {
        change.block_$eq(Blocks.glass);
        return change;
    }
}
