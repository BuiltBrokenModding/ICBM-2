package com.builtbroken.icbm.content.blast.explosive;

import com.builtbroken.icbm.content.blast.ExplosiveHandlerICBM;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/30/2016.
 */
public class ExMicroQuake extends ExplosiveHandlerICBM<BlastMicroQuake>
{
    public ExMicroQuake()
    {
        super("MicroQuake", 3);
    }

    @Override
    protected BlastMicroQuake newBlast()
    {
        return new BlastMicroQuake();
    }
}
