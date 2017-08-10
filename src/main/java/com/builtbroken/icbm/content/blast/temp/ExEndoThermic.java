package com.builtbroken.icbm.content.blast.temp;

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
public class ExEndoThermic extends ExplosiveHandler<BlastEndoThermic> implements IBlastHandler
{
    public ExEndoThermic(ExplosiveData data)
    {
        super(data);
    }

    @Override
    protected BlastEndoThermic newBlast()
    {
        return new BlastEndoThermic(this);
    }

    @Override
    public boolean doesDamageMissile(IMissileEntity entity, IMissile missile, IWarhead warhead, boolean warheadBlew, boolean engineBlew)
    {
        return engineBlew;
    }
}
