package icbm.explosion.blast;

import icbm.ICBM;
import net.minecraft.world.World;
import resonant.lib.transform.vector.Vector3;
import resonant.lib.world.edit.BlockEdit;

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
    public float getEnergyCostOfTile(BlockEdit vec, float energy)
    {
       return energy - 1;
    }

    @Override
    protected BlockEdit onBlockMapped(BlockEdit change, float e, float e2)
    {
        change.block_$eq(ICBM.blockExplosiveMarker);
        change.meta_$eq(change.face() != null ? change.face().ordinal() : 0);
        return change;
    }
}
