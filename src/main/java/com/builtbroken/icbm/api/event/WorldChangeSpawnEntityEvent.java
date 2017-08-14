package com.builtbroken.icbm.api.event;

import com.builtbroken.mc.api.edit.IWorldChangeAction;
import com.builtbroken.mc.imp.transform.vector.Location;
import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;

/**
 * Even triggered when spawning an entity from a missile explosion action
 * Created by robert on 12/25/2014.
 */
public class WorldChangeSpawnEntityEvent extends LivingSpawnEvent.SpecialSpawn //TODO move to core
{
    IWorldChangeAction blast;

    public WorldChangeSpawnEntityEvent(EntityLiving entity, IWorldChangeAction blast, Location vec)
    {
        this(entity, blast, vec.oldWorld(), vec.x(), vec.y(), vec.z());
    }

    public WorldChangeSpawnEntityEvent(EntityLiving entity, IWorldChangeAction blast, World world, double x, double y, double z)
    {
        super(entity, world, (float) x, (float) y, (float) z);
        this.blast = blast;
    }
}
