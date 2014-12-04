package icbm.explosion.blast;

import icbm.api.explosion.TriggerCause;
import icbm.explosion.Blast;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.*;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import resonant.engine.ResonantEngine;
import resonant.lib.prefab.EntityProjectile;
import resonant.lib.transform.sorting.Vector3DistanceComparator;
import resonant.lib.transform.vector.Vector3;
import resonant.lib.utility.DamageUtility;
import resonant.lib.world.Placement;

import java.text.DecimalFormat;
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
    static DamageSource source = new DamageSource("blast").setExplosion();
    static DecimalFormat numFormatter = new DecimalFormat("#,###");

    protected float energy = 0;
    protected double radius = 0;

    protected Entity explosionBlameEntity;
    protected Explosion wrapperExplosion;

    private int tilesPathed = 0;
    private int airBlocksPathed = 0;
    private int blocksRemoved = 0;

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
        double volume = (4 * Math.PI * (radius * radius * radius)) / 3;
        energy = (float) (volume * eUnitPerBlock);

        return this;
    }

    @Override
    public void getEffectedBlocks(List<Placement> list)
    {
        long start = System.nanoTime();
        long t = start;
        StringBuilder stringBuilder = new StringBuilder();
        HashMap<Placement, Float> map = new HashMap();

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


        stringBuilder.append("\n===== Starting explosive calculations =====");
        stringBuilder.append("\nCenter: " + new Vector3(this));
        stringBuilder.append("\nEnergy: " + energy);
        stringBuilder.append("\nSize:   " + radius);
        triggerPathFinder(map, new Vector3(this), energy);
        stringBuilder.append("Path Time:  " + getTimeDifference(t));

        //Add map keys to block list and sort by closest to center
        t = System.nanoTime();
        list.addAll(map.keySet());
        Collections.sort(list, new Vector3DistanceComparator(new Vector3(this)));

        //Generate debug info
        if(ResonantEngine.runningAsDev)
        {
            stringBuilder.append("\nSort Time:  " + getTimeDifference(t));
            stringBuilder.append("\nTotal Time: " + getTimeDifference(start));
            stringBuilder.append("\nChanges:    " + snum(list.size(), 5));
            stringBuilder.append("\nIterations: " + snum(tilesPathed, 5));
            stringBuilder.append("\n\tAir:        " + snum(airBlocksPathed, 5));
            stringBuilder.append("\n\tBlocks:     " + snum(blocksRemoved, 5));
            stringBuilder.append("\n=====  Ending explosive calculations  =====\n");
            System.out.println(stringBuilder);

            if (cause instanceof TriggerCause.TriggerCauseEntity)
            {
                Entity ent = ((TriggerCause.TriggerCauseEntity) cause).source;
                EntityPlayer player = null;
                if (ent instanceof EntityPlayer)
                    player = (EntityPlayer) ent;
                if (ent instanceof EntityProjectile && ((EntityProjectile) ent).firedByEntity instanceof EntityPlayer)
                    player = (EntityPlayer) ((EntityProjectile) ent).firedByEntity;
                if (player != null)
                    player.addChatComponentMessage(new ChatComponentText("Explosive went off in " + getTimeDifference(start)));
            }
        }
    }

    /**
     * Formats a number to fit into so many spaces, and to contain ,
     *
     * @param num    - number to format
     * @param spaces - spaces to pad the front with
     * @return formated string
     */
    protected String snum(long num, int spaces)
    {
        return String.format("%" + spaces + "s", numFormatter.format(num));
    }

    protected void outputTimeDifference(String prefix, long start)
    {
        outputTime(prefix, System.nanoTime() - start);
    }

    protected String getTimeDifference(long start)
    {
        return getTime(System.nanoTime() - start);
    }

    protected void outputTime(String prefix, long nano)
    {
        System.out.println(prefix + " Time: " + getTime(nano));
    }

    protected String getTime(long nano)
    {
        long s = nano / 1000000000;
        long ms = (nano % 1000000000) / 1000000;
        long us = ((nano % 1000000000) % 1000000) / 1000;
        long ns = ((nano % 1000000000) % 1000000) % 1000;
        String string = "";
        string += snum(s, 3) + "s  ";
        string += snum(ms, 3) + "ms  ";
        string += snum(us, 3) + "us  ";
        string += snum(ns, 3) + "ns  ";

        return string;
    }

    protected void triggerPathFinder(HashMap<Placement, Float> map, Vector3 vec, float energy)
    {
        //Start pathfinder
        expand(map, vec, energy, null, 0);
    }

    protected void expand(HashMap<Placement, Float> map, Vector3 vec, float energy, EnumFacing side, int iteration)
    {
        if (iteration < size * 2)
        {
            float e = getEnergyCostOfTile(vec, energy);
            if (e >= 0)
            {
                tilesPathed++;
                //Add block to effect list
                Placement c = new Placement(vec.x(), vec.y(), vec.z());
                onBlockMapped(c, energy - e);
                map.put(c, energy - e);

                //Only iterate threw sides if we have more energy
                if (e > 1)
                {
                    //Get valid sides to iterate threw
                    List<Placement> sides = new ArrayList();
                    for (EnumFacing dir : EnumFacing.values())
                    {
                        if (dir != side)
                        {
                            Placement v = new Placement(vec.x(), vec.y(), vec.z());
                            v.addEquals(dir);
                            v.setFace(dir);
                            sides.add(v);
                        }
                    }

                    Collections.sort(sides, new Vector3DistanceComparator(new Vector3(this)));
                    //Iterate threw sides expending energy outwards
                    for (Placement f : sides)
                    {
                        float eToSpend = (e / sides.size()) + (e % sides.size());
                        e -= eToSpend;
                        EnumFacing face = side == null ? getOpposite(f.getFace()) : side;
                        if (!map.containsKey(f) || map.get(f) < eToSpend)
                        {
                            expand(map, f, eToSpend, face, iteration + 1);
                            f.setFace(face);
                        }
                    }
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
    protected float getEnergyCostOfTile(Vector3 vec, float energy)
    {
        Block block = vec.getBlock(world);
        if (block != null)
        {
            float resistance = block.getExplosionResistance(explosionBlameEntity, world, vec.xi(), vec.yi(), vec.zi(), x, y, z);
            float hardness = vec.getHardness(world);
            if (!vec.isAirBlock(world) && hardness >= 0)
            {
                blocksRemoved++;
                return energy - (float) Math.max(resistance, 0.5);
            }
            else if (!vec.isAirBlock(world))
            {
                airBlocksPathed++;
            }
        }
        return energy - 0.5F;
    }

    @Override
    public void handleBlockPlacement(Placement vec)
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
    protected Placement onBlockMapped(Placement change, float energyExpended)
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
}
