package icbm.explosion.missile.modular;

import icbm.core.base.BlockICBM;
import universalelectricity.core.UniversalElectricity;

/** Multi-block table use to hold a missile prototype while the player is working on the design. 3x1x1 in size
 *
 * @author DarkGuardsman */
public class BlockMissileTable extends BlockICBM
{
    public BlockMissileTable(int id)
    {
        super(id, "MissileTable", UniversalElectricity.machine);
    }
}
