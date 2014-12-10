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
    @Override
    public float getEnergyCostOfTile(BlockEdit vec, float energy)
    {
       return energy - eUnitPerBlock;
    }

    @Override
    protected BlockEdit onBlockMapped(BlockEdit change, float e, float e2)
    {
        change.block_$eq(ICBM.blockExplosiveMarker);
        change.meta_$eq(change.face() != null ? change.face().ordinal() : 0);
        return change;
    }
}
