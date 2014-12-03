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
    public float effectBlock(Vector3 vec, float energy)
    {
       return energy - 1;
    }

    @Override
    protected Vector3Change changeBlockTo(Vector3Change change,  float e)
    {
        change.block_$eq(Blocks.glass);
        return change;
    }
}
