package com.builtbroken.icbm.content.blast.entity;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.content.blast.BlastSpawn;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;
import resonant.engine.ResonantEngine;
import resonant.lib.utility.ReflectionUtility;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
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
