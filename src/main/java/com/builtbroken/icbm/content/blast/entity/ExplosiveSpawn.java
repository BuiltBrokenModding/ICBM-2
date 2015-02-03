package com.builtbroken.icbm.content.blast.entity;

import com.builtbroken.mc.api.event.TriggerCause;
import com.builtbroken.mc.lib.world.edit.IWorldChangeAction;
import com.builtbroken.mc.prefab.explosive.Explosive;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * Created by robert on 12/25/2014.
 */
public class ExplosiveSpawn extends Explosive
{
    public ExplosiveSpawn()
    {
        super("Spawn", null);
    }

    @Override
    public IWorldChangeAction createBlastForTrigger(World world, double x, double y, double z, TriggerCause triggerCause, double yieldMultiplier, NBTTagCompound tag)
    {
        if(tag != null)
        {
            int entityID = tag.getInteger("EntityID");
            return new BlastEntitySpawn(entityID);
        }
        return null;
    }
}
