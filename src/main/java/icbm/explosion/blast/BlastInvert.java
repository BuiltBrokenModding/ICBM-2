package icbm.explosion.blast;

import icbm.api.explosion.TriggerCause;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import resonant.lib.transform.vector.Vector3;

import java.util.Collection;
import java.util.List;

/**
 * Created by robert on 12/1/2014.
 */
public class BlastInvert extends BlastBasic
{
    public BlastInvert(World world, int x, int y, int z, int size)
    {
        super(world, x, y, z, size);
    }

    protected void effectBlock(Vector3 vec, TriggerCause triggerCause, List<Vector3> list, ForgeDirection side)
    {
        if(!list.contains(vec) && vec.distance(this) < radius)
        {
            list.add(vec);
        }
    }
    @Override
    public void doEffectBlocks(Collection<Vector3> blocks, TriggerCause triggerCause)
    {
        for(Vector3 vec : blocks)
        {
            vec.setBlock(world, Blocks.glass);
        }
    }
}
