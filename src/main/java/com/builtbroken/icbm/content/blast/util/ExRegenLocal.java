package com.builtbroken.icbm.content.blast.util;

import com.builtbroken.icbm.api.blast.IBlastHandler;
import com.builtbroken.icbm.api.missile.IMissileEntity;
import com.builtbroken.icbm.api.modules.IMissile;
import com.builtbroken.icbm.api.modules.IWarhead;
import com.builtbroken.mc.framework.explosive.handler.ExplosiveData;
import com.builtbroken.mc.framework.explosive.handler.ExplosiveHandler;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/30/2016.
 */
public class ExRegenLocal extends ExplosiveHandler<BlastRegenLocal> implements IBlastHandler
{
    public ExRegenLocal(ExplosiveData data)
    {
        super(data);
    }

    @Override
    protected BlastRegenLocal newBlast()
    {
        return new BlastRegenLocal(this);
    }

    @Override
    public boolean doesDamageMissile(IMissileEntity entity, IMissile missile, IWarhead warhead, boolean warheadBlew, boolean engineBlew)
    {
        return engineBlew;
    }
}
