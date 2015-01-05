package com.builtbroken.icbm.content.blast.entity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import com.builtbroken.api.TriggerCause;
import com.builtbroken.lib.world.edit.IWorldChangeAction;
import com.builtbroken.lib.world.explosive.Explosive;

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
    public IWorldChangeAction createBlastForTrigger(World world, double x, double y, double z, TriggerCause triggerCause, int yieldMultiplier, NBTTagCompound tag)
    {
        if(tag != null)
        {
            int entityID = tag.getInteger("EntityID");
            return new BlastEntitySpawn(entityID);
        }
        return null;
    }
}
