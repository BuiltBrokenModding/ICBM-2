package icbm.explosion.missile.modular;

import icbm.api.ITier;
import calclavia.lib.multiblock.IMultiBlock;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.tile.TileEntityAdvanced;

public class TileEntityMissileTable extends TileEntityAdvanced implements IMultiBlock, ITier
{
    public int tier = -1;

    @Override
    public Vector3[] getMultiBlockVectors()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getTier()
    {
        if (tier == -1)
        {
            this.setTier(this.worldObj.getBlockMetadata(xCoord, yCoord, zCoord));
        }
        return tier;
    }

    @Override
    public void setTier(int tier)
    {
        this.tier = tier & 3;
    }

}
