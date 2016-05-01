package com.builtbroken.icbm.content.blast.entity;

import com.builtbroken.icbm.api.missile.IMissileEntity;
import com.builtbroken.icbm.api.modules.IMissile;
import com.builtbroken.icbm.api.modules.IWarhead;
import com.builtbroken.icbm.content.blast.ExplosiveHandlerICBM;
import com.builtbroken.mc.api.edit.IWorldChangeAction;
import com.builtbroken.mc.api.event.TriggerCause;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * Created by robert on 12/25/2014.
 */
public class ExplosiveHandlerSpawn extends ExplosiveHandlerICBM<BlastSpawn>
{
    public ExplosiveHandlerSpawn()
    {
        super("EntitySpawn", 1);
    }

    @Override
    public IWorldChangeAction createBlastForTrigger(World world, double x, double y, double z, TriggerCause triggerCause, double yieldMultiplier, NBTTagCompound tag)
    {
        if (tag != null)
        {
            int entityID = tag.getInteger("EntityID");
            return new BlastEntitySpawn(entityID);
        }
        return null;
    }

    @Override
    protected BlastSpawn newBlast(NBTTagCompound tag)
    {
        int entityID = tag.getInteger("EntityID");
        return new BlastEntitySpawn(entityID);
    }

    @Override
    public boolean doesDamageMissile(IMissileEntity entity, IMissile missile, IWarhead warhead, boolean warheadBlew, boolean engineBlew)
    {
        return engineBlew;
    }
}
