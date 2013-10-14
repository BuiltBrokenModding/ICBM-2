package icbm.explosion.missile.modular;

import net.minecraftforge.common.ForgeDirection;
import icbm.api.ITier;
import calclavia.lib.multiblock.IMultiBlock;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.tile.IRotatable;
import universalelectricity.prefab.tile.TileEntityAdvanced;

public class TileEntityMissileTable extends TileEntityAdvanced implements IMultiBlock, ITier, IRotatable
{
    public int tier = -1;
    /** Side placed on */
    ForgeDirection placedSide = ForgeDirection.UP;
    /** 0 - 3 of rotation on the given side */
    byte rotationSide = 0;

    @Override
    public Vector3[] getMultiBlockVectors()
    {
        //rotation doesn't really effect the multi block too much however placed side does
        if (placedSide == ForgeDirection.UP || placedSide == ForgeDirection.DOWN)
        {
            //line up on the x
            if (rotationSide == 0 || rotationSide == 2)
            {
                return new Vector3[] { new Vector3(1, 0, 0), new Vector3(-1, 0, 0) };
            }
            //lined up on the z
            return new Vector3[] { new Vector3(0, 0, 1), new Vector3(0, 0, -1) };
        }
        else
        {
            //Lined up with x or z
            if (rotationSide == 0 || rotationSide == 2)
            {
                if (placedSide == ForgeDirection.NORTH || placedSide == ForgeDirection.EAST)
                {
                    return new Vector3[] { new Vector3(-1, 0, 0), new Vector3(1, 0, 0) };
                }
                else if (placedSide == ForgeDirection.SOUTH || placedSide == ForgeDirection.WEST)
                {
                    return new Vector3[] { new Vector3(0, 0, -1), new Vector3(0, 0, 1) };
                }
            }
            //Lined up with the Y
            return new Vector3[] { new Vector3(0, 1, 0), new Vector3(0, -1, 0) };
        }
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

    @Override
    public ForgeDirection getDirection()
    {
        return null;
    }

    @Override
    public void setDirection(ForgeDirection direection)
    {
        // TODO Auto-generated method stub

    }

}
