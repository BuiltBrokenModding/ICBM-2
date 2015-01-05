package com.builtbroken.icbm.api;

import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import com.builtbroken.lib.transform.vector.VectorWorld;
import com.builtbroken.lib.world.edit.IWorldChangeAction;

/**
 * Created by robert on 12/25/2014.
 */
public class WorldChangeSpawnEntityEvent extends LivingSpawnEvent.SpecialSpawn
{
    IWorldChangeAction blast;

    public WorldChangeSpawnEntityEvent(EntityLiving entity, IWorldChangeAction blast, VectorWorld vec)
    {
        this(entity, blast, vec.world(), vec.x(), vec.y(), vec.z());
    }

    public WorldChangeSpawnEntityEvent(EntityLiving entity, IWorldChangeAction blast, World world, double x, double y, double z)
    {
        super(entity, world, (float)x, (float)y, (float)z);
        this.blast = blast;
    }
}
