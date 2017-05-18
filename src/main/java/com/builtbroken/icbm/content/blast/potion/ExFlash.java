package com.builtbroken.icbm.content.blast.potion;

import com.builtbroken.icbm.content.blast.ExplosiveHandlerICBM;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/30/2016.
 */
public class ExFlash extends ExplosiveHandlerICBM<BlastFlash>
{
    /**
     * Creates an explosive using a blast class, and name
     */
    public ExFlash()
    {
        super("Flash", 1);
    }

    @Override
    protected BlastFlash newBlast()
    {
        return new BlastFlash(this);
    }
}
