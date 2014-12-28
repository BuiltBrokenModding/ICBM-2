package com.builtbroken.icbm.content.blast.entity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import resonant.api.TriggerCause;
import resonant.lib.world.edit.IWorldChangeAction;
import resonant.lib.world.explosive.Blast;
import resonant.lib.world.explosive.Explosive;

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
        return null;
    }
}
