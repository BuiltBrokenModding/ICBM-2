package com.builtbroken.icbm.content.blast.nuke;

import com.builtbroken.icbm.api.blast.IBlastHandler;
import com.builtbroken.icbm.api.missile.IMissileEntity;
import com.builtbroken.icbm.api.modules.IMissile;
import com.builtbroken.icbm.api.modules.IWarhead;
import com.builtbroken.mc.api.event.TriggerCause;
import com.builtbroken.mc.framework.explosive.handler.ExplosiveData;
import com.builtbroken.mc.framework.explosive.handler.ExplosiveHandler;
import net.minecraft.nbt.NBTTagCompound;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/30/2016.
 */
public class ExNuke extends ExplosiveHandler<BlastNuke> implements IBlastHandler
{
    /**
     * Creates an explosive using a blast class, and name
     */
    public ExNuke(ExplosiveData data)
    {
        super(data);
    }

    @Override
    protected BlastNuke newBlast()
    {
        return new BlastNuke(this);
    }

    @Override
    protected void addData(BlastNuke blast)
    {
        super.addData(blast);
        blast.addPostTriggerExplosive("ExoThermic", blast.size * 1.3, new TriggerCause.TriggerCauseExplosion(blast.wrapperExplosion), new NBTTagCompound());
        blast.addPostTriggerExplosive("Emp", blast.size * 5, new TriggerCause.TriggerCauseExplosion(blast.wrapperExplosion), new NBTTagCompound());
        blast.addPostTriggerExplosive("Flash", blast.size * 4, new TriggerCause.TriggerCauseExplosion(blast.wrapperExplosion), new NBTTagCompound());
        blast.addPostTriggerExplosive("Radiation", blast.size * 4, new TriggerCause.TriggerCauseExplosion(blast.wrapperExplosion), new NBTTagCompound());
    }

    @Override
    public boolean doesVaporizeParts(IMissileEntity entity, IMissile missile, IWarhead warhead, boolean warheadBlew, boolean engineBlew)
    {
        return true;
    }
}
