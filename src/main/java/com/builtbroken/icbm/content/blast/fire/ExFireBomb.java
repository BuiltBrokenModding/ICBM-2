package com.builtbroken.icbm.content.blast.fire;

import com.builtbroken.icbm.api.blast.IBlastHandler;
import com.builtbroken.mc.framework.explosive.handler.ExplosiveData;
import com.builtbroken.mc.framework.explosive.handler.ExplosiveHandler;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/30/2016.
 */
public class ExFireBomb extends ExplosiveHandler<BlastFireBomb> implements IBlastHandler
{
    public ExFireBomb(ExplosiveData data)
    {
        super(data);
    }

    @Override
    protected BlastFireBomb newBlast()
    {
        return new BlastFireBomb(this);
    }
}
