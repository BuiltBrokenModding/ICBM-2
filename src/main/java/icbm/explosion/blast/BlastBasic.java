package icbm.explosion.blast;

import icbm.ICBM;
import icbm.content.warhead.TileExplosive;
import icbm.explosion.Blast;
import net.minecraft.block.Block;
import net.minecraft.block.BlockTNT;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.Explosion;
import resonant.api.TriggerCause;
import resonant.api.mffs.machine.IForceField;
import resonant.engine.ResonantEngine;
import resonant.lib.transform.sorting.Vector3DistanceComparator;
import resonant.lib.transform.vector.Vector3;
import resonant.lib.utility.DamageUtility;
import resonant.lib.utility.MathUtility;
import resonant.lib.utility.TextUtility;
import resonant.lib.world.edit.BlockEdit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by robert on 11/19/2014.
 */
//TODO use pathfinder for emp to allow for EMP shielding
public class BlastBasic extends Blast
{
    /**
     * DamageSourse to attack entities with
     */
    static DamageSource source = new DamageSource("blast").setExplosion();
    static BlastProfiler profiler = new BlastProfiler();

    /**
     * Energy to start the explosion with
     */
    protected float energy = 0;
    /**
     * Median size of the explosion from center, max size is x2, min size is 0
     */
    protected double radius = 0;

    /**
     * Entity to pass into methods when destroying blocks or attacking entities
     */
    protected Entity explosionBlameEntity;
    /**
     * Explosion wrapper for block methods
     */
    protected Explosion wrapperExplosion;
    /**
     * Blocks to call after all blocks are removed in case they do updates when destroyed
     */
    protected List<BlockEdit> postCallDestroyMethod = new ArrayList();
    /**
     * Profilier for the blast
     */
    protected BlastRunProfile profile;

    public BlastBasic()
    {
        profile = profiler.run(this);
    }


    @Override
    public void getEffectedBlocks(List<BlockEdit> list)
    {
        HashMap<BlockEdit, Float> map = new HashMap();
        profile.startSection("getEffectedBlocks");

        //Start path finder
        profile.startSection("Pathfinder");
        triggerPathFinder(map, new BlockEdit(this.world, this.x, this.y, this.z), energy);
        profile.endSection("Pathfinder");

        //Add map keys to block list
        list.addAll(map.keySet());

        //Sort results so blocks are placed in the center first
        profile.startSection("Sorter");
        Collections.sort(list, new Vector3DistanceComparator(new Vector3(this)));
        profile.endSection("Sorter");

        profile.endSection("getEffectedBlocks");
        //Generate debug info
        if (ResonantEngine.runningAsDev)
        {
            ICBM.LOGGER.info(profile.getOutputSimple());
        }
    }

    /** Called to trigger the blast pathfinder
     * @param map - hash map to store data for block placement to energy used
     * @param vec - starting block
     * @param energy - starting energy
     */
    protected void triggerPathFinder(HashMap<BlockEdit, Float> map, BlockEdit vec, float energy)
    {
        //Start pathfinder
        expand(map, vec, energy, null, 0);
    }

    /** Called to map another iteration to expand outwards from the center of the explosion
     *
     * @param map - hash map to store data for block placement to energy used
     * @param vec - next block to expand from, and to log to map
     * @param energy - current energy at block
     * @param side - side not to expand in, and direction we came from
     * @param iteration - current iteration count from center, use this to stop the iteration if they get too far from center
     */
    protected void expand(HashMap<BlockEdit, Float> map, BlockEdit vec, float energy, EnumFacing side, int iteration)
    {
        long timeStart = System.nanoTime();
        if (iteration < size * 2)
        {
            float e = getEnergyCostOfTile(vec, energy);
            profile.tilesPathed++;
            if (e >= 0)
            {
                //Add block to effect list
                vec.energy_$eq(energy);
                onBlockMapped(vec, e, energy - e);
                map.put(vec, energy - e);

                //Only iterate threw sides if we have more energy
                if (e > 1)
                {
                    //Get valid sides to iterate threw
                    List<BlockEdit> sides = new ArrayList();
                    for (EnumFacing dir : EnumFacing.values())
                    {
                        if (dir != side)
                        {
                            BlockEdit v = new BlockEdit(world, vec.x(), vec.y(), vec.z());
                            v.addEquals(dir);
                            v.face_$eq(dir);
                            v.logPrevBlock();
                            sides.add(v);
                        }
                    }

                    Collections.sort(sides, new Vector3DistanceComparator(new Vector3(this)));

                    profile.blockIterationTimes.add(System.nanoTime() - timeStart);
                    //Iterate threw sides expending energy outwards
                    for (BlockEdit f : sides)
                    {
                        float eToSpend = (e / sides.size()) + (e % sides.size());
                        e -= eToSpend;
                        EnumFacing face = side == null ? getOpposite(f.face()) : side;
                        if (!map.containsKey(f) || map.get(f) < eToSpend)
                        {
                            f.face_$eq(face);
                            expand(map, f, eToSpend, face, iteration + 1);
                        }
                    }
                }
            }
        }
    }

    //TODO move to helper class later, and PR into forge if its not already there
    private EnumFacing getOpposite(EnumFacing face)
    {
        switch (face)
        {
            case UP:
                return EnumFacing.DOWN;
            case DOWN:
                return EnumFacing.UP;
            case NORTH:
                return EnumFacing.SOUTH;
            case SOUTH:
                return EnumFacing.NORTH;
            case EAST:
                return EnumFacing.WEST;
            case WEST:
                return EnumFacing.EAST;
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
    protected float getEnergyCostOfTile(BlockEdit vec, float energy)
    {
        //Update debug info
        if (vec.isAirBlock(world))
            profile.airBlocksPathed++;
        else
            profile.blocksRemoved++;
        //Get cost
        return (vec.getHardness() >= 0 ? energy - (float) Math.max(vec.getResistance(explosionBlameEntity, x, y, z), 0.5) : -1);

    }

    @Override
    public void handleBlockPlacement(BlockEdit vec)
    {
        Block block = vec.getBlock();
        TileEntity tile = vec.getTileEntity();

        //MFFS support
        if (tile instanceof IForceField)
        {
            ((IForceField) tile).weakenForceField((int) vec.energy() * 100);
        }
        else
        {
            if (vec.block() == Blocks.air || vec.block() == Blocks.fire)
            {
                //TODO add energy value of explosion to this explosion if it is small
                //TODO maybe trigger explosion inside this thread allowing for controlled over lap
                //TODO if we trigger the explosive move most of the energy in the same direction
                //the current explosion is running in with a little bit in the opposite direction

                //Trigger break event so blocks can do X action
                if (!(block instanceof BlockTNT) && !(vec.getTileEntity() instanceof TileExplosive))
                {
                    block.onBlockDestroyedByExplosion(world, vec.xi(), vec.yi(), vec.zi(), wrapperExplosion);
                }
                else
                {
                    //Add explosives to post call to allow the thread to finish before generating more explosions
                    postCallDestroyMethod.add(vec);
                }
            }

            vec.place();
        }
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
    protected BlockEdit onBlockMapped(BlockEdit change, float energyExpended, float energyLeft)
    {
        if (energyExpended > energyLeft)
            change.doItemDrop_$eq(true);
        return change;
    }

    @Override
    public void doEffectOther(boolean beforeBlocksPlaced)
    {
        if (!beforeBlocksPlaced)
        {
            //TODO wright own version of getEntitiesWithinAABB that takes a filter and cuboid(or Vector3 to Vector3)
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
        if (DamageUtility.canDamage(entity))
        {
            Vector3 eVec = new Vector3(entity);
            MovingObjectPosition hit = eVec.rayTrace(world, new Vector3(this));
            if (hit == null || hit.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK)
            {
                float e = ((float) radius + 1 * 5) / (float) eVec.distance(this);

                entity.attackEntityFrom(source, e);
                applyMotion(entity, eVec, e);
            }
        }
    }

    protected void applyMotion(Entity entity, Vector3 eVec, float energyAppliedNearEntity)
    {
        if (!entity.isRiding())
        {
            Vector3 motion = eVec.toEulerAngle(new Vector3(this)).toVector().multiply(energyAppliedNearEntity);
            entity.motionX += motion.xi() & 1;
            entity.motionY += motion.xi() & 1;
            entity.motionZ += motion.xi() & 1;
        }
    }

    @Override
    public Blast setCause(TriggerCause cause)
    {
        super.setCause(cause);
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
        return this;
    }


    @Override
    public BlastBasic setYield(int size)
    {
        super.setYield(size);
        //Most of the time radius equals size of the explosion
        radius = size;
        calcStartingEnergy();
        return this;
    }

    /**
     * Calculates the starting energy based on the size of the explosion
     */
    protected void calcStartingEnergy()
    {
        energy = (float) (MathUtility.getSphereVolume(radius) * eUnitPerBlock);
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
}
