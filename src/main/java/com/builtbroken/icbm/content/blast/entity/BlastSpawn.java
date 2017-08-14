package com.builtbroken.icbm.content.blast.entity;

import com.builtbroken.icbm.api.event.WorldChangeSpawnEntityEvent;
import com.builtbroken.mc.api.explosive.IExplosiveHandler;
import com.builtbroken.mc.imp.transform.vector.Location;
import com.builtbroken.mc.framework.explosive.blast.Blast;
import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;

import java.util.Random;

/**
 * Prefab for spawning entities in mass into the world
 */
public abstract class BlastSpawn extends Blast
{

    public BlastSpawn(IExplosiveHandler handler)
    {
        super(handler);
    }

    @Override
    public BlastSpawn setYield(double size)
    {
        super.setYield(size);
        return this;
    }

    @Override
    public void doEffectOther(boolean beforeBlocksPlaced)
    {
        if (!beforeBlocksPlaced)
        {
            Random rand = oldWorld.rand;
            for (int i = 0; i < numberOfEntities(); i++)
            {
                Entity ent = getNewEntity(oldWorld, rand, i);
                if (ent != null)
                {
                    Location location = new Location(this);
                    boolean spawn = true;
                    if (ent instanceof EntityLiving)
                    {
                        LivingSpawnEvent.CheckSpawn event = new LivingSpawnEvent.CheckSpawn((EntityLiving) ent, location.oldWorld(), location.xf(), location.yf(), location.zf());
                        spawn = event.hasResult() && event.getResult() != Event.Result.DENY;
                        ent = event.entity;
                    }
                    if (spawn)
                    {
                        if (ent instanceof EntityLiving)
                        {

                            WorldChangeSpawnEntityEvent spawnEvent = new WorldChangeSpawnEntityEvent((EntityLiving) ent, this, location);
                            MinecraftForge.EVENT_BUS.post(spawnEvent);
                            spawn = !spawnEvent.isCanceled();
                        }
                        if (spawn)
                        {
                            location = location.add(rand.nextInt((int)size) - rand.nextInt((int)size), rand.nextInt((int)size) + 1, rand.nextInt((int)size) - rand.nextInt((int)size));
                            ent.setLocationAndAngles(location.x(), location.y(), location.z(), MathHelper.wrapAngleTo180_float(rand.nextFloat() * 360.0F), 0.0F);
                            if (ent instanceof EntityLivingBase)
                            {
                                ((EntityLivingBase) ent).rotationYawHead = ent.rotationYaw;
                                ((EntityLivingBase) ent).renderYawOffset = ent.rotationYaw;
                                if (ent instanceof EntityLiving)
                                    ((EntityLiving) ent).onSpawnWithEgg(null);
                            }
                            spawn(ent);
                        }
                    }
                }
            }
        }
    }


    /**
     * Adds the entity to the world. Can be
     * used to modify the entity before spawning
     * to introduce a few location based traits.
     *
     * @param entity - entity to spawn
     */
    protected void spawn(Entity entity)
    {
        oldWorld.spawnEntityInWorld(entity);
        if (entity instanceof EntityLiving)
            ((EntityLiving) entity).playLivingSound();
    }

    /**
     * Number of entities to spawn for the current blast
     */
    protected int numberOfEntities()
    {
        return (int) size;
    }

    /**
     * Entity instance to spawn, Do not set the location
     */
    protected abstract Entity getNewEntity(World world, Random random, int count);
}
