package icbm.explosion.blast;

import icbm.api.explosion.TriggerCause;
import icbm.explosion.Blast;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import resonant.lib.transform.vector.Vector3;
import resonant.lib.utility.DamageUtility;
import resonant.lib.world.Vector3Change;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by robert on 11/19/2014.
 */
public class BlastBasic extends Blast
{
    static DamageSource source = new DamageSource("blast").setExplosion();
    protected float energy = 0;
    protected double radius = 0;

    protected List<Vector3> pathedBlocks = new ArrayList<Vector3>();
    protected Entity explosionBlameEntity;
    protected Explosion wrapperExplosion;

    public BlastBasic()
    {
    }

    public BlastBasic(World world, int x, int y, int z, int size)
    {
        super(world, x, y, z, size);
    }

    @Override
    public BlastBasic setYield(int size)
    {
        super.setYield(size);
        radius = size;
        float e = (size * 2 + 1) * eUnitPerBlock;
        energy = e * e * e;

        return this;
    }

    @Override
    public void getEffectedBlocks(List<Vector3Change> list)
    {
        //Create entity to check for blast resistance values on blocks
        if (cause instanceof TriggerCause.TriggerCauseEntity)
        {
            explosionBlameEntity = ((TriggerCause.TriggerCauseEntity) cause).source;
        }
        if (explosionBlameEntity == null)
        {
            explosionBlameEntity = new EntityTNTPrimed(world);
            explosionBlameEntity.setPosition(x, y, z);
        }
        wrapperExplosion = new WrapperExplosion(this);

        System.out.println("===== Starting explosive pathfinder =====");
        //Start pathfinder
        expand(list, new Vector3(this), energy, null, 0);
        System.out.println("=====  Ending explosive pathfinder  =====\n");
    }

    protected void expand(List<Vector3Change> list, Vector3 vec, float energy, EnumFacing side, int iteration)
    {
        String s = iteration + "\t";
        for (int i = 0; i < iteration; i++)
        {
            s += "  ";
        }
        s += "Expanding to location " + vec + " with " + energy + " energy from " + side + " side";
        System.out.println(s);
        float e = effectBlock(vec, energy);
        if (e >= 0)
        {
            //Add block to effect list
            Vector3Change c = new Vector3Change(vec.x(), vec.y(), vec.z());
            changeBlockTo(c, energy - e);
            list.add(c);

            //Only iterate threw sides if we have more energy
            if (e > 1)
            {
                //Get valid sides to iterate threw
                List<FacingVector> sides = new ArrayList();
                for (EnumFacing dir : EnumFacing.values())
                {
                    FacingVector v = new FacingVector(vec.x() + dir.getFrontOffsetX(), vec.y() + dir.getFrontOffsetY(), vec.z() + dir.getFrontOffsetZ(), dir);
                    if (dir != side)
                    {
                        sides.add(v);
                    }
                }

                Collections.sort(sides, new Vector3ClosestComparator(new Vector3(this)));

                //Iterate threw sides expending energy outwards
                for (FacingVector f : sides)
                {
                    float eToSpend = (e / sides.size()) + (e % sides.size());
                    e -= eToSpend;
                    expand(list, f, eToSpend, getOpposite(f.face), iteration + 1);
                }
            }
        }
    }


    private EnumFacing getOpposite(EnumFacing face)
    {
        switch (face)
        {
            case UP:
                return EnumFacing.DOWN;
            case DOWN:
                return EnumFacing.UP;
            case NORTH:
                return EnumFacing.NORTH;
            case SOUTH:
                return EnumFacing.SOUTH;
            case EAST:
                return EnumFacing.EAST;
            case WEST:
                return EnumFacing.WEST;
            default:
                return null;
        }
    }

    /**
     * Called to see how much energy is lost effecting the block at the location
     *
     * @param vec    - location
     * @param energy - energy to expend on the location
     * @return energy left over after effecting the block
     */
    protected float effectBlock(Vector3 vec, float energy)
    {
        Block block = vec.getBlock(world);
        if (block != null)
        {
            float resistance = block.getExplosionResistance(explosionBlameEntity, world, vec.xi(), vec.yi(), vec.zi(), x, y, z);
            float hardness = vec.getHardness(world);
            if (!vec.isAirBlock(world) && hardness >= 0)
            {
                return energy - resistance;
            }
        }
        return energy - 0.5F;
    }

    @Override
    public void doEffectBlock(Vector3Change vec)
    {
        Block block = vec.getBlock(world);
        if (block != null)
        {
            //If you need to modify the items dropped use the forge events fired by ForgeEventFactory.fireBlockHarvesting
            block.onBlockDestroyedByExplosion(world, vec.xi(), vec.yi(), vec.zi(), wrapperExplosion);
            block.dropBlockAsItem(world, vec.xi(), vec.yi(), vec.zi(), vec.getBlockMetadata(world), 0);
        }
        vec.setBlock(world);
    }

    /**
     * Called to give the blast a chance to override what
     * the block at the location will turn into
     *
     * @param change         - location and placement data
     *                       change.setBlock(Block)
     *                       change.setMeta(meta)
     *                       to update the placement info
     * @param energyExpended - energy expended on the block to change it
     * @return new placement info, never change the location or you will
     * create a duplication issue as the original block will not be removed
     */
    protected Vector3Change changeBlockTo(Vector3Change change, float energyExpended)
    {
        return change;
    }

    @Override
    public void doEffectOther(boolean beforeBlocksPlaced)
    {
        if (!beforeBlocksPlaced)
        {
            AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(x - size - 1, y - size - 1, z - size - 1, x + size + 1, y + size + 1, z + size + 1);
            List list = world.getEntitiesWithinAABB(Entity.class, bounds);
            if (list != null && !list.isEmpty())
            {
                for (Object obj : list)
                {
                    if (obj instanceof Entity)
                    {
                        effectEntity((Entity) obj);
                    }
                }
            }
        }
    }

    /**
     * called to effect the entity
     *
     * @param entity
     */
    protected void effectEntity(Entity entity)
    {
        Vector3 eVec = new Vector3(entity);
        MovingObjectPosition hit = eVec.rayTrace(world, new Vector3(this));
        if (hit == null || hit.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK)
        {
            float e = ((float) radius + 1 * 5) / (float) eVec.distance(this);
            if (DamageUtility.canDamage(entity))
            {
                entity.attackEntityFrom(source, e);
            }
            applyMotion(entity, eVec, e);
        }
    }

    protected void applyMotion(Entity entity, Vector3 eVec, float energyAppliedNearEntity)
    {
        if (!entity.isRiding())
        {
            Vector3 motion = eVec.toEulerAngle(new Vector3(this)).toVector().multiply(energyAppliedNearEntity);
            entity.motionX += motion.xi() % 1;
            entity.motionY += motion.xi() % 1;
            entity.motionZ += motion.xi() % 1;
        }
    }

    public static class WrapperExplosion extends Explosion
    {
        public final BlastBasic blast;

        public WrapperExplosion(BlastBasic blast)
        {
            super(blast.world(), blast.explosionBlameEntity, blast.x(), blast.y(), blast.z(), blast.size);
            this.blast = blast;
        }
    }

    public static class Vector3ClosestComparator implements Comparator<Vector3>
    {
        final Vector3 center;

        public Vector3ClosestComparator(Vector3 center)
        {
            this.center = center;
        }

        @Override
        public int compare(Vector3 o1, Vector3 o2)
        {
            double d = o1.distance(center);
            double d2 = o2.distance(center);
            return d > d2 ? 1 : d == d2 ? 0 : -1;
        }
    }

    public static class FacingVector extends Vector3
    {
        EnumFacing face;

        public FacingVector(double x, double y, double z, EnumFacing face)
        {
            super(x, y, z);
            this.face = face;
        }
    }
}
