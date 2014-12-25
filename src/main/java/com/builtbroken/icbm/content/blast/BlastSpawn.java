package com.builtbroken.icbm.content.blast;

import com.builtbroken.icbm.api.WorldChangeSpawnEntityEvent;
import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import resonant.lib.transform.vector.Vector3;
import resonant.lib.transform.vector.VectorWorld;
import resonant.lib.world.explosive.Blast;

import java.util.Random;

/** Prefab for spawning entities in mass into the world */
public abstract class BlastSpawn extends Blast
{
    /** Max amount of entities to spawn */
    protected int maxEntities = 1;

    @Override
    public BlastSpawn setYield(int size)
    {
        super.setYield(size);
        this.maxEntities *= size;
        return this;
    }

    @Override
    public void doEffectOther(boolean beforeBlocksPlaced)
    {
        if(!beforeBlocksPlaced)
        {
            Random rand = world.rand;
            for(int i = 0; i < numberOfEntities(); i++)
            {
                EntityLiving ent = getNewEntity(world, rand, i);
                if(ent != null)
                {
                    VectorWorld location = new VectorWorld(this);
                    LivingSpawnEvent.CheckSpawn event = new LivingSpawnEvent.CheckSpawn(ent, location.world(), location.xf(), location.yf(), location.zf());
                    if(event.hasResult() && event.getResult() != Event.Result.DENY)
                    {
                        if(ent instanceof EntityLiving)
                        {
                            ent = (EntityLiving) event.entityLiving;

                            WorldChangeSpawnEntityEvent spawnEvent = new WorldChangeSpawnEntityEvent(ent, this, location);
                            MinecraftForge.EVENT_BUS.post(spawnEvent);
                            if(!spawnEvent.isCanceled())
                            {
                                location.add(rand.nextInt(size) - rand.nextInt(size), size, rand.nextInt(size) - rand.nextInt(size));
                                ent.setLocationAndAngles(location.x(), location.y(), location.z(), 0, 0);
                                spawn(ent);
                            }
                        }
                    }


                }
            }
        }
    }

    /** Adds the entity to the world. Can be
     * used to modify the entity before spawning
     * to introduce a few location based traits.
     *
     * @param entity - entity to spawn
     */
    protected void spawn(EntityLiving entity)
    {
        world.spawnEntityInWorld(entity);
    }

    /** Number of entities to spawn for the current blast */
    protected int numberOfEntities()
    {
        return 1 + world.rand.nextInt(maxEntities);
    }

    /** Entity instance to spawn, Do not set the location */
    protected abstract EntityLiving getNewEntity(World world, Random random, int count);
}
