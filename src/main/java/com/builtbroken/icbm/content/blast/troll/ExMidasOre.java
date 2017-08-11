package com.builtbroken.icbm.content.blast.troll;

import com.builtbroken.icbm.api.blast.IBlastHandler;
import com.builtbroken.mc.framework.explosive.handler.ExplosiveData;
import com.builtbroken.mc.framework.explosive.handler.ExplosiveHandler;

public class ExMidasOre extends ExplosiveHandler<BlastMidasOre> implements IBlastHandler
{
    public ExMidasOre(ExplosiveData data)
    {
        super(data);
    }

    @Override
    protected BlastMidasOre newBlast()
    {
        return new BlastMidasOre(this);
    }
}