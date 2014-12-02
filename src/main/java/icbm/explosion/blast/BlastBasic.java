package icbm.explosion.blast;

import icbm.api.explosion.TriggerCause;
import icbm.explosion.Blast;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import org.apache.logging.log4j.Level;
import resonant.engine.References;
import resonant.lib.transform.rotation.EulerAngle;
import resonant.lib.transform.vector.IVector3;
import resonant.lib.transform.vector.Vector3;
import resonant.lib.world.Vector3Change;

import java.util.Collection;
import java.util.List;

/**
 * Created by robert on 11/19/2014.
 */
public class BlastBasic extends Blast
{
    static DamageSource source = new DamageSource("blast").setExplosion();
    protected float[] energy = new float[6];
    protected float[][][] energyMap;
    protected double radius = 0;

    public BlastBasic(){}

    public BlastBasic(World world, int x, int y, int z, int size)
    {
        super(world, x, y, z, size);
    }

    @Override
    public BlastBasic setYield(int size)
    {
        super.setYield(size);
        radius = size * 1.5;
        float e = (float)radius + 1;
        e = e * e * e;

        for(int i = 0; i < 6; i++)
        {
            energy[i] = e / 6;
        }

        energyMap = new float[size][size][size];
        return this;
    }

    @Override
    public void getEffectedBlocks(List<Vector3Change> list)
    {
        list.add(new Vector3Change(x, y, z));
        for(int i = 0; i <= size; i++)
        {
            for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
            {
                if (energy[dir.ordinal()] >= 1)
                {
                    expand(list, dir, i + 1);
                }
            }
        }
    }

    protected void expand(List<Vector3Change> list, ForgeDirection side, int d)
    {
        for(int a = -d; a <= d; a++)
        {
            for(int b = -d; b <= d; b++)
            {
                Vector3Change v = new Vector3Change(x, y, z);
                switch (side)
                {
                    case NORTH:
                        v.addEquals(x + a, y + b, z - d);
                        break;
                    case SOUTH:
                        v.addEquals(x + a, y + b, z + d);
                        break;
                    case EAST:
                        v.addEquals(x + d, y + b, z + a);
                        break;
                    case WEST:
                        v.addEquals(x - d, y + b, z + a);
                        break;
                    case DOWN:
                        v.addEquals(x + a, y - d, z + b);
                        break;
                    case UP:
                        v.addEquals(x + a, y + d, z + b);
                        break;
                }
                effectBlock(v, list, side);
            }
        }
    }

    protected void effectBlock(Vector3Change vec, List<Vector3Change> list, ForgeDirection side)
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
                    energyMap[vec.xi()][ vec.yi()][ vec.zi()] = energy[side.ordinal()];
                    list.add(vec);
                }
            }
        }
    }

    protected Vector3Change changeBlockTo(Vector3Change change)
    {
        return change;
    }

    @Override
    public void doEffectOther(boolean beforeBlocksPlaced)
    {
        if(!beforeBlocksPlaced)
        {
            AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(x - size - 1, y - size - 1, z - size - 1, x + size + 1, y + size + 1, z + size + 1);
            List list = world.getEntitiesWithinAABB(Entity.class, bounds);
            if (list != null && !list.isEmpty())
            {
                for (Object obj : list)
                {
                    if(obj instanceof Entity)
                    {
                        effectEntity((Entity) obj);
                    }
                }
            }
        }
    }

    /**
     * called to effect the entity
     * @param entity
     */
    protected void effectEntity(Entity entity)
    {
        Vector3 eVec = new Vector3(entity);
        MovingObjectPosition hit = eVec.rayTrace(world, new Vector3(this));
        if(hit == null || hit.typeOfHit  != MovingObjectPosition.MovingObjectType.BLOCK )
        {
            float e = energyMap[eVec.xi()][eVec.yi()][eVec.zi()];
            entity.attackEntityFrom(source, e);
            applyMotion(entity, eVec, e);
        }
    }

    protected void applyMotion(Entity entity, Vector3 eVec, float energyAppliedNearEntity)
    {
        Vector3 motion = eVec.toEulerAngle(new Vector3(this)).toVector().multiply(energyAppliedNearEntity);
        entity.motionX += motion.xi();
        entity.motionY += motion.xi();
        entity.motionZ += motion.xi();
    }
}
