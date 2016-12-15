package com.builtbroken.icbm.content.blast.entity;

import com.builtbroken.mc.api.explosive.IExplosiveHandler;
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

    public BlastEntitySpawn(IExplosiveHandler handler, int id)
    {
        super(handler);
        this.entityId = id;
    }

    @Override
    protected Entity getNewEntity(World world, Random random, int count)
    {
        return EntityList.createEntityByID(entityId, world);
    }
}
