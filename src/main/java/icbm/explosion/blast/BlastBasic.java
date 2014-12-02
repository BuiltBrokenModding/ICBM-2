package icbm.explosion.blast;

import icbm.api.explosion.TriggerCause;
import icbm.explosion.Blast;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import org.apache.logging.log4j.Level;
import resonant.engine.References;
import resonant.lib.transform.vector.IVector3;
import resonant.lib.transform.vector.Vector3;

import java.util.Collection;
import java.util.List;

/**
 * Created by robert on 11/19/2014.
 */
public class BlastBasic extends Blast
{
    protected float[] energy = new float[6];
    protected double radius = 0;

    public BlastBasic(){}

    public BlastBasic(World world, int x, int y, int z, int size)
    {
        super(world, x, y, z, size);
        radius = size * 1.5;
        float e = (float)radius + 1;
        e = e * e * e;

        for(int i = 0; i < 6; i++)
        {
            energy[i] = e / 6;
        }
    }

    @Override
    public void getEffectedBlocks(TriggerCause triggerCause, List<Vector3> list)
    {
        /*
        for(int y = -size; y <= size; y++)
        {
            for (int x = -size; x <= size; x++)
            {
                for (int z = -size; z <= size; z++)
                {
                    Vector3 vec = new Vector3(this.x + x, this.y + y, this.z + z);
                    Block block = vec.getBlock(world);
                    if(shouldEffectBlock(vec, block))
                        list.add(vec);
                }
            }
        } */
        list.add(new Vector3(x, y, z));
        for(int i = 0; i <= size; i++)
        {
            for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
            {
                if (energy[dir.ordinal()] >= 1)
                {
                    expand(triggerCause, list, dir, i + 1);
                }
            }
        }
    }

    protected void expand(TriggerCause triggerCause, List<Vector3> list, ForgeDirection side, int d)
    {
        for(int a = -d; a <= d; a++)
        {
            for(int b = -d; b <= d; b++)
            {
                References.LOGGER.log(Level.INFO,"F:" + side + " A:" + a +"  B:" + b +" D:" +d);
                Vector3 v = null;
                switch (side)
                {
                    case NORTH:
                        v = new Vector3(x + a, y + b, z - d);
                        break;
                    case SOUTH:
                        v = new Vector3(x + a, y + b, z + d);
                        break;
                    case EAST:
                        v = new Vector3(x + d, y + b, z + a);
                        break;
                    case WEST:
                        v = new Vector3(x - d, y + b, z + a);
                        break;
                    case DOWN:
                        v = new Vector3(x + a, y - d, z + b);
                        break;
                    case UP:
                        v = new Vector3(x + a, y + d, z + b);
                        break;
                }
                effectBlock(v, triggerCause, list, side);
            }
        }
    }

    protected void effectBlock(Vector3 vec, TriggerCause triggerCause, List<Vector3> list, ForgeDirection side)
    {
        if(!list.contains(vec) && vec.distance(this) < radius)
        {
            Block block = vec.getBlock(world);
            if (block != null)
            {
                float resistance = block.getExplosionResistance(null, world, vec.xi(), vec.yi(), vec.zi(), x, y, z);
                float hardness = vec.getHardness(world);
                if (!vec.isAirBlock(world) && hardness >= 0 && resistance <= 500)
                {
                    energy[side.ordinal()] = energy[side.ordinal()] - resistance;
                    list.add(vec);
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
