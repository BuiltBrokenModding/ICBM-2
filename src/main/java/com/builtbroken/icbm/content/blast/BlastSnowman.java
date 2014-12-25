package com.builtbroken.icbm.content.blast;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Created by robert on 12/25/2014.
 */
public class BlastSnowman extends BlastSpawn
{
    @Override
    protected EntityLiving getNewEntity(World world, Random random, int count)
    {
        return new EntitySnowman(world);
    }
}
