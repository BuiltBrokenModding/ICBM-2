package com.builtbroken.icbm.content.blast.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Created by robert on 12/25/2014.
 */
public class BlastEntitySpawn extends BlastSpawn
{
    int entityId;

    public BlastEntitySpawn(int id)
    {
        this.entityId = id;
    }

    @Override
    protected Entity getNewEntity(World world, Random random, int count)
    {
        return EntityList.createEntityByID(entityId, world);
    }
}
