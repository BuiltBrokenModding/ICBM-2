package com.builtbroken.icbm.content.blast.entity.slime;

import com.builtbroken.icbm.api.blast.IBlastHandler;
import com.builtbroken.icbm.api.blast.IExHandlerTileMissile;
import com.builtbroken.icbm.api.missile.IMissileEntity;
import com.builtbroken.icbm.api.modules.IMissile;
import com.builtbroken.icbm.api.modules.IWarhead;
import com.builtbroken.mc.framework.explosive.handler.ExplosiveData;
import com.builtbroken.mc.framework.explosive.handler.ExplosiveHandler;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/30/2016.
 */
public class ExSlimeRain extends ExplosiveHandler<BlastSlimeRain> implements IExHandlerTileMissile, IBlastHandler
{
    public ExSlimeRain(ExplosiveData data)
    {
        super(data);
    }

    @Override
    protected BlastSlimeRain newBlast()
    {
        return new BlastSlimeRain(this);
    }

    @Override
    public boolean doesSpawnMissileTile(IMissile missile, IMissileEntity entity)
    {
        //TODO implement small pod spawn(10, etc entities only)
        //TODO implement mob spawner version
        return true;
    }

    @Override
    public boolean doesDamageMissile(IMissileEntity entity, IMissile missile, IWarhead warhead, boolean warheadBlew, boolean engineBlew)
    {
        return engineBlew;
    }
}
