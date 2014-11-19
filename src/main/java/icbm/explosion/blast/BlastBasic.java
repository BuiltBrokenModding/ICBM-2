package icbm.explosion.blast;

import icbm.api.explosion.TriggerCause;
import icbm.explosion.Blast;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import resonant.lib.transform.vector.Vector3;

import java.util.Collection;
import java.util.List;

/**
 * Created by robert on 11/19/2014.
 */
public class BlastBasic extends Blast
{
    public BlastBasic(World world, int x, int y, int z, int size)
    {
        super(world, x, y, z, size);
    }

    @Override
    public void getEffectedBlocks(TriggerCause triggerCause, List<Vector3> list)
    {
        for(int y = -size; y <= size; y++)
        {
            for (int x = -size; x <= size; x++)
            {
                for (int z = -size; z <= size; z++)
                {
                    Vector3 vec = new Vector3(this.x + x, this.y + y, this.z + z);
                    float hardness = vec.getHardness(world);
                    if(!vec.isAirBlock(world) && hardness >= 0 && hardness <= 500)
                    {
                        list.add(vec);
                    }
                }
            }
        }
    }

    @Override
    public void doEffectBlocks(Collection<Vector3> blocks, TriggerCause triggerCause)
    {
        for(Vector3 vec : blocks)
        {
            vec.setBlockToAir(world);
        }
    }

    @Override
    public void doEffectOther(World world, double x, double y, double z, TriggerCause triggerCause)
    {

    }
}
